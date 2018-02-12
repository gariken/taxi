package taxi.eskar.eskartaxi.ui.start.passenger

import android.support.design.widget.Snackbar
import android.support.v7.app.AlertDialog
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.jakewharton.rxbinding2.view.clicks
import kotlinx.android.synthetic.main.fragment_start_passenger.*
import taxi.eskar.eskartaxi.R
import taxi.eskar.eskartaxi.base.BaseFragment
import taxi.eskar.eskartaxi.data.model.Address
import taxi.eskar.eskartaxi.injection.Scopes
import taxi.eskar.eskartaxi.ui.Screens
import taxi.eskar.eskartaxi.ui.order.setup.passenger.OrderSetupFragment
import toothpick.Toothpick

class StartPassengerFragment : BaseFragment(), StartPassengerView {

    companion object {
        fun newInstance() = StartPassengerFragment()
        const val DURATION_CAMERA_ANIM = 2000
    }


    @InjectPresenter lateinit var presenter: StartPassengerPresenter


    private val orderDetailsFragment = OrderSetupFragment.newInstance()


    // =============================================================================================
    //   Android
    // =============================================================================================

    override val layoutResId: Int = R.layout.fragment_start_passenger


    // =============================================================================================
    //   View
    // =============================================================================================

    override fun bind() {
        card_to.clicks()
                .doOnSubscribe(presenter::unsubscribeOnDetach)
                .subscribe { presenter.onWhereToClicked() }

        card_profile.clicks()
                .doOnSubscribe(presenter::unsubscribeOnDetach)
                .subscribe { presenter.onProfileClicked() }

        card_location.clicks()
                .doOnSubscribe(presenter::unsubscribeOnDetach)
                .subscribe { presenter.onLocationClicked() }
    }

    override fun showAddressFrom(address: Address) {
        tv_address_from.text = address.subtitle
    }

    override fun showLoading(show: Boolean) {
        if (show) {
            iv_address_from.visibility = INVISIBLE
            progress_bar.visibility = VISIBLE
        } else {
            progress_bar.visibility = INVISIBLE
            iv_address_from.visibility = VISIBLE
        }
    }

    override fun showOrderDetails() {
        val transaction = fragmentManager?.beginTransaction()
                ?.addToBackStack(Screens.ORDER_SETUP_PASSENGER)
        orderDetailsFragment.show(transaction, Screens.ORDER_SETUP_PASSENGER)
    }

    override fun showSystemMessage(message: String) {
        val context = this.context ?: return

        AlertDialog.Builder(context, R.style.AppTheme_AlertDialog)
                .setMessage(message)
                .setPositiveButton(R.string.text_ok, { _, _ -> })
                .show()
    }


    // =============================================================================================
    //   Private
    // =============================================================================================


    // =============================================================================================
    //   Moxy
    // =============================================================================================

    @ProvidePresenter fun providePresenter(): StartPassengerPresenter =
            Toothpick.openScope(Scopes.APP).getInstance(StartPassengerPresenter::class.java)
}