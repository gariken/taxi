package taxi.eskar.eskartaxi.injection

import retrofit2.Retrofit
import taxi.eskar.eskartaxi.data.retrofit.EskarApi
import toothpick.config.Module

class EskarApiModule(
        domain: String
) : Module() {

    init {
//        val eskarApi = Retrofit.Builder()
//                .addCallAdapterFactory(CALL_ADAPTER_FACTORY)
//                .addConverterFactory(CONVERTER_FACTORY)
//                .baseUrl("http://$domain/")
//                .client(okhttpClientEskar)
//                .build().create(EskarApi::class.java)
//        bind(EskarApi::class.java).toInstance(eskarApi)
    }

}