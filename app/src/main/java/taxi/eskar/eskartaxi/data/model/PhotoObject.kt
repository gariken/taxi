package taxi.eskar.eskartaxi.data.model

import java.io.Serializable

data class PhotoObject(var url: String?, var large: Photo, var thumb: Photo) : Serializable {
    data class Photo(var url: String?) : Serializable

    companion object {
        fun empty() = PhotoObject(null, Photo(null), Photo(null))
    }

    fun exists() = url != null
            && large.url != null
            && thumb.url != null
}