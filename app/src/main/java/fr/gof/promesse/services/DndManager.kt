package fr.gof.promesse.services

import android.app.Activity
import android.app.AlertDialog
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.provider.Settings
import fr.gof.promesse.R

/**
 * Dnd manager
 *
 * @property context
 */
class DndManager(var context: Activity) {
    private lateinit var mNotificationManager: NotificationManager

    /**
     * Set ring mode that enable or disable the do not disturb mod.
     *
     * @param ringerMode
     *
     * Permet d'activer ou désactiver le mode ne pas déranger.
     */
    fun setRingMode(ringerMode: Int) {
        mNotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        changeInterruptionFiler(ringerMode)
    }

    /**
     * Change interruption filer
     *
     * @param interruptionFilterNone
     */
    private fun changeInterruptionFiler(interruptionFilterNone: Int) {
        if (mNotificationManager.isNotificationPolicyAccessGranted) {
            mNotificationManager.setInterruptionFilter(interruptionFilterNone)
        }
    }

    /**
     * Ask permission that send a pop-up to ask permissions to enable o disable do not disturb mod.
     *
     * Affiche une pop-up pour demander la permission de gérer le mode ne pas déranger.
     *
     */
    fun askPermission() {
        mNotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
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