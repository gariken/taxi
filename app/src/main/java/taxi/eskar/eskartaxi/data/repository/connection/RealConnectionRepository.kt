package taxi.eskar.eskartaxi.data.repository.connection

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject


class RealConnectionRepository(private val context: Context) : ConnectionRepository {

    private val relay = PublishSubject.create<Boolean>()
    private val filter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(c: Context, i: Intent) {
            val cm = c.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val info = cm.activeNetworkInfo
            val connected: Boolean = info != null && info.isConnectedOrConnecting
            relay.onNext(connected)
        }
    }

    override fun register() {
        context.registerReceiver(receiver, filter)
    }

    override fun unregister() {
        context.unregisterReceiver(receiver)
    }

    override fun connectionChanges(): Observable<Boolean> {
        return relay.hide()
    }
}