package taxi.eskar.eskartaxi.data.model.responses

data class DebtResponse(val data: Data) {
    data class Data(val debt: Int)
}