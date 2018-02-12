package taxi.eskar.eskartaxi.util

import android.content.Context
import okhttp3.logging.HttpLoggingInterceptor
import taxi.eskar.eskartaxi.BuildConfig
import taxi.eskar.eskartaxi.injection.ApplicationModule
import taxi.eskar.eskartaxi.injection.Scopes
import toothpick.Toothpick
import toothpick.configuration.Configuration

object DependencyInjection {

    fun configure(context: Context) {
        Toothpick.setConfiguration(this.getConfig())
        this.initScopes(context)
    }

    private fun getConfig(): Configuration {
        return Configuration.forDevelopment().preventMultipleRootScopes()
    }

    private fun initScopes(context: Context) {
        val appModule = ApplicationModule(context,
                BuildConfig.CLOUDPAYMENTS_PUBLIC_ID,
                BuildConfig.ESKAR_DOMAIN,
                BuildConfig.YANDEX_API_KEY,
                BuildConfig.YANDEX_URL_GEOCODER,
                HttpLoggingInterceptor.Level.BODY)

        val appScope = Toothpick.openScope(Scopes.APP)
        appScope.installModules(appModule)
    }

}