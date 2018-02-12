package taxi.eskar.eskartaxi.data.retrofit

import io.reactivex.Single
import retrofit2.http.Body
import retrofit2.http.POST
import taxi.eskar.eskartaxi.data.model.bodies.AddressSuggestionBody
import taxi.eskar.eskartaxi.data.model.responses.DadataSuggestionResponse

interface DadataApi {
    companion object {
        const val SUGGESTION_ENDPOINT = "/suggestions/api/4_1/rs/suggest/address"
    }

    @POST(SUGGESTION_ENDPOINT) fun getSuggestions(
            @Body body: AddressSuggestionBody): Single<DadataSuggestionResponse>
}