package taxi.eskar.eskartaxi.ui.profile.passenger

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.view.View
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.jakewharton.rxbinding2.view.clicks
import com.squareup.picasso.Picasso
import com.theartofdev.edmodo.cropper.CropImage
import kotlinx.android.synthetic.main.fragment_profile_passenger.*
import taxi.eskar.eskartaxi.R
import taxi.eskar.eskartaxi.base.BaseFragment
import taxi.eskar.eskartaxi.data.model.Passenger
import taxi.eskar.eskartaxi.injection.Scopes
import taxi.eskar.eskartaxi.util.ImagePickerHelper
import taxi.eskar.eskartaxi.util.transformations.CircleTransformation
import toothpick.Toothpick


class ProfilePassengerFragment : BaseFragment(), ProfilePassengerView {

    companion object {
        fun newInstance() = ProfilePassengerFragment()
    }

    @InjectPresenter lateinit var presenter: ProfilePassengerPresenter

    private var photoDialog: AlertDialog? = null

    private var resultHandler: (uri: Uri) -> Unit = { }


    // =============================================================================================
    //   Android
    // =============================================================================================
    override val layoutResId: Int = R.layout.fragment_profile_passenger

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        toolbar.apply {
            setTitle(R.string.title_profile_passenger)
            setNavigationIcon(R.drawable.ic_arrow_back_orange)
            setNavigationOnClickListener { presenter.onBackClicked() }
        }

        iv_avatar.setOnClickListener { presenter.onPhotoClicked() }
        btn_profile_edit.setOnClickListener { presenter.onEditProfileClicked() }
        tv_cards.setOnClickListener { presenter.onCards() }
        tv_favorited_addresses.setOnClickListener { presenter.onFavoritedAddressesClicked() }
        tv_orders_history.setOnClickListener { presenter.onOrderHistory() }
        tv_debts.setOnClickListener { presenter.onDebtsClicked() }
        tv_share.setOnClickListener { presenter.onShareClicked() }
        btn_profile_sign_out.setOnClickListener { presenter.onSignOutClicked() }

        photoDialog = AlertDialog.Builder(this.context!!, R.style.AppTheme_AlertDialog)
                .setTitle(R.string.title_profile_passenger_dialog)
                .setMessage(R.string.message_dialog_profile_passenger)
                .setPositiveButton(R.string.pos_btn_dialog_profile_passenger_delete, { _, _ ->
                    presenter.onDeletePhotoOk()
                })
                .setNegativeButton(R.string.neg_btn_dialog_profile_passenger_delete, { _, _ -> })
                .setCancelable(true).create()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        when (requestCode) {
            CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE -> {
                val result = CropImage.getActivityResult(data)
                if (resultCode == RESULT_OK) {
                    resultHandler.invoke(result.uri)
                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    // val error = result.error
                }
            }
            else -> super.onActivityResult(requestCode, resultCode, data)
        }
    }

    override fun onPause() {
        photoDialog?.dismiss()
        super.onPause()
    }


    override fun showProfile(passenger: Passenger) {
        tv_name_last.text = passenger.surname
        tv_name_first.text = passenger.name

        if (passenger.photo.exists()) {
            Picasso.with(this.context)
                    .load(passenger.photo.thumb.url).fit().centerCrop()
                    .transform(CircleTransformation())
                    .error(R.drawable.ic_account_orange)
                    .into(iv_avatar)

        } else {
            Picasso.with(this.context)
                    .load(R.drawable.ic_account_orange)
                    .placeholder(R.drawable.ic_account_orange)
                    .error(R.drawable.ic_account_orange)
                    .into(iv_avatar)
        }
    }

    // =============================================================================================
    //   View
    // =============================================================================================

    override fun showPhotoDialog() {
        photoDialog?.show()
    }

    override fun showPhotoPicker() {
        resultHandler = { presenter.onPhotoResult(it) }
        ImagePickerHelper.setup(context!!, 1, 1).start(activity!!)
    }


    // =============================================================================================
    //   Moxy
    // =============================================================================================
    @ProvidePresenter fun providePresenter(): ProfilePassengerPresenter =
            Toothpick.openScope(Scopes.APP).getInstance(ProfilePassengerPresenter::class.java)
}