package taxi.eskar.eskartaxi.data.model.responses

import com.google.gson.annotations.SerializedName

data class YandexSuggestionResponse(val response: Response) {
    data class Response(@SerializedName("GeoObjectCollection") val geoObjectCollection: GeoObjectCollection) {
        data class GeoObjectCollection(@SerializedName("metaDataProperty") val metaDataProperty: MetaDataProperty, @SerializedName("featureMember") val featureMember: List<FeatureMember>) {
            data class MetaDataProperty(@SerializedName("GeocoderResponseMetaData") val geocoderResponseMetaData: GeocoderResponseMetaData) {
                data class GeocoderResponseMetaData(val request: String, val found: String, val results: String, @SerializedName("boundedBy") val boundedBy: BoundedBy)
            }

            data class FeatureMember(@SerializedName("GeoObject") val geoObject: GeoObject) {
                data class GeoObject(val name: String, val description: String, @SerializedName("boundedBy") val boundedBy: BoundedBy, @SerializedName("metaDataProperty") val metadataProperty: MetadataProperty, @SerializedName("Point") val point: Point) {
                    data class MetadataProperty(@SerializedName("GeocoderMetaData") val geocoderMetaData: GeocoderMetaData) {
                        data class GeocoderMetaData(val kind: String, val text: String, val precision: String, @SerializedName("Address") val address: Address, @SerializedName("AddressDetails") val addressDetails: AddressDetails) {
                            data class Address(val countryCode: String, val formatted: String, @SerializedName("Components") val components: List<Component>) {
                                data class Component(val kind: String, val name: String)
                            }

                            data class AddressDetails(@SerializedName("Country") val country: Country) {
                                data class Country(@SerializedName("AddressLine") val addressLine: String, @SerializedName("CountryNameCode") val countryNameCode: String, @SerializedName("CountryName") val countryName: String)
                            }
                        }
                    }

                    data class Point(val pos: String)
                }
            }
        }
    }
}

data class BoundedBy(@SerializedName("Envelope") val envelope: Envelope) {
    data class Envelope(@SerializedName("lowerCorner") val lowerCorner: String, @SerializedName("upperCorner") val upperCorner: String)
}