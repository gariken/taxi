package taxi.eskar.eskartaxi.ui.favoritedaddresses

import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.jakewharton.rxbinding2.view.clicks
import kotlinx.android.synthetic.main.fragment_favorited_addresses.*
import ru.terrakok.cicerone.Router
import taxi.eskar.eskartaxi.R
import taxi.eskar.eskartaxi.base.BaseFragment
import taxi.eskar.eskartaxi.data.model.Address
import taxi.eskar.eskartaxi.data.repository.profile.ProfileRepository
import taxi.eskar.eskartaxi.injection.Scopes
import toothpick.Toothpick

class FavoritedAddressesFragment : BaseFragment(), FavoritedAddressesView {

    companion object {
        const val ARG_RESULT_CODE = "args.result_code"
        fun newInstance(data: Int): FavoritedAddressesFragment {
            val args = Bundle()
            args.putSerializable(ARG_RESULT_CODE, data)
            val fragment = FavoritedAddressesFragment()
            fragment.arguments = args
            return fragment
        }
    }

    @InjectPresenter lateinit var presenter: FavoritedAddressesPresenter

    private val adapter = AddressAdapter({ _, address ->
        presenter.onAddressClicked(address)
    })

    private lateinit var readModeOnlyDialog: AlertDialog
    private lateinit var deleteOtherDialog: AlertDialog
    private lateinit var deleteHomeDialog: AlertDialog
    private lateinit var deleteWorkDialog: AlertDialog

    // =============================================================================================
    //   Android
    // =============================================================================================
    override val layoutResId: Int = R.layout.fragment_favorited_addresses

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // Toolbar
        toolbar.setTitle(R.string.title_favorited_addresses)
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_orange)
        toolbar.setNavigationOnClickListener { presenter.onBackClicked() }

        // RecyclerView
        rv_addresses.layoutManager = LinearLayoutManager(this.context)
        rv_addresses.adapter = adapter

        // Read-Only AlertDialog
        readModeOnlyDialog = AlertDialog.Builder(this.context!!, R.style.AppTheme_AlertDialog)
                .setMessage(R.string.dialog_fav_addresses_read_mode_only_msg)
                .setPositiveButton(R.string.dialog_fav_addresses_read_mode_only_pos_btn, { _, _ -> })
                .setCancelable(true).create()

        // Delete Other AlertDialog
        deleteOtherDialog = AlertDialog.Builder(this.context!!, R.style.AppTheme_AlertDialog)
                .setMessage(R.string.dialog_fav_addresses_delete_other_msg)
                .setPositiveButton(R.string.dialog_fav_addresses_delete_other_pos_btn, { _, _ -> presenter.onDeleteOtherOk() })
                .setNegativeButton(R.string.dialog_fav_addresses_delete_other_neg_btn, { _, _ -> })
                .setCancelable(true).create()

        // Delete Home AlertDialog
        deleteHomeDialog = AlertDialog.Builder(this.context!!, R.style.AppTheme_AlertDialog)
                .setMessage(R.string.dialog_fav_addresses_delete_home_msg)
                .setPositiveButton(R.string.dialog_fav_addresses_delete_home_pos_btn, { _, _ -> presenter.onDeleteHomeOk() })
                .setNegativeButton(R.string.dialog_fav_addresses_delete_home_neg_btn, { _, _ -> })
                .setCancelable(true).create()

        // Delete Work AlertDialog
        deleteWorkDialog = AlertDialog.Builder(this.context!!, R.style.AppTheme_AlertDialog)
                .setMessage(R.string.dialog_fav_addresses_delete_work_msg)
                .setPositiveButton(R.string.dialog_fav_addresses_delete_work_pos_btn, { _, _ -> presenter.onDeleteWorkOk() })
                .setNegativeButton(R.string.dialog_fav_addresses_delete_work_neg_btn, { _, _ -> })
                .setCancelable(true).create()
    }

    override fun onPause() {
        readModeOnlyDialog.dismiss()
        deleteOtherDialog.dismiss()
        deleteHomeDialog.dismiss()
        deleteWorkDialog.dismiss()
        super.onPause()
    }


    // =============================================================================================
    //   View
    // =============================================================================================
    override fun bind() {
        btn_favorited_home.clicks()
                .doOnSubscribe(presenter::unsubscribeOnDetach)
                .subscribe { presenter.onHomeClicked() }

        btn_favorited_work.clicks()
                .doOnSubscribe(presenter::unsubscribeOnDetach)
                .subscribe { presenter.onWorkClicked() }

        btn_favorited_add_new.clicks()
                .doOnSubscribe(presenter::unsubscribeOnDetach)
                .subscribe { presenter.onAddNewClicked() }
    }

    override fun showButtonsEnabled(homeEnabled: Boolean, workEnabled: Boolean,
                                    othersEnabled: Boolean, addOthersEnabled: Boolean) {
        tv_header_favorited_home.visibility = resolveVisibility(homeEnabled)
        btn_favorited_home.visibility = resolveVisibility(homeEnabled)

        tv_header_favorited_work.visibility = resolveVisibility(workEnabled)
        btn_favorited_work.visibility = resolveVisibility(workEnabled)

        tv_header_favorited_other.visibility = resolveVisibility(othersEnabled)
        rv_addresses.visibility = resolveVisibility(othersEnabled)
        btn_favorited_add_new.visibility = resolveVisibility(addOthersEnabled)

        val showEmptyState = (homeEnabled || workEnabled || othersEnabled).not()
        tv_fav_addresses_empty.visibility = resolveVisibility(showEmptyState)
    }

    override fun showHomeAddress(address: Address) {
        btn_favorited_home.text = address.title
    }

    override fun showHomeAddressEmpty() {
        btn_favorited_home.text = this.getString(R.string.msg_fav_addresses_no_home)
    }

    override fun showWorkAddress(address: Address) {
        btn_favorited_work.text = address.title
    }

    override fun showWorkAddressEmpty() {
        btn_favorited_work.text = this.getString(R.string.msg_fav_addresses_no_work)
    }

    override fun showFvrtAddresses(addresses: List<Address>) {
        adapter.replaceItems(addresses)
    }


    // =============================================================================================
    //   View - Dialogs
    // =============================================================================================
    override fun showReadModeOnlyAlert() {
        readModeOnlyDialog.show()
    }

    override fun showDeleteOtherAddressAlert() {
        deleteOtherDialog.show()
    }

    override fun showDeleteHomeAddressAlert() {
        deleteHomeDialog.show()
    }

    override fun showDeleteWorkAddressAlert() {
        deleteWorkDialog.show()
    }


    // =============================================================================================
    //   Private
    // =============================================================================================

    private fun resolveVisibility(show: Boolean): Int = if (show) View.VISIBLE else View.GONE

    // =============================================================================================
    //   Moxy
    // =============================================================================================
    @ProvidePresenter fun providePresenter(): FavoritedAddressesPresenter {
        val profileRepository = Toothpick.openScope(Scopes.APP)
                .getInstance(ProfileRepository::class.java)
        val resultCode = arguments?.getInt(ARG_RESULT_CODE, 0) as Int
        val router = Toothpick.openScope(Scopes.APP).getInstance(Router::class.java)
        return FavoritedAddressesPresenter(profileRepository, resultCode, router)
    }
}