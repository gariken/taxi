package taxi.eskar.eskartaxi.data.model.results

sealed class AddressStringsResult {

    companion object {
        fun success(addresses: List<String>): AddressStringsResult = Success(addresses)
        fun fail(throwable: Throwable): AddressStringsResult = Fail(throwable)
    }

    class Success(val addresses: List<String>) : AddressStringsResult()
    class Fail(val throwable: Throwable) : AddressStringsResult()
}