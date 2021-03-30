package fr.gof.promesse.services

import android.app.Activity
import android.app.AlertDialog
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.provider.Settings
import fr.gof.promesse.R

class DndManager(var context: Activity) {
    private lateinit var mNotificationManager: NotificationManager

    fun setRingMode(ringerMode: Int) {
        mNotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        changeInterruptionFiler(ringerMode)
    }

    private fun changeInterruptionFiler(interruptionFilterNone: Int) {
        if (mNotificationManager.isNotificationPolicyAccessGranted) {
            mNotificationManager.setInterruptionFilter(interruptionFilterNone)
        }
    }

    fun askPermission() {
        mNotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (!mNotificationManager.isNotificationPolicyAccessGranted) {
            AlertDialog.Builder(context)
                    .setTitle(context.getString(R.string.permissionRequired))
                    .setMessage(context.getString(R.string.permissionRequiredMessage))
                    .setPositiveButton(R.string.allow) { _, _ ->
                        val intent = Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS)
                        context.startActivity(intent)
                    }.setNegativeButton(R.string.deny, null)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show()
        }

    }
}