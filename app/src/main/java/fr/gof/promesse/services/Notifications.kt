package fr.gof.promesse.services

import android.app.*
import android.app.job.JobInfo
import android.app.job.JobParameters
import android.app.job.JobScheduler
import android.app.job.JobService
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.*
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import fr.gof.promesse.MainActivity
import fr.gof.promesse.R
import fr.gof.promesse.database.PromiseDataBase
import fr.gof.promesse.model.Promise
import fr.gof.promesse.model.User
import java.util.*


/**
 * Notifications
 *
 * @constructor Create empty Notifications
 */
class Notifications : JobService() {

    private val promiseDataBase = PromiseDataBase(this)
    private val notificationId = utils.NOTIFICATION_CHANNEL_ID.toInt()
    var context : Context = this
    lateinit var listPromises : MutableList<Promise>
    lateinit var email : String
    private val oneDayInMilis: Long = 86400000L


    /**
     * Method called when the Job Service is created
     *
     * @param params is used to get the email of user to update promises
     */
    override fun onStartJob(params: JobParameters?): Boolean {
        if (params != null) {
            this.email = params.extras.getString("email").toString()
        }
        createNotificationChannel()
        updateListPromises()
        if(listPromises.size > 0){
            sendNotification(listPromises)
        }
        return true
    }

    /**
     * Method called when the Job Service finish
     *
     * @return true to restart it later
     */
    override fun onStopJob(params: JobParameters?): Boolean {
        return true
    }

    /**
     * Method called to launch the Job Service
     */
    fun scheduleJob(context: Context, user: User) {
        val serviceComponent = ComponentName(context, Notifications::class.java)
        val bundle = PersistableBundle()
        bundle.putString("email", user.email)
        val jobInbo = JobInfo.Builder(0, serviceComponent)
                //.setPersisted(true)
                .setPeriodic(oneDayInMilis) // Temps d'attente entre deux déclenchements (1 jour)
                .setExtras(bundle)
                .build()
        val jobScheduler: JobScheduler = context.getSystemService(JobScheduler::class.java)
        jobScheduler.schedule(jobInbo)
    }

    /**
     * Send notification for promises of the day
     *
     * @param promises
     */
    private fun sendNotification(promises: MutableList<Promise>){
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(context, 0, intent, 0)
        var titles = ""
        for(promise : Promise in promises){
            titles+=promise.title+" "
        }
        val notifContent = context.getString(R.string.notificationContent) + " $titles" + "aujourd'hui !"

        var builder = NotificationCompat.Builder(context, utils.NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.notification_icon)
                .setContentTitle(context.getString(R.string.notificationTitle))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setStyle(NotificationCompat.BigTextStyle()
                        .bigText(notifContent))
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
        with(NotificationManagerCompat.from(context)) {
            notify(notificationId, builder.build())
        }
    }

    /**
     * Create a notification channel to send notifications
     */
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.channel_name)
            val descriptionText = getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(utils.NOTIFICATION_CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                    getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    /**
     * Update list promises
     *
     */
    private fun updateListPromises(){
        listPromises = promiseDataBase.getAllPromisesOfTheDay(email, Date(System.currentTimeMillis())).toMutableList()
    }
}