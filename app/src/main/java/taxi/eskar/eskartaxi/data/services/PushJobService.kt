package taxi.eskar.eskartaxi.data.services

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.firebase.jobdispatcher.JobParameters
import com.firebase.jobdispatcher.JobService
import taxi.eskar.eskartaxi.data.Actions
import taxi.eskar.eskartaxi.data.Extras
import taxi.eskar.eskartaxi.data.model.Notification
import taxi.eskar.eskartaxi.data.receivers.DefaultPushReceiver
import timber.log.Timber


class PushJobService : JobService() {

    override fun onStopJob(job: JobParameters): Boolean {
        val notification = job.extras?.getSerializable(Extras.NOTIFICATION) as Notification?
        if (notification != null) {
            Timber.d("Received $notification")
            this.sendNotification(notification)
        } else {
            Timber.d("No notifiation found")
        }
        return false
    }

    override fun onStartJob(job: JobParameters?): Boolean {
        return false
    }

    private fun sendNotification(notification: Notification, extras: Bundle = Bundle()) {
        val broadcast = Intent()
        broadcast.action = Actions.BROADCAST_NOTIFICATION
        broadcast.putExtra(Extras.NOTIFICATION, notification)

        this.sendOrderedBroadcast(broadcast, null, DefaultPushReceiver(),
                null, Activity.RESULT_OK, null, null)
    }
}