package taxi.eskar.eskartaxi.data.model.responses

data class SuggestionsResponse(val data: Data) {
    data class Data(val result: Result) {
        data class Result(val result: Result) {
            data class Result(val predictions: List<Prediction>) {
                data class Prediction(
                        val description: String,
                        val placeId: String,
                        val terms: List<Term>
                ) {
                    data class Term(val value: String, val offset: Int)
                }
            }
        }
    }
}