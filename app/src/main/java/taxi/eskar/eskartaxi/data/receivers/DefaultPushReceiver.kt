package taxi.eskar.eskartaxi.data.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.app.Activity
import android.widget.Toast
import taxi.eskar.eskartaxi.data.Extras
import taxi.eskar.eskartaxi.data.model.Notification
import timber.log.Timber


class DefaultPushReceiver : BroadcastReceiver() {
    override fun onReceive(ctx: Context, intent: Intent) {
        val resultCode = this.resultCode
        if (resultCode == Activity.RESULT_OK) {
            val notification = intent.getSerializableExtra(Extras.NOTIFICATION) as Notification
            Timber.d("Received notification: $notification")
            Toast.makeText(ctx, notification.message, Toast.LENGTH_SHORT).show()
        } else {

        }
    }
}