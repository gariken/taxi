package taxi.eskar.eskartaxi.data.model.results

import taxi.eskar.eskartaxi.data.model.Address

sealed class AddressResult {

    companion object {
        fun success(address: Address): AddressResult = Success(address)
        fun fail(throwable: Throwable): AddressResult = Fail(throwable)
    }

    class Success(val address: Address) : AddressResult()
    class Fail(val throwable: Throwable) : AddressResult()
}