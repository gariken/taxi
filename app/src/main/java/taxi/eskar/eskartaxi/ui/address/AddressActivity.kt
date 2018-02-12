package taxi.eskar.eskartaxi.ui.address

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import ru.terrakok.cicerone.NavigatorHolder
import ru.terrakok.cicerone.android.SupportAppNavigator
import ru.terrakok.cicerone.commands.Replace
import taxi.eskar.eskartaxi.R
import taxi.eskar.eskartaxi.base.BaseActivity
import taxi.eskar.eskartaxi.injection.Scopes
import taxi.eskar.eskartaxi.ui.Screens
import taxi.eskar.eskartaxi.ui.address.selecting.AddressSelectingFragment
import taxi.eskar.eskartaxi.ui.address.typing.AddressTypingFragment
import taxi.eskar.eskartaxi.ui.favoritedaddresses.FavoritedAddressesFragment
import toothpick.Toothpick
import javax.inject.Inject

class AddressActivity : BaseActivity(), AddressView {

    companion object {
        private const val EXTRA_RESULT_CODE = "extras.result_code"

        fun newIntent(context: Context, resultCode: Int): Intent {
            val intent = Intent(context, AddressActivity::class.java)
            intent.putExtra(EXTRA_RESULT_CODE, resultCode)
            return intent
        }
    }


    @Inject lateinit var holder: NavigatorHolder

    private var resultCode: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.setContentView(R.layout.activity_address)

        Toothpick.inject(this, Toothpick.openScope(Scopes.APP))

        resultCode = intent.getIntExtra(EXTRA_RESULT_CODE, resultCode)

        if (savedInstanceState == null) {
            navigator.applyCommand(Replace(Screens.ADDRESS_TYPING, resultCode))
        }
    }

    override fun onResumeFragments() {
        super.onResumeFragments()
        holder.setNavigator(navigator)
    }

    override fun onPause() {
        holder.removeNavigator()
        super.onPause()
    }

    override fun bind() {

    }

    private val navigator = object : SupportAppNavigator(this, R.id.container_address) {

        override fun createActivityIntent(screenKey: String?, data: Any?): Intent? = null

        override fun createFragment(screenKey: String?, data: Any?): Fragment =
                when (screenKey) {
                    Screens.ADDRESS_TYPING -> AddressTypingFragment.newInstance(data as Int)
                    Screens.ADDRESS_SELECTING -> AddressSelectingFragment.newInstance(data as Int)
                    Screens.FAVORITED_ADDRESSES_PASSENGER -> FavoritedAddressesFragment.newInstance(data as Int)
                    else -> throw UnsupportedOperationException("No screen for key $screenKey")
                }
    }
}