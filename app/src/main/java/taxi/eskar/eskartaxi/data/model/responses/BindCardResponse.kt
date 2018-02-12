package taxi.eskar.eskartaxi.data.model.responses

data class BindCardResponse(val data: Data) {
    data class Data(val message: String?, val transactionId: Int?, val paReq: String?,
                    val acsUrl: String?, val termUrl: String?, val code: Int)
}