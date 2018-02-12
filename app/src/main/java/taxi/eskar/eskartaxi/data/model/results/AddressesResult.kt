package taxi.eskar.eskartaxi.data.model.results

import taxi.eskar.eskartaxi.data.model.Address

sealed class AddressesResult {

    companion object {
        fun success(addresses: List<Address>): AddressesResult = Success(addresses)
        fun fail(throwable: Throwable): AddressesResult = Fail(throwable)
    }

    class Success(val addresses: List<Address>) : AddressesResult()
    class Fail(val throwable: Throwable) : AddressesResult()
}