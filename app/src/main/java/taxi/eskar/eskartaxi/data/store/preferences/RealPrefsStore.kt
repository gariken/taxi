package taxi.eskar.eskartaxi.data.store.preferences

import android.content.SharedPreferences

class RealPrefsStore(private val sharedPreferences: SharedPreferences) : PrefsStore {

    companion object {
        private const val KEY_SHOULD_DRIVER_RECEIVE_NOTIFICATIONS = "prefs.should_driver_receive_notifications"
        private const val DEFAULT_SHOULD_DRIVER_RECEIVE_NOTIFICATIONS = true
    }

    override fun shouldDriverReceiveNotifications(): Boolean {
        return sharedPreferences.getBoolean(KEY_SHOULD_DRIVER_RECEIVE_NOTIFICATIONS,
                DEFAULT_SHOULD_DRIVER_RECEIVE_NOTIFICATIONS)
    }
}