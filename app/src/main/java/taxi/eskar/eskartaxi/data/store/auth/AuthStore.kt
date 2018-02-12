package taxi.eskar.eskartaxi.data.store.auth

interface AuthStore {

    fun getTokenPassenger(): String
    fun getTokenDriver(): String

    fun getIdPassenger(): Int
    fun getIdDriver(): Int
    fun getTaiffIdDriver(): Int

    fun putTokenPassenger(id: Int, token: String)
    fun putTokenDriver(id: Int, token: String)

    fun putTariffIdDriver(tariffId: Int?)

    fun containsAuthPassenger(): Boolean
    fun containsAuthDriver(): Boolean

    fun removeAuthPassenger()
    fun removeAuthDriver()

}