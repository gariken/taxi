package taxi.eskar.eskartaxi.injection

import taxi.eskar.eskartaxi.data.model.results.BindResult
import toothpick.config.Module

class ThreeDSecureModule(result: BindResult.Success3dsE) : Module() {
    init {
        bind(BindResult.Success3dsE::class.java).toInstance(result)
    }
}