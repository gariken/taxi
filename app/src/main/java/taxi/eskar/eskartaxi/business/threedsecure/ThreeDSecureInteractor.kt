package taxi.eskar.eskartaxi.business.threedsecure

import com.google.gson.Gson
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import taxi.eskar.eskartaxi.data.model.responses.BindCardResponse
import taxi.eskar.eskartaxi.data.model.results.BindResult
import taxi.eskar.eskartaxi.data.repository.payment.PaymentRepository
import javax.inject.Inject

class ThreeDSecureInteractor @Inject constructor(
        private val gson: Gson, private val paymentRepository: PaymentRepository
) {

    fun onResponse(json: String): Single<BindResult> {
        return Single
                .fromCallable { gson.fromJson(json, BindCardResponse::class.java) }
                .map(paymentRepository::mapResponseToResult)
                .subscribeOn(Schedulers.computation())
    }

}