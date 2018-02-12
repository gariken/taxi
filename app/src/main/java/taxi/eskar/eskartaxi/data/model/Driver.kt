package taxi.eskar.eskartaxi.data.model

import java.io.Serializable


data class Driver(
        val id: Int,
        val phoneNumber: String,
        var rating: Double?,
        var name: String?,
        var surname: String?,
        var carModel: String?,
        var licencePlate: String?,
        val confirmed: Boolean,
        val balance: Double,
        var photo: PhotoObject,
        var license: PhotoObject,
        var carColor: String?,
        var tariffId: Int?
) : Serializable {

    companion object {
        fun empty() = Driver(0, "", .0, null, null,
                null, null, false,
                .0, PhotoObject.empty(), PhotoObject.empty(), null, null)
    }

    fun isRegistered() = (name != null && name!!.isNotBlank())
            && (surname != null && surname!!.isNotBlank())
            && (carModel != null && carModel!!.isNotBlank())
            && (licencePlate != null && licencePlate!!.isNotBlank())
            && (carColor != null && carColor!!.isNotBlank())

    fun isNotRegistered() = !isRegistered()

}