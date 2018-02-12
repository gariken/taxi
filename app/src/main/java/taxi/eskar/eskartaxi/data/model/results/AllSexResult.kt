package taxi.eskar.eskartaxi.data.model.results

import taxi.eskar.eskartaxi.data.model.Sex

sealed class AllSexResult {

    companion object {
        fun success(sexList: List<Sex>): AllSexResult = Success(sexList)
        fun fail(throwable: Throwable): AllSexResult = Fail(throwable)
    }

    class Success(val sexList: List<Sex>) : AllSexResult()
    class Fail(val throwable: Throwable) : AllSexResult()
}