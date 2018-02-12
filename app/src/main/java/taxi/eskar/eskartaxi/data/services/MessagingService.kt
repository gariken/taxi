package taxi.eskar.eskartaxi.data.services

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import taxi.eskar.eskartaxi.data.Extras
import taxi.eskar.eskartaxi.data.model.Notification
import timber.log.Timber
import com.firebase.jobdispatcher.*
import taxi.eskar.eskartaxi.data.Actions
import taxi.eskar.eskartaxi.data.receivers.DefaultPushReceiver


class MessagingService : FirebaseMessagingService() {

    companion object {
        const val DEFAULT_MESSAGE = "empty"
        const val KEY_MESSAGE = "message"
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        if (message.data.isNotEmpty()) {
            val notification = Notification(message.data.getOrDefault(KEY_MESSAGE, DEFAULT_MESSAGE))
            Timber.d("Received $notification")
            this.sendBroadcast(notification)
        } else {
            Timber.d("No data received!")
        }
    }

    private fun dispatchJob(notification: Notification, messageId: String?) {
        val extras = Bundle()
        extras.putSerializable(Extras.NOTIFICATION, notification)

        val dispatcher = FirebaseJobDispatcher(GooglePlayDriver(this))
        val myJob = dispatcher.newJobBuilder()
                .setService(PushJobService::class.java)
                .setTag(PushJobService::class.java.name + messageId)
                .setRecurring(false)
                .setLifetime(Lifetime.UNTIL_NEXT_BOOT)
                .setTrigger(Trigger.executionWindow(0, 15))
                .setRetryStrategy(RetryStrategy.DEFAULT_EXPONENTIAL)
                .setExtras(extras)
                .build()
        dispatcher.mustSchedule(myJob)
    }

    private fun startIntentService(notification: Notification) {
        val intent = Intent(this, PushJobService::class.java)
        intent.putExtra(Extras.NOTIFICATION, notification)
        this.startService(intent)
    }

    private fun sendBroadcast(notification: Notification) {
        val broadcast = Intent()
        broadcast.action = Actions.BROADCAST_NOTIFICATION
        broadcast.putExtra(Extras.NOTIFICATION, notification)

        this.sendOrderedBroadcast(broadcast, null, DefaultPushReceiver(),
                null, Activity.RESULT_OK, null, null)
    }
}