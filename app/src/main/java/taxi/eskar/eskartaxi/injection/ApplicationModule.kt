package taxi.eskar.eskartaxi.injection

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Resources
import com.google.gson.FieldNamingPolicy
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.patloew.rxlocation.RxLocation
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import ru.terrakok.cicerone.Cicerone
import ru.terrakok.cicerone.NavigatorHolder
import ru.terrakok.cicerone.Router
import taxi.eskar.eskartaxi.data.actioncable.base.ACRepository
import taxi.eskar.eskartaxi.data.actioncable.base.RealACRepository
import taxi.eskar.eskartaxi.data.actioncable.orderprogresspassenger.OrderProgressPassengerCable
import taxi.eskar.eskartaxi.data.actioncable.orderprogresspassenger.RealOrderProgressPassengerCable
import taxi.eskar.eskartaxi.data.actioncable.startdriver.RealStartDriverCable
import taxi.eskar.eskartaxi.data.actioncable.startdriver.StartDriverCable
import taxi.eskar.eskartaxi.data.bus.RxBus
import taxi.eskar.eskartaxi.data.managers.FirebasePushManager
import taxi.eskar.eskartaxi.data.managers.PushManager
import taxi.eskar.eskartaxi.data.repository.address.AddressRepository
import taxi.eskar.eskartaxi.data.repository.address.RealAddressRepository
import taxi.eskar.eskartaxi.data.repository.connection.ConnectionRepository
import taxi.eskar.eskartaxi.data.repository.connection.RealConnectionRepository
import taxi.eskar.eskartaxi.data.repository.location.LocationRepository
import taxi.eskar.eskartaxi.data.repository.location.RealLocationRepository
import taxi.eskar.eskartaxi.data.repository.order.OrderRepository
import taxi.eskar.eskartaxi.data.repository.order.RealOrderRepository
import taxi.eskar.eskartaxi.data.repository.payment.RealPaymentRepository
import taxi.eskar.eskartaxi.data.repository.payment.PaymentRepository
import taxi.eskar.eskartaxi.data.repository.profile.ProfileRepository
import taxi.eskar.eskartaxi.data.repository.profile.RealProfileRepository
import taxi.eskar.eskartaxi.data.resources.AndroidMessageResource
import taxi.eskar.eskartaxi.data.resources.AndroidStringResource
import taxi.eskar.eskartaxi.data.resources.MessageResource
import taxi.eskar.eskartaxi.data.resources.StringResource
import taxi.eskar.eskartaxi.data.retrofit.EskarApi
import taxi.eskar.eskartaxi.data.retrofit.YandexApi
import taxi.eskar.eskartaxi.data.store.auth.AuthStore
import taxi.eskar.eskartaxi.data.store.auth.RealAuthStore
import taxi.eskar.eskartaxi.data.store.location.LocationStore
import taxi.eskar.eskartaxi.data.store.location.RealLocationStore
import taxi.eskar.eskartaxi.data.store.order.OrderStore
import taxi.eskar.eskartaxi.data.store.order.RealOrderStore
import taxi.eskar.eskartaxi.data.store.payment.PaymentStore
import taxi.eskar.eskartaxi.data.store.payment.PrefsPaymentStore
import taxi.eskar.eskartaxi.data.store.preferences.PrefsStore
import taxi.eskar.eskartaxi.data.store.preferences.RealPrefsStore
import taxi.eskar.eskartaxi.injection.qualifiers.CloudPaymentsPublicId
import taxi.eskar.eskartaxi.ui.order.history.driver.OrderHistoryDriverPresenter
import taxi.eskar.eskartaxi.ui.order.history.passenger.OrderHistoryPassengerPresenter
import toothpick.config.Module
import java.util.concurrent.TimeUnit

