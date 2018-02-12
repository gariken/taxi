package taxi.eskar.eskartaxi.data.repository.profile

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import io.reactivex.Single
import io.reactivex.functions.BiFunction
import retrofit2.Response
import taxi.eskar.eskartaxi.data.managers.PushManager
import taxi.eskar.eskartaxi.data.model.Driver
import taxi.eskar.eskartaxi.data.model.LatLon
import taxi.eskar.eskartaxi.data.model.Passenger
import taxi.eskar.eskartaxi.data.model.Sex
import taxi.eskar.eskartaxi.data.model.responses.AuthResponseDriver
import taxi.eskar.eskartaxi.data.model.responses.AuthResponsePassenger
import taxi.eskar.eskartaxi.data.model.responses.DriverResponse
import taxi.eskar.eskartaxi.data.model.responses.PassengerResponse
import taxi.eskar.eskartaxi.data.model.results.*
import taxi.eskar.eskartaxi.data.retrofit.EskarApi
import taxi.eskar.eskartaxi.data.store.auth.AuthStore
import taxi.eskar.eskartaxi.data.store.preferences.PrefsStore
import java.io.ByteArrayOutputStream
import java.io.InputStream


class RealProfileRepository(
        private val context: Context,
        private val eskarApi: EskarApi,
        private val authStore: AuthStore,
        private val prefsStore: PrefsStore,
        private val pushManager: PushManager
) : ProfileRepository {

    companion object {
        private const val PREFERED_LENGTH = 1024
    }


    override fun getAllSex(): Single<AllSexResult> {
        return Single.just(Sex.ALL).map { AllSexResult.success(it) }
                .onErrorReturn { AllSexResult.fail(it) }
    }


    // =============================================================================================
    //  Passenger api
    // =============================================================================================

    override fun getPassenger(id: Int?): Single<PassengerResult> {
        return eskarApi.getPassenger(id)
                .map(this::responseToPassengerResult)
                .onErrorReturn { PassengerResult.failure(it) }
    }

    override fun getPassengerMe(): Single<PassengerResult> {
        return eskarApi.getPassenger(authStore.getIdPassenger())
                .map(this::responseToPassengerResult)
                .onErrorReturn { PassengerResult.failure(it) }
    }

    override fun getPassengerMeSplash(): Single<PassengerResult> {
        return eskarApi.getPassenger(authStore.getIdPassenger())
                .map(this::responseToPassengerResult)
                .onErrorReturn { PassengerResult.failure(it) }
    }


    override fun updatePassenger(passenger: Passenger): Single<PassengerResult> {
        return eskarApi
                .updatePassenger(passenger.id, passenger.name, passenger.surname,
                        passenger.sex, passenger.addresses().toJson())
                .map { PassengerResult.success(it.data.user) }
                .onErrorReturn { PassengerResult.failure(it) }
    }

    override fun initPassenger(latLon: LatLon): Single<PassengerResult> {
        return eskarApi.initPassenger(authStore.getIdPassenger(), latLon.lat, latLon.lon)
                .map(this::responseToPassengerResult)
                .onErrorReturn { PassengerResult.failure(it) }
    }

    override fun uploadPhotoPassenger(uri: Uri): Single<PassengerResult> {
        return Single
                .fromCallable { this.loadBitmapToBase64(uri) }
                .flatMap { eskarApi.uploadPhotoPassenger(authStore.getIdPassenger(), it) }
                .map { PassengerResult.success(it.data.user) }
                .onErrorReturn { PassengerResult.failure(it) }
    }

    override fun deletePhotoPassenger(): Single<PassengerResult> {
        return eskarApi.deletePhotoPassenger(authStore.getIdPassenger(), "delete")
                .map { PassengerResult.success(it.data.user) }
                .onErrorReturn { PassengerResult.failure(it) }
    }


    // =============================================================================================
    //   Driver api
    // =============================================================================================

    override fun getDriver(id: Int?): Single<DriverResult> {
        return eskarApi.getDriver(id)
                .map(this::responseToDriverResult)
                .onErrorReturn { DriverResult.failure(it) }
                .doOnSuccess(this::syncNewOrdersPushes)
    }

    override fun getDriverMe(): Single<DriverResult> {
        return eskarApi.getDriver(authStore.getIdDriver())
                .map(this::responseToDriverResult)
                .onErrorReturn { DriverResult.failure(it) }
                .doOnSuccess(this::syncNewOrdersPushes)
    }

    override fun getDriverMeSplash(): Single<DriverResult> {
        return eskarApi.getDriver(authStore.getIdDriver())
                .map(this::responseToDriverResult)
                .onErrorReturn { DriverResult.failure(it) }
                .doOnSuccess(this::syncNewOrdersPushes)
    }

    override fun updateDriver(driver: Driver): Single<DriverResult> {
        return eskarApi
                .updateDriver(driver.id, driver.name, driver.surname, driver.carModel,
                        driver.licencePlate, driver.carColor)
                .map { DriverResult.success(it.data.driver) }
                .onErrorReturn { DriverResult.failure(it) }
                .doOnSuccess(this::syncNewOrdersPushes)
    }


    override fun initDriver(latLon: LatLon): Single<DriverResult> {
        return eskarApi.initDriver(authStore.getIdDriver(), latLon.lat, latLon.lon)
                .map(this::responseToDriverResult)
                .onErrorReturn { DriverResult.failure(it) }
                .doOnSuccess(this::syncNewOrdersPushes)
    }

    override fun uploadPhotoDriver(uri: Uri): Single<DriverResult> {
        return Single
                .fromCallable { this.loadBitmapToBase64(uri) }
                .flatMap { eskarApi.uploadPhotoDriver(authStore.getIdDriver(), it) }
                .map { DriverResult.success(it.data.driver) }
                .onErrorReturn { DriverResult.failure(it) }
                .doOnSuccess(this::syncNewOrdersPushes)
    }

    override fun deletePhotoDriver(): Single<DriverResult> {
        return eskarApi.deletePhotoDriver(authStore.getIdDriver(), "delete")
                .map { DriverResult.success(it.data.driver) }
                .onErrorReturn { DriverResult.failure(it) }
                .doOnSuccess(this::syncNewOrdersPushes)
    }

    override fun uploadLicenseDriver(uri: Uri): Single<DriverResult> {
        return Single
                .fromCallable { this.loadBitmapToBase64(uri) }
                .flatMap { eskarApi.uploadLicenseDriver(authStore.getIdDriver(), it) }
                .map { DriverResult.success(it.data.driver) }
                .onErrorReturn { DriverResult.failure(it) }
                .doOnSuccess(this::syncNewOrdersPushes)
    }


    // =============================================================================================
    //   Auth api
    // =============================================================================================

    override fun requestSmsCodePassenger(phone: String): Single<RequestSmsResult> {
        return eskarApi.sendPasswordPassenger(phone)
                .map { RequestSmsResult.success(phone) }
                .onErrorReturn { RequestSmsResult.fail(it) }
    }


    override fun requestSmsCodeDriver(phone: String): Single<RequestSmsResult> {
        return eskarApi.sendPasswordDriver(phone)
                .map { RequestSmsResult.success(phone) }
                .onErrorReturn { RequestSmsResult.fail(it) }
    }


    override fun confirmAuthWithCodePassenger(phone: String, code: String): Single<ConfirmAuthResult> {
        return eskarApi.authPassenger(phone, code)
                .map(this::storeDataPassenger)
                .map {
                    if (it.isRegistered())
                        ConfirmAuthResult.successPassengerOld(it)
                    else ConfirmAuthResult.successPassengerNew(it)
                }
                .onErrorReturn { ConfirmAuthResult.fail(it) }
    }

    private fun storeDataPassenger(response: AuthResponsePassenger): Passenger {
        authStore.putTokenPassenger(response.data.user.id, response.data.authToken)
        return response.data.user
    }


    override fun confirmAuthWithCodeDriver(phone: String, code: String): Single<ConfirmAuthResult> {
        return eskarApi.authDriver(phone, code)
                .map(this::storeDataDriver)
                .map {
                    if (it.isRegistered())
                        ConfirmAuthResult.successDriverOld(it)
                    else ConfirmAuthResult.successDriverNew(it)
                }
                .onErrorReturn { ConfirmAuthResult.fail(it) }
    }

    private fun storeDataDriver(response: AuthResponseDriver): Driver {
        authStore.putTokenDriver(response.data.driver.id, response.data.authToken)
        return response.data.driver
    }


    override fun hasAuthPassenger(): Boolean = authStore.containsAuthPassenger()


    override fun hasAuthDriver(): Boolean = authStore.containsAuthDriver()


    override fun signOutPassenger(): Single<SignOutResult> = Single
            .fromCallable { authStore.removeAuthPassenger() }
            .map { SignOutResult.success() }
            .onErrorReturn { SignOutResult.fail(it) }


    override fun signOutDriver(): Single<SignOutResult> = Single
            .fromCallable { pushManager.unsubscribeFromNewOrders(authStore.getTaiffIdDriver()) }
            .doOnSuccess { authStore.removeAuthDriver() }
            .map { SignOutResult.success() }
            .onErrorReturn { SignOutResult.fail(it) }

    override fun hasAuthBoth(): Boolean =
            authStore.containsAuthDriver() && authStore.containsAuthPassenger()

    override fun clearBoth(): Single<SignOutResult> = Single
            .zip(this.signOutDriver(), this.signOutPassenger(), BiFunction { _, _ ->
                SignOutResult.success()
            })


    // region private

    private fun syncNewOrdersPushes(driverResult: DriverResult) {
        if (driverResult is DriverResult.Success) {
            this.subscribeDriverIfAllowed(driverResult.driver)
        }
    }

    private fun subscribeDriverIfAllowed(driver: Driver) {
        val oldTariffId = authStore.getTaiffIdDriver()
        if (oldTariffId != driver.tariffId) {
            pushManager.unsubscribeFromNewOrders(oldTariffId)
            authStore.putTariffIdDriver(driver.tariffId)
        }

        if (prefsStore.shouldDriverReceiveNotifications())
            pushManager.subscribeToNewOrders(driver.tariffId ?: -1)
    }

    private fun loadBitmapToBase64(uri: Uri): String {
        val options = BitmapFactory.Options().apply {
            inJustDecodeBounds = true
        }

        var inputStream: InputStream = context.contentResolver.openInputStream(uri)
        BitmapFactory.decodeStream(inputStream, null, options)

        if (options.outHeight > PREFERED_LENGTH || options.outWidth > PREFERED_LENGTH) {
            val biggestSide = Math.max(options.outHeight, options.outWidth)
            options.inSampleSize = biggestSide / PREFERED_LENGTH
        }

        options.inJustDecodeBounds = false

        inputStream = context.contentResolver.openInputStream(uri)
        val bitmap = BitmapFactory.decodeStream(inputStream, null, options)
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 50, baos)
        val byteArray = baos.toByteArray()
        return "data:image/png;base64,${Base64.encodeToString(byteArray, Base64.DEFAULT)}"
    }

    // endregion

    // region private

    private fun responseToPassengerResult(response: Response<PassengerResponse>): PassengerResult =
            when (response.code()) {
                200 -> {
                    val order = response.body()?.data?.openOrder
                    val passenger = response.body()?.data?.user ?: Passenger.empty()
                    PassengerResult.success(passenger, order)
                }
                401 -> PassengerResult.unauthorized()
                406 -> PassengerResult.unauthorized()
                410 -> PassengerResult.banned()
                else -> PassengerResult.unknownStatusCode()
            }

    private fun responseToDriverResult(response: Response<DriverResponse>): DriverResult =
            when (response.code()) {
                200 -> {
                    val order = response.body()?.data?.openOrder
                    val driver = response.body()?.data?.driver ?: Driver.empty()
                    DriverResult.success(driver, order)
                }
                401 -> DriverResult.unauthorized()
                406 -> DriverResult.unauthorized()
                410 -> DriverResult.banned()
                else -> DriverResult.unknownStatusCode()
            }

    // endregion

}