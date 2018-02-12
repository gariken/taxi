package taxi.eskar.eskartaxi.data.resources

import android.content.res.Resources
import taxi.eskar.eskartaxi.R
import javax.inject.Inject

class AndroidStringResource @Inject constructor(
        private val resources: Resources
) : StringResource {

    override fun paymentMethod(paymentMethod: String): String =
            when (paymentMethod) {
                "cash" -> resources.getString(R.string.payment_method_cash)
                "cashless" -> resources.getString(R.string.payment_method_cashless)
                else -> resources.getString(R.string.payment_method_unknown)
            }

    override fun paymentTypeCard(lastFourNumbers: String): String =
            resources.getString(R.string.mask_payment_type_card, lastFourNumbers)
}