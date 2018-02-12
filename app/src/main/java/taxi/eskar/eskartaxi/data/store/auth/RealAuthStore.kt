package taxi.eskar.eskartaxi.data.store.auth

import android.content.SharedPreferences

class RealAuthStore(
        private val prefs: SharedPreferences
) : AuthStore {

    companion object {
        private const val PREFS_AUTH_TOKEN_DRIVER = "prefs.auth_token_driver"
        private const val PREFS_ID_DRIVER = "prefs.id_driver"
        private const val PREFS_ID_DRIVER_TARIFF = "prefs.tariff_id_driver"

        private const val PREFS_AUTH_TOKEN_PASSENGER = "prefs.auth_token_passenger"
        private const val PREFS_ID_PASSENGER = "prefs.id_passenger"

        private const val PREFS_ID_DEFAULT = -1
        private const val PREFS_TOKEN_DEFAULT = ""
    }


    override fun getTokenPassenger(): String =
            prefs.getString(PREFS_AUTH_TOKEN_PASSENGER, PREFS_TOKEN_DEFAULT)

    override fun getTokenDriver(): String =
            prefs.getString(PREFS_AUTH_TOKEN_DRIVER, PREFS_TOKEN_DEFAULT)

    override fun getIdPassenger() =
            prefs.getInt(PREFS_ID_PASSENGER, PREFS_ID_DEFAULT)

    override fun getIdDriver(): Int =
            prefs.getInt(PREFS_ID_DRIVER, PREFS_ID_DEFAULT)

    override fun getTaiffIdDriver(): Int =
            prefs.getInt(PREFS_ID_DRIVER_TARIFF, -1)


    override fun putTokenPassenger(id: Int, token: String) {
        prefs.edit().apply {
            putInt(PREFS_ID_PASSENGER, id)
            putString(PREFS_AUTH_TOKEN_PASSENGER, token)
        }.apply()
    }

    override fun putTokenDriver(id: Int, token: String) {
        prefs.edit().apply {
            putInt(PREFS_ID_DRIVER, id)
            putString(PREFS_AUTH_TOKEN_DRIVER, token)
        }.apply()
    }

    override fun putTariffIdDriver(tariffId: Int?) {
        prefs.edit().apply {
            putInt(PREFS_ID_DRIVER_TARIFF, tariffId ?: -1)
        }.apply()
    }

    override fun containsAuthPassenger(): Boolean =
            prefs.contains(PREFS_ID_PASSENGER) && prefs.contains(PREFS_AUTH_TOKEN_PASSENGER)

    override fun containsAuthDriver(): Boolean =
            prefs.contains(PREFS_ID_DRIVER) && prefs.contains(PREFS_AUTH_TOKEN_DRIVER)

    override fun removeAuthPassenger() {
        prefs.edit().apply {
            remove(PREFS_AUTH_TOKEN_PASSENGER)
            remove(PREFS_ID_PASSENGER)
        }.apply()
    }

    override fun removeAuthDriver() {
        prefs.edit().apply {
            remove(PREFS_AUTH_TOKEN_DRIVER)
            remove(PREFS_ID_DRIVER)
            remove(PREFS_ID_DRIVER_TARIFF)
        }.apply()
    }
}