package taxi.eskar.eskartaxi.data.model.responses

import com.google.gson.annotations.SerializedName

data class DadataSuggestionResponse(val suggestions: List<Suggestion>) {
    data class Suggestion(val value: String, val unrestrictedValue: String, val data: Data) {
        data class Data(val country: String?,
                        val region: String?, val regionType: String?, val regionTypeFull: String?, val regionWithType: String?,
                        val area: String?, val areaType: String?, val areaTypeFull: String?, val areaWithType: String?,
                        val city: String?, val cityType: String?, val cityTypeFull: String?, val cityWithType: String?,
                        val cityDistrict: String?, val cityDistrictType: String?, val cityDistrictTypeFull: String?, val cityDistrictWithType: String?,
                        val settlement: String?, val settlementType: String?, val settlementTypeFull: String?, val settlementWithType: String?,
                        val street: String?, val streetType: String?, val streetTypeFull: String?, val streetWithType: String?,
                        val house: String?, val houseType: String?, val houseTypeFull: String?, val houseWithType: String?,
                        val block: String?, val blockType: String?, val blockTypeFull: String?,
                        val geoLat: String?, val geoLon: String?, val qcGeo: String,
                        @SerializedName("fias_level") val fiasLevel: String, val capitalMarker: Int) {

            fun isDisplayable(): Boolean =
                    (fiasLevel == "7" || fiasLevel == "8") && geoLat != null && geoLon != null

            fun toReadableAddress(): String {
                return when (fiasLevel) {
                    "7" -> "${settlement ?: city ?: "$region, $area"}, $street"
                    "8" -> "${settlement ?: city ?: "$region, $area"}, $street, $house ${blockType ?: ""}${block ?: ""}"
                    else -> "Ошибка распознавания"
                }
            }

            fun lat() = geoLat?.toDouble() ?: .0
            fun lon() = geoLon?.toDouble() ?: .0
        }
    }
}