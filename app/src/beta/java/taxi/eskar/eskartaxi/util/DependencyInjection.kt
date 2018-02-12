package taxi.eskar.eskartaxi.util

import android.content.Context
import okhttp3.logging.HttpLoggingInterceptor
import taxi.eskar.eskartaxi.BuildConfig
import taxi.eskar.eskartaxi.injection.ApplicationModule
import taxi.eskar.eskartaxi.injection.Scopes
import toothpick.Toothpick
import toothpick.configuration.Configuration
import toothpick.registries.FactoryRegistryLocator
import toothpick.registries.MemberInjectorRegistryLocator

object DependencyInjection {

    fun configure(context: Context) {
        Toothpick.setConfiguration(this.getConfig())
        FactoryRegistryLocator.setRootRegistry(taxi.eskar.eskartaxi.FactoryRegistry())
        MemberInjectorRegistryLocator.setRootRegistry(taxi.eskar.eskartaxi.MemberInjectorRegistry())
        this.initScopes(context)
    }

    private fun getConfig(): Configuration {
        return Configuration.forProduction().disableReflection()
    }

    private fun initScopes(context: Context) {
        val appModule = ApplicationModule(context,
                BuildConfig.ESKAR_DOMAIN,
                BuildConfig.YANDEX_API_KEY,
                BuildConfig.YANDEX_URL_GEOCODER,
                HttpLoggingInterceptor.Level.NONE)

        val appScope = Toothpick.openScope(Scopes.APP)
        appScope.installModules(appModule)
    }
}