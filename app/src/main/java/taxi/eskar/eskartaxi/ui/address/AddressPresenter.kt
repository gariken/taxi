package taxi.eskar.eskartaxi.ui.address

import ru.terrakok.cicerone.Router
import taxi.eskar.eskartaxi.base.BasePresenter
import javax.inject.Inject

class AddressPresenter @Inject constructor(
        router: Router
) : BasePresenter<AddressView>(router)