package taxi.eskar.eskartaxi.util

import android.content.Context
import android.util.Log.ERROR
import com.crashlytics.android.Crashlytics
import com.crashlytics.android.core.CrashlyticsCore
import io.fabric.sdk.android.Fabric
import taxi.eskar.eskartaxi.BuildConfig
import timber.log.Timber

class Tree(context: Context) : Timber.Tree() {

    init {
        Fabric.with(context, Crashlytics())
    }

    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        if (priority == ERROR) t?.let { Crashlytics.logException(it) }
    }
}