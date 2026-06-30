package com.foco.app

import android.app.AlertDialog
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
                .setMessage("Vai bloquear \"$pkg\" por $days dias. Você poderá encerrar antes, mas com uma etapa de confirmação proposital pra te ajudar a manter o compromisso. Continuar?")
                .setPositiveButton("Sim, iniciar") { _, _ ->
                    LockManager.startLock(this, pkg, days)
                    updateStatus()
                }
                .setNegativeButton("Cancelar", null)
                .show()
        }

        findViewById<Button>(R.id.stopButton).setOnClickListener {
            confirmEarlyStop()
        }

        updateStatus()
    }

    override fun onResume() {
        super.onResume()
        updateStatus()
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

    /**
     * Fricção honesta: o usuário PODE sempre encerrar o bloqueio (é o
     * celular dele), mas precisa confirmar digitando uma frase, o que
     * adiciona um momento de reflexão antes de desistir do compromisso.
     */
    private fun confirmEarlyStop() {
        if (!LockManager.isActive(this)) {
            Toast.makeText(this, "Não há bloqueio ativo.", Toast.LENGTH_SHORT).show()
            return
        }

        val confirmPhrase = "quero desistir"
        val input = EditText(this)
        input.hint = "Digite: $confirmPhrase"

        AlertDialog.Builder(this)
            .setTitle("Tem certeza?")
            .setMessage("Pra encerrar o bloqueio antes do prazo, digite a frase abaixo exatamente:\n\"$confirmPhrase\"")
            .setView(input)
            .setPositiveButton("Confirmar") { _, _ ->
                if (input.text.toString().trim().equals(confirmPhrase, ignoreCase = true)) {
                    LockManager.stopLock(this)
                    updateStatus()
                    Toast.makeText(this, "Bloqueio encerrado.", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Frase incorreta. Bloqueio continua ativo.", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Voltar", null)
            .show()
    }
}
