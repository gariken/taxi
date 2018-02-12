package taxi.eskar.eskartaxi.data.model.results

import taxi.eskar.eskartaxi.data.model.Card

sealed class UnbindResult {

    companion object {
        fun success(card: Card): UnbindResult = Success(card)
        fun failure(throwable: Throwable): UnbindResult = Failure(throwable)
    }

    data class Success(val removedCard: Card) : UnbindResult()
    data class Failure(val throwable: Throwable): UnbindResult()

}