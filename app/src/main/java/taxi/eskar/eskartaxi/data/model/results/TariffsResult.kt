package taxi.eskar.eskartaxi.data.model.results

import taxi.eskar.eskartaxi.data.model.Tariff

sealed class TariffsResult {
    companion object {
        fun success(tariffs: List<Tariff>): TariffsResult = Success(tariffs)
        fun fail(throwable: Throwable): TariffsResult = Fail(throwable)
    }

    class Success(val tariffs: List<Tariff>) : TariffsResult()
    class Fail(val throwable: Throwable) : TariffsResult()
}