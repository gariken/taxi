package taxi.eskar.eskartaxi.business.splash

import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import taxi.eskar.eskartaxi.data.model.LatLon
import taxi.eskar.eskartaxi.data.model.results.DriverResult
import taxi.eskar.eskartaxi.data.model.results.PassengerResult
import taxi.eskar.eskartaxi.data.model.results.SignOutResult
import taxi.eskar.eskartaxi.data.repository.profile.ProfileRepository
import javax.inject.Inject

class SplashInteractor @Inject constructor(
        private val profileRepository: ProfileRepository
) {

    fun sync(): Single<SyncResult> =
            when {
                profileRepository.hasAuthBoth() ->
                    this.signout()
                profileRepository.hasAuthDriver() ->
                    this.initDriver()
                profileRepository.hasAuthPassenger() ->
                    this.initPassenger()
                else -> Single.just(SyncResult.none())
            }

    fun signout(): Single<SyncResult> =
            profileRepository.clearBoth().map(this::signOutResultToSyncResult)

    private fun signOutResultToSyncResult(signOutResult: SignOutResult): SyncResult =
            when (signOutResult) {
                is SignOutResult.Success -> SyncResult.none()
                is SignOutResult.Fail -> SyncResult.error(signOutResult.throwable)
            }


    // region passenger

    private fun initPassenger() =
            profileRepository.initPassenger(LatLon(.0, .0))
                    .subscribeOn(Schedulers.io())
                    .map(this::passengerResultToSyncResult)

    private fun passengerResultToSyncResult(result: PassengerResult): SyncResult =
            when (result) {
                is PassengerResult.Success -> SyncResult.passenger(result.passenger, result.order)
                is PassengerResult.Failure -> SyncResult.error(result.throwable)
                is PassengerResult.Banned -> SyncResult.banned()
                is PassengerResult.Unauthorized -> SyncResult.unauthorized()
                is PassengerResult.UnknownStatusCode -> SyncResult.unknownStatusCode()
            }

    // endregion

    // region driver

    private fun initDriver() =
            profileRepository.initDriver(LatLon(.0, .0))
                    .subscribeOn(Schedulers.io())
                    .map(this::driverResultToSyncResult)

    private fun driverResultToSyncResult(result: DriverResult): SyncResult =
            when (result) {
                is DriverResult.Success -> SyncResult.driver(result.driver, result.order)
                is DriverResult.Failure -> SyncResult.error(result.throwable)
                is DriverResult.Banned -> SyncResult.banned()
                is DriverResult.Unauthorized -> SyncResult.unauthorized()
                is DriverResult.UnknownStatusCode -> SyncResult.unknownStatusCode()
            }

    // endregion

}