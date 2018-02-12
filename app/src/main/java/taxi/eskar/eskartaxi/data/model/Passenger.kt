package taxi.eskar.eskartaxi.data.model

import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import java.io.Serializable

data class Passenger(
        val id: Int,
        val phoneNumber: String,
        var rating: Double?,
        var name: String?,
        var surname: String?,
        var sex: String?,
        val photo: PhotoObject,
        private val favoriteAddresses: String?
) : Serializable {

    companion object {
        fun empty() = Passenger(-1, "", null, null,
                null, null, PhotoObject.empty(), null)
    }

    private var addresses: FavoriteAddresses? = null

    fun isRegistered() = (name != null && name!!.isNotBlank())
            && (surname != null && surname!!.isNotBlank())
            && (sex != null && sex!!.isNotBlank())

    fun isNotRegistered() = !isRegistered()

    fun sex() = Sex.getFor(sex)

    fun addresses(): FavoriteAddresses {
        if (addresses == null) {
            val json = favoriteAddresses ?: "{}"
            addresses = GsonBuilder()
                    .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create()
                    .fromJson<FavoriteAddresses>(if (json.isBlank()) "{}" else json, FavoriteAddresses::class.java)
        }

        return addresses!!
    }

}