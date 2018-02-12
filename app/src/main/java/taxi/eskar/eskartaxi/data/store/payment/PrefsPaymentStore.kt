package taxi.eskar.eskartaxi.data.store.payment

import android.content.SharedPreferences
import android.content.res.Resources
import taxi.eskar.eskartaxi.R
import taxi.eskar.eskartaxi.data.model.PaymentType
import javax.inject.Inject

class PrefsPaymentStore @Inject constructor(
        private val prefs: SharedPreferences,
        private val resources: Resources
) : PaymentStore {

    private companion object {
        private const val PREFS_ID = "prefs.payment.id"
        private const val PREFS_TITLE = "prefs.payment.title"
    }

    override fun getPreferredPaymentType(): PaymentType {
        val id = prefs.getInt(PREFS_ID, 0)
        val title = prefs.getString(PREFS_TITLE, resources.getString(R.string.payment_type_cash))
        return PaymentType(id, title)
    }

    override fun putPreferredPaymentType(type: PaymentType) {
        prefs.edit()
                .putInt(PREFS_ID, type.id)
                .putString(PREFS_TITLE, type.title)
                .apply()
    }

    override fun clear() {
        prefs.edit().remove(PREFS_ID).remove(PREFS_TITLE).apply()
    }
}