class ApplicationModule(
        context: Context,
        cloudPaymentsPublicId: String,
        eskarDomain: String,
        yandexApiKey: String, yandexUrl: String,
        logLevel: HttpLoggingInterceptor.Level) : Module() {

    companion object {
        private val CALL_ADAPTER_FACTORY = RxJava2CallAdapterFactory.create()

        private var GSON = GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .create()

        private val CONVERTER_FACTORY = GsonConverterFactory.create(GSON)

        private const val CONTENT_TYPE_JSON = "application/json; charset=utf-8"

        private const val FORMAT_JSON = "json"
        private const val HEADER_AUTH = "Authorization"
        private const val HEADER_CONTENT_TYPE = "Content-Type"

        private const val PREFS_NAME = "prefs"

        private const val TIMEOUT_MS_CONNECT = 600000L
        private const val TIMEOUT_MS_WRITE = 600000L
        private const val TIMEOUT_MS_READ = 600000L

        private const val QUERY_FORMAT = "format"
        private const val QUERY_KEY = "key"
    }

    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    private val authStore = RealAuthStore(prefs) as AuthStore
    private val locationStore = RealLocationStore(prefs) as LocationStore
    private val orderStore = RealOrderStore() as OrderStore
    private val prefsStore = RealPrefsStore(prefs) as PrefsStore

    private val rxLocation = RxLocation(context)

    private val realLocationRepository = RealLocationRepository(locationStore, rxLocation)

    init {
        val okhttpInterceptorEskar = Interceptor { chain ->
            val token = when {
                authStore.containsAuthPassenger() -> authStore.getTokenPassenger()
                authStore.containsAuthDriver() -> authStore.getTokenDriver()
                else -> null
            }

            val request = chain.request().newBuilder()
                    .addHeader(HEADER_AUTH, "Bearer $token")
                    .addHeader(HEADER_CONTENT_TYPE, CONTENT_TYPE_JSON)
                    .build()

            chain.proceed(request)
        }

        val okhttpInterceptorLogging = HttpLoggingInterceptor().apply { level = logLevel }
        val okhttpInterceptorYandex = Interceptor { chain ->
            val url = chain.request().url().newBuilder()
                    .addQueryParameter(QUERY_KEY, yandexApiKey)
                    .addQueryParameter(QUERY_FORMAT, FORMAT_JSON)
                    .build()

            chain.proceed(chain.request().newBuilder().url(url).build())
        }
        val okhttpClientEskar = OkHttpClient.Builder()
                .addInterceptor(okhttpInterceptorEskar)
                .addInterceptor(okhttpInterceptorLogging)
                .build()
        val okhttpClientYandex = OkHttpClient.Builder()
                .addInterceptor(okhttpInterceptorYandex)
                .addInterceptor(okhttpInterceptorLogging)
                .connectTimeout(TIMEOUT_MS_CONNECT, TimeUnit.MILLISECONDS)
                .writeTimeout(TIMEOUT_MS_WRITE, TimeUnit.MILLISECONDS)
                .readTimeout(TIMEOUT_MS_READ, TimeUnit.MILLISECONDS)
                .build()

        val eskarApi = Retrofit.Builder()
                .addCallAdapterFactory(CALL_ADAPTER_FACTORY)
                .addConverterFactory(CONVERTER_FACTORY)
                .baseUrl("http://$eskarDomain/")
                .client(okhttpClientEskar)
                .build().create(EskarApi::class.java)
        bind(EskarApi::class.java).toInstance(eskarApi)

        val yandexApi = Retrofit.Builder()
                .addCallAdapterFactory(CALL_ADAPTER_FACTORY)
                .addConverterFactory(CONVERTER_FACTORY)
                .baseUrl(yandexUrl)
                .client(okhttpClientYandex)
                .build().create(YandexApi::class.java)

        bind(Context::class.java).toInstance(context)
        bind(Gson::class.java).toInstance(GSON)
        bind(Resources::class.java).toInstance(context.resources)
        bind(SharedPreferences::class.java).toInstance(prefs)

        /* RxBus */
        bind(RxBus::class.java).toInstance(RxBus())

        /* Cable */
        bind(OrderProgressPassengerCable::class.java).toProviderInstance({ RealOrderProgressPassengerCable(eskarDomain, GSON) })
        bind(StartDriverCable::class.java).toProviderInstance({ RealStartDriverCable(eskarDomain, GSON) })

        /* Store */
        bind(AuthStore::class.java).toInstance(authStore)
        bind(LocationStore::class.java).toInstance(locationStore)
        bind(OrderStore::class.java).toInstance(orderStore)
        bind(PaymentStore::class.java).to(PrefsPaymentStore::class.java).singletonInScope()
        bind(PrefsStore::class.java).toInstance(prefsStore)

        /* Managers */
        val pushManager = FirebasePushManager()
        bind(PushManager::class.java).toInstance(pushManager)

        /* Resources */
        bind(MessageResource::class.java).to(AndroidMessageResource::class.java)
        bind(StringResource::class.java).to(AndroidStringResource::class.java)

        /* Repository */
        bind(ACRepository::class.java).toProviderInstance({ RealACRepository(eskarDomain, GSON) })
        bind(AddressRepository::class.java).toInstance(RealAddressRepository(eskarApi, yandexApi))
        bind(ConnectionRepository::class.java).toProviderInstance({ RealConnectionRepository(context) })
        bind(LocationRepository::class.java).toInstance(realLocationRepository)
        bind(OrderRepository::class.java).toInstance(RealOrderRepository(eskarApi, authStore, pushManager))
        bind(PaymentRepository::class.java).to(RealPaymentRepository::class.java)
        bind(ProfileRepository::class.java).toInstance(RealProfileRepository(context, eskarApi, authStore, prefsStore, pushManager))

        /* Navigation */
        val cicerone = Cicerone.create()
        bind(Router::class.java).toInstance(cicerone.router)
        bind(NavigatorHolder::class.java).toInstance(cicerone.navigatorHolder)

        /* Presenters */
        bind(OrderHistoryDriverPresenter::class.java).to(OrderHistoryDriverPresenter::class.java)
        bind(OrderHistoryPassengerPresenter::class.java).to(OrderHistoryPassengerPresenter::class.java)

        /* Primitives */
        bind(String::class.java).withName(CloudPaymentsPublicId::class.java)
                .toInstance(cloudPaymentsPublicId)
    }
}