package taxi.eskar.eskartaxi.data.model

import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import java.io.Serializable

data class FavoriteAddresses(
        var home: Address?, var work: Address?,
        private var others: MutableList<Address>?
) : Serializable {

    fun others(): MutableList<Address> {
        if (others == null)
            others = mutableListOf()

        return others!!
    }

    fun toJson() = GsonBuilder()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .create().toJson(this).toString()

}
