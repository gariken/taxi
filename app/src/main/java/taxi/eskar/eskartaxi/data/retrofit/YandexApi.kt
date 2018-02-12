package taxi.eskar.eskartaxi.data.retrofit

import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query
import taxi.eskar.eskartaxi.data.model.responses.YandexSuggestionResponse

interface YandexApi {

    @GET("/1.x/") fun getSuggestions(
            @Query("geocode") query: String,
            @Query("ll") lonlat: String?,
            @Query("results") results: Int): Single<YandexSuggestionResponse>
}