package taxi.eskar.eskartaxi.data.model.responses

data class CloseDebtResponse(val data: Data) {
    data class Data(val code: Int, val message: String)
}