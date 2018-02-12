package taxi.eskar.eskartaxi.data.repository.profile

import android.net.Uri
import io.reactivex.Single
import taxi.eskar.eskartaxi.data.model.Driver
import taxi.eskar.eskartaxi.data.model.LatLon
import taxi.eskar.eskartaxi.data.model.Passenger
import taxi.eskar.eskartaxi.data.model.results.*

interface ProfileRepository {

    fun getAllSex(): Single<AllSexResult>

    // Passenger
    fun getPassenger(id: Int?): Single<PassengerResult>

    fun getPassengerMe(): Single<PassengerResult>
    fun getPassengerMeSplash(): Single<PassengerResult>
    fun updatePassenger(passenger: Passenger): Single<PassengerResult>
    fun initPassenger(latLon: LatLon): Single<PassengerResult>

    fun uploadPhotoPassenger(uri: Uri): Single<PassengerResult>
    fun deletePhotoPassenger(): Single<PassengerResult>

    // Driver
    fun getDriver(id: Int?): Single<DriverResult>

    fun getDriverMe(): Single<DriverResult>
    fun getDriverMeSplash(): Single<DriverResult>
    fun updateDriver(driver: Driver): Single<DriverResult>
    fun initDriver(latLon: LatLon): Single<DriverResult>

    fun uploadPhotoDriver(uri: Uri): Single<DriverResult>
    fun deletePhotoDriver(): Single<DriverResult>

    fun uploadLicenseDriver(uri: Uri): Single<DriverResult>

    // Auth
    fun requestSmsCodePassenger(phone: String): Single<RequestSmsResult>

    fun requestSmsCodeDriver(phone: String): Single<RequestSmsResult>

    fun confirmAuthWithCodePassenger(phone: String, code: String): Single<ConfirmAuthResult>
    fun confirmAuthWithCodeDriver(phone: String, code: String): Single<ConfirmAuthResult>

    fun hasAuthDriver(): Boolean
    fun hasAuthPassenger(): Boolean

    fun signOutPassenger(): Single<SignOutResult>
    fun signOutDriver(): Single<SignOutResult>

    fun hasAuthBoth(): Boolean
    fun clearBoth(): Single<SignOutResult>
}