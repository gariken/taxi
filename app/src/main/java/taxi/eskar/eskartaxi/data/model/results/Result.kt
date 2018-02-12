package taxi.eskar.eskartaxi.data.model.results

sealed class Result {
    companion object {
        fun success(): Result = Success()
        fun fail(): Result = Fail()
    }

    class Success : Result()
    class Fail : Result()
}