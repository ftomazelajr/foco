package com.foco.app

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        // Nada de especial a fazer aqui além de garantir que o estado
        // persistido em SharedPreferences continua valendo. O Accessibility
        // Service é reativado pelo próprio Android se o usuário o concedeu
        // como serviço de acessibilidade persistente.
    }
}
