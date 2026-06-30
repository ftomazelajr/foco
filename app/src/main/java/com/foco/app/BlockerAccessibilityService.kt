package com.foco.app

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.view.accessibility.AccessibilityEvent

class BlockerAccessibilityService : AccessibilityService() {

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event == null) return
        if (!LockManager.isActive(this)) return

        val target = LockManager.getTargetPackage(this)
        if (target.isEmpty()) return

        val packageName = event.packageName?.toString() ?: return
        if (packageName == target) {
            // Volta pra tela inicial em vez de abrir o app bloqueado
            val homeIntent = Intent(Intent.ACTION_MAIN)
            homeIntent.addCategory(Intent.CATEGORY_HOME)
            homeIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(homeIntent)
        }
    }

    override fun onInterrupt() {}
}
