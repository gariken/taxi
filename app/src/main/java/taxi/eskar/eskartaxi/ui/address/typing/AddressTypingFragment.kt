package taxi.eskar.eskartaxi.ui.address.typing

import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.jakewharton.rxbinding2.view.clicks
import com.jakewharton.rxbinding2.widget.textChanges
import kotlinx.android.synthetic.main.fragment_address_typing.*
import ru.terrakok.cicerone.Router
import taxi.eskar.eskartaxi.R
import taxi.eskar.eskartaxi.base.BaseFragment
import taxi.eskar.eskartaxi.data.model.Address
import taxi.eskar.eskartaxi.data.repository.address.AddressRepository
import taxi.eskar.eskartaxi.data.repository.location.LocationRepository
import taxi.eskar.eskartaxi.injection.Scopes
import toothpick.Toothpick


class AddressTypingFragment : BaseFragment(), AddressTypingView {

    companion object {
        const val ARG_RESULT_CODE = "args.result_code"

        fun newInstance(resultCode: Int): AddressTypingFragment {
            val args = Bundle()
            args.putInt(ARG_RESULT_CODE, resultCode)
            val fragment = AddressTypingFragment()
            fragment.arguments = args
            return fragment
        }
    }

    @InjectPresenter lateinit var presenter: AddressTypingPresenter

    private val adapter = SuggestionsAdapter({ _, address ->
        presenter.onSuggestionClicked(address)
    })

    private var noFavsFromFavsDialog: AlertDialog? = null

    // =============================================================================================
    //   Android
    // =============================================================================================
    override val layoutResId: Int = R.layout.fragment_address_typing

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        toolbar.setTitle(R.string.title_address_typing)
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_orange)
        toolbar.setNavigationOnClickListener { presenter.onBackClicked() }

        rv_addresses.layoutManager = LinearLayoutManager(this.context)
        rv_addresses.adapter = this.adapter

        noFavsFromFavsDialog = AlertDialog.Builder(this.context!!)
                .setMessage(R.string.dialog_addresses_typing_no_favs_from_favs_msg)
                .setPositiveButton(R.string.dialog_addresses_typing_no_favs_from_favs_btn, { _, _ -> })
                .setCancelable(true).create()
    }

    override fun onPause() {
        noFavsFromFavsDialog?.dismiss()
        this.view?.findFocus()?.let { this.hideKeyboard(it) }
        super.onPause()
    }


    // =============================================================================================
    //   View
    // =============================================================================================
    override fun bind() {
        et_address_typing_address.textChanges().skipInitialValue()
                .doOnSubscribe(presenter::unsubscribeOnDetach)
                .subscribe { presenter.onQueryChanged(it.toString()) }

        btn_address_typing_favorited.clicks()
                .doOnSubscribe(presenter::unsubscribeOnDetach)
                .subscribe { presenter.onFavoritedAddressesClicked() }

        btn_address_typing_select_on_map.clicks()
                .doOnSubscribe(presenter::unsubscribeOnDetach)
                .subscribe { presenter.onSelectOnMapClicked() }
    }

    override fun showButtonsEnabled(showFavsButton: Boolean) {
        btn_address_typing_favorited.visibility = if (showFavsButton) View.VISIBLE else View.GONE
    }

    override fun showRecents(addresses: List<Address>) {
        tv_address_typing_empty_suggestions.visibility = View.INVISIBLE
        progress_bar.visibility = View.INVISIBLE

        adapter.replaceItems(addresses)
    }

    override fun showRecentsEmpty() {
        adapter.clearItems()
        progress_bar.visibility = View.INVISIBLE
        tv_address_typing_empty_suggestions.visibility = View.VISIBLE
    }

    override fun showSuggestions(addresses: List<Address>) {
        tv_address_typing_empty_suggestions.visibility = View.INVISIBLE
        progress_bar.visibility = View.INVISIBLE
        adapter.replaceItems(addresses)
    }

    override fun showSuggestionsEmpty() {
        adapter.clearItems()
        progress_bar.visibility = View.INVISIBLE
        tv_address_typing_empty_suggestions.visibility = View.VISIBLE
    }

    override fun showLoading(show: Boolean) {
        adapter.clearItems()
        tv_address_typing_empty_suggestions.visibility = if (show) View.INVISIBLE else View.VISIBLE
        progress_bar.visibility = if (show) View.VISIBLE else View.INVISIBLE
    }

    override fun showNoFavsFromFavsAlert() {
        noFavsFromFavsDialog?.show()
    }


    // =============================================================================================
    //   Moxy
    // =============================================================================================
    @ProvidePresenter fun providePresenter(): AddressTypingPresenter {
        val addressRepository = Toothpick.openScope(Scopes.APP)
                .getInstance(AddressRepository::class.java)
        val locationRepository = Toothpick.openScope(Scopes.APP)
                .getInstance(LocationRepository::class.java)
        val resultCode = arguments?.getInt(ARG_RESULT_CODE) ?: 0
        val router = Toothpick.openScope(Scopes.APP).getInstance(Router::class.java)
        return AddressTypingPresenter(addressRepository, locationRepository, resultCode, router)
    }

}