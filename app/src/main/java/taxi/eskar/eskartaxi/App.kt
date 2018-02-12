package taxi.eskar.eskartaxi

import android.content.Context
import android.support.multidex.MultiDex
import android.support.multidex.MultiDexApplication
import com.google.firebase.FirebaseApp
import com.mapbox.mapboxsdk.Mapbox
import taxi.eskar.eskartaxi.util.DependencyInjection
import taxi.eskar.eskartaxi.util.Tree
import timber.log.Timber


class App : MultiDexApplication() {

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }

    override fun onCreate() {
        super.onCreate()

        // Firebase
        FirebaseApp.initializeApp(this)

        // Mapbox
        Mapbox.getInstance(this, BuildConfig.MAPBOX_API_KEY)

        // Timber
        Timber.plant(Tree(this))

        // Toothpick
        DependencyInjection.configure(this)
    }
}