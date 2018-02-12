package taxi.eskar.eskartaxi.data.resources

import android.content.res.Resources
import taxi.eskar.eskartaxi.R
import taxi.eskar.eskartaxi.data.Codes
import javax.inject.Inject

class AndroidMessageResource @Inject constructor(
        private val resources: Resources
) : MessageResource {

    override fun cardBindingSuccess(): String =
            resources.getString(R.string.message_card_binding_success)

    override fun cardBindingError(): String =
            resources.getString(R.string.error_binding)

    override fun cardBindingDataError(): String =
            resources.getString(R.string.error_binding_data)

    override fun cardUnbindingError(): String =
            resources.getString(R.string.error_unbinding)

    override fun cardsLoadingError(): String =
            resources.getString(R.string.error_cards_loading)

    override fun debtLoadingError(): String =
            resources.getString(R.string.error_debt_loading)

    override fun debtClosingError(): String =
            resources.getString(R.string.error_debt_closing)

    override fun error(code: Int): String =
            when (code) {
                Codes.NO_DEBTS ->
                    resources.getString(R.string.error_debt_closing_no_debts)
                Codes.CARD_NOT_FOUND ->
                    resources.getString(R.string.error_card_not_found)
                Codes.NO_ORDERS ->
                    resources.getString(R.string.error_orders_not_found)
                Codes.PAYMENT_NOT_FOUND ->
                    resources.getString(R.string.error_payment_not_found)
                Codes.NO_MONEY ->
                    resources.getString(R.string.error_no_money)
                Codes.HAS_DEBT ->
                        resources.getString(R.string.error_has_debt)
                else -> resources.getString(R.string.error_unknown, code)
            }

    override fun debtClosingSuccess(): String =
            resources.getString(R.string.message_debt_closing_success)

    override fun orderClosingError(): String =
            resources.getString(R.string.error_order_closing)

    override fun orderCreatingError(statusCode: Int): String =
            resources.getString(R.string.error_order_creating, statusCode)
}