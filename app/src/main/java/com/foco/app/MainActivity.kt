package com.foco.app

import android.app.AlertDialog
import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var statusText: TextView
    private lateinit var packageInput: EditText
    private lateinit var daysInput: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        statusText = findViewById(R.id.statusText)
        packageInput = findViewById(R.id.packageInput)
        daysInput = findViewById(R.id.daysInput)

        findViewById<Button>(R.id.enableAccessibilityButton).setOnClickListener {
            startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
        }

        findViewById<Button>(R.id.startButton).setOnClickListener {
            val pkg = packageInput.text.toString().trim()
            val days = daysInput.text.toString().toIntOrNull()

            if (pkg.isEmpty() || days == null || days <= 0) {
                Toast.makeText(this, "Preencha o pacote e os dias corretamente", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            AlertDialog.Builder(this)
                .setTitle("Confirmar bloqueio")
                .setMessage("Vai bloquear \"$pkg\" por $days dias, sem opção de cancelar pelo app. Continuar?")
                .setPositiveButton("Sim, iniciar") { _, _ ->
                    LockManager.startLock(this, pkg, days)
                    updateStatus()
                }
                .setNegativeButton("Cancelar", null)
                .show()
        }

        findViewById<Button>(R.id.enableAdminButton).setOnClickListener {
            val devicePolicyManager = getSystemService(DEVICE_POLICY_SERVICE) as DevicePolicyManager
            val adminComponent = ComponentName(this, AdminReceiver::class.java)

            if (devicePolicyManager.isAdminActive(adminComponent)) {
                Toast.makeText(this, "Proteção já está ativa.", Toast.LENGTH_SHORT).show()
            } else {
                if (!PasswordManager.isPasswordSet(this)) {
                    promptSetPassword {
                        requestDeviceAdmin()
                    }
                } else {
                    requestDeviceAdmin()
                }
            }
        }

        findViewById<Button>(R.id.disableAdminButton).setOnClickListener {
            disableAdminFlow()
        }

        updateStatus()
    }

    override fun onResume() {
        super.onResume()
        updateStatus()
    }

    private fun requestDeviceAdmin() {
        val adminComponent = ComponentName(this, AdminReceiver::class.java)
        val intent = Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN)
        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, adminComponent)
        intent.putExtra(
            DevicePolicyManager.EXTRA_ADD_EXPLANATION,
            "Impede que o app Foco seja desinstalado sem a senha definida por quem está te ajudando a manter o compromisso."
        )
        startActivity(intent)
    }

    private fun promptSetPassword(onSet: () -> Unit) {
        val input = EditText(this)
        input.hint = "Defina a senha (quem vai guardar ela?)"

        AlertDialog.Builder(this)
            .setTitle("Defina a senha de proteção")
            .setMessage("Essa senha será necessária pra desativar a proteção contra desinstalação antes do prazo. Recomendado: deixe a pessoa de confiança (ex: sua mãe) digitar e guardar essa senha, não você.")
            .setView(input)
            .setPositiveButton("Confirmar") { _, _ ->
                val pass = input.text.toString()
                if (pass.length < 4) {
                    Toast.makeText(this, "Senha muito curta.", Toast.LENGTH_SHORT).show()
                } else {
                    PasswordManager.setPassword(this, pass)
                    Toast.makeText(this, "Senha definida.", Toast.LENGTH_SHORT).show()
                    onSet()
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun disableAdminFlow() {
        if (!PasswordManager.isPasswordSet(this)) {
            Toast.makeText(this, "Nenhuma senha foi definida ainda.", Toast.LENGTH_SHORT).show()
            return
        }

        val input = EditText(this)
        input.hint = "Senha"

        AlertDialog.Builder(this)
            .setTitle("Desativar proteção")
            .setMessage("Digite a senha definida por quem está guardando ela.")
            .setView(input)
            .setPositiveButton("Confirmar") { _, _ ->
                val pass = input.text.toString()
                if (PasswordManager.checkPassword(this, pass)) {
                    val devicePolicyManager = getSystemService(DEVICE_POLICY_SERVICE) as DevicePolicyManager
                    val adminComponent = ComponentName(this, AdminReceiver::class.java)
                    devicePolicyManager.removeActiveAdmin(adminComponent)
                    Toast.makeText(this, "Proteção desativada. Agora é possível desinstalar.", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(this, "Senha incorreta.", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun updateStatus() {
        if (LockManager.isActive(this)) {
            val target = LockManager.getTargetPackage(this)
            val days = LockManager.daysRemaining(this)
            statusText.text = "Bloqueio ativo: $target\nDias restantes: $days"
        } else {
            statusText.text = "Nenhum bloqueio ativo no momento."
        }
    }
}
