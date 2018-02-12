package taxi.eskar.eskartaxi.data.model.results

import taxi.eskar.eskartaxi.data.model.Card

sealed class CardsResult {

    companion object {
        fun success(cards: List<Card>): CardsResult = Success(cards)
        fun fail(throwable: Throwable): CardsResult = Failure(throwable)
        fun unknownStatusCode(statusCode: Int): CardsResult = UnknownStatusCode(statusCode)
        fun unconfirmed(): CardsResult = Unconfirmed()
    }

    data class Success(val cards: List<Card>) : CardsResult()
    data class Failure(val throwable: Throwable) : CardsResult()
    data class UnknownStatusCode(val statusCode: Int): CardsResult()
    class Unconfirmed: CardsResult()

}