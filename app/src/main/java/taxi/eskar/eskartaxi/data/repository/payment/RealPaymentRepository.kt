package taxi.eskar.eskartaxi.data.repository.payment

import android.content.res.Resources
import io.reactivex.Single
import ru.cloudpayments.sdk.CardFactory
import taxi.eskar.eskartaxi.R
import taxi.eskar.eskartaxi.data.Codes
import taxi.eskar.eskartaxi.data.model.Card
import taxi.eskar.eskartaxi.data.model.CardInfo
import taxi.eskar.eskartaxi.data.model.PaymentType
import taxi.eskar.eskartaxi.data.model.responses.BindCardResponse
import taxi.eskar.eskartaxi.data.model.responses.CloseDebtResponse
import taxi.eskar.eskartaxi.data.model.results.*
import taxi.eskar.eskartaxi.data.retrofit.EskarApi
import taxi.eskar.eskartaxi.data.store.auth.AuthStore
import taxi.eskar.eskartaxi.injection.qualifiers.CloudPaymentsPublicId
import taxi.eskar.eskartaxi.util.orZero
import timber.log.Timber
import javax.inject.Inject

class RealPaymentRepository @Inject constructor(
        private val authStore: AuthStore,
        private val eskarApi: EskarApi,
        private val resources: Resources,
        @CloudPaymentsPublicId private val publicKey: String
) : PaymentRepository {

    val list = mutableListOf<PaymentType>()

    init {
        list.add(PaymentType(-1, "Наличный расчет"))
    }

    override fun getCashPaymentType(): PaymentType =
            PaymentType(0, resources.getString(R.string.payment_type_cash))

    override fun getPaymentTypes(): Single<PaymentsResult> {
        return Single.just(list)
                .map { PaymentsResult.success(it) }
                .onErrorReturn { PaymentsResult.fail(it) }
    }

    override fun setPaymentType(type: PaymentType): Single<PaymentResult> {
        return Single.just(type)
                .doOnSuccess { type.active = true }
                .map { PaymentResult.success(it) }
                .onErrorReturn { PaymentResult.fail(it) }
    }

    // region cards

    override fun getCards(): Single<CardsResult> {
        return eskarApi.getCards(authStore.getIdPassenger())
                .map { CardsResult.success(it.data.cards) }
                .onErrorReturn { CardsResult.fail(it) }
    }

    // endregion

    // region binding

    override fun bindCard(cardInfo: CardInfo): Single<BindResult> {
        return try {
            val cryptogram = CardFactory.create(cardInfo.number, cardInfo.expiration, cardInfo.cvv)
                    .cardCryptogram(publicKey)
            eskarApi.bindCard(authStore.getIdPassenger(), cardInfo.owner, cryptogram)
                    .map(this::mapResponseToResult).onErrorReturn { BindResult.failure(it) }
                    .doOnSuccess { Timber.i(it.toString()) }
        } catch (e: Exception) {
            Single.just(BindResult.failure(e))
        }
    }

    override fun mapResponseToResult(response: BindCardResponse): BindResult {
        Timber.i(response.toString())
        return when (response.data.code) {
            Codes.SUCCESS -> BindResult.success3dsD(
                    response.data.message ?: "", response.data.code)
            Codes.THREE_D_SECURE -> BindResult.success3dsE(response.data.message.orEmpty(),
                    response.data.transactionId.orZero(), response.data.paReq.orEmpty(),
                    response.data.acsUrl.orEmpty(), response.data.termUrl.orEmpty())
            else -> BindResult.code(response.data.code)
        }
    }

    override fun unbindCard(card: Card): Single<UnbindResult> {
        return eskarApi.unbindCard(authStore.getIdPassenger(), card.id)
                .map { UnbindResult.success(card) }
                .onErrorReturn(UnbindResult.Companion::failure)
    }

    // endregion

    // region debts

    override fun getDebt(): Single<DebtResult> {
        return eskarApi.getDebt(authStore.getIdPassenger())
                .map { DebtResult.success(it.data.debt) }
                .onErrorReturn(DebtResult.Companion::failure)
    }

    override fun closeDebt(card: Card): Single<CloseDebtResult> {
        return eskarApi.closeDebt(authStore.getIdPassenger(), card.id)
                .map(this::mapToCloseDebtResult)
                .onErrorReturn(CloseDebtResult.Companion::failure)
    }

    private fun mapToCloseDebtResult(response: CloseDebtResponse): CloseDebtResult {
        val code = response.data.code
        return when (code) {
            0 -> CloseDebtResult.success()
            else -> CloseDebtResult.code(code)
        }
    }
}