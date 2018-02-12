package taxi.eskar.eskartaxi.data.model.results

import taxi.eskar.eskartaxi.data.model.Driver
import taxi.eskar.eskartaxi.data.model.Passenger

sealed class ConfirmAuthResult {

    companion object {
        fun successPassengerOld(passenger: Passenger): ConfirmAuthResult = SuccessPassengerOld(passenger)
        fun successPassengerNew(passenger: Passenger): ConfirmAuthResult = SuccessPassengerNew(passenger)

        fun successDriverOld(driver: Driver): ConfirmAuthResult = SuccessDriverOld(driver)
        fun successDriverNew(driver: Driver): ConfirmAuthResult = SuccessDriverNew(driver)

        fun fail(throwable: Throwable): ConfirmAuthResult = Fail(throwable)
    }

    class SuccessPassengerOld(val passenger: Passenger) : ConfirmAuthResult()
    class SuccessPassengerNew(val passenger: Passenger) : ConfirmAuthResult()

    class SuccessDriverOld(val driver: Driver) : ConfirmAuthResult()
    class SuccessDriverNew(val driver: Driver) : ConfirmAuthResult()

    class Fail(val throwable: Throwable) : ConfirmAuthResult()
}