package taxi.eskar.eskartaxi.data.system

import android.content.Context
import android.os.Vibrator
import javax.inject.Inject

class Vibrator @Inject constructor(context: Context) {

    companion object {
        private const val LENGTH_MILLIS = 500L
    }

    private val vibrator: Vibrator? = context
            .getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

    fun vibrate() {
        vibrator?.vibrate(LENGTH_MILLIS)
    }
}