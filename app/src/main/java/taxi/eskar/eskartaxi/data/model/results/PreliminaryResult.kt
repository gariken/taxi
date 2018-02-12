package taxi.eskar.eskartaxi.data.model.results

import taxi.eskar.eskartaxi.data.model.Tariff

sealed class PreliminaryResult {
    companion object {
        fun success(distance: Double, tariffs: List<Tariff>): PreliminaryResult =
                Success(distance, tariffs)

        fun fail(throwable: Throwable): PreliminaryResult = Fail(throwable)
    }

    class Success(val distance: Double, val tariffs: List<Tariff>) : PreliminaryResult()

    class Fail(val throwable: Throwable) : PreliminaryResult()
}