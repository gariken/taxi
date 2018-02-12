package taxi.eskar.eskartaxi.ui.profile.driver

import android.app.Activity
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
import kotlinx.android.synthetic.main.fragment_profile_driver.*
import taxi.eskar.eskartaxi.R
import taxi.eskar.eskartaxi.base.BaseFragment
import taxi.eskar.eskartaxi.data.model.Driver
import taxi.eskar.eskartaxi.injection.Scopes
import taxi.eskar.eskartaxi.util.ImagePickerHelper
import taxi.eskar.eskartaxi.util.transformations.CircleTransformation
import toothpick.Toothpick

class ProfileDriverFragment : BaseFragment(), ProfileDriverView {

    companion object {
        fun newInstance() = ProfileDriverFragment()
    }

    @InjectPresenter lateinit var presenter: ProfileDriverPresenter

    private var photoDialog: AlertDialog? = null

    private var resultHandler: (uri: Uri) -> Unit = { }

    // Android
    override val layoutResId: Int = R.layout.fragment_profile_driver

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        toolbar.setTitle(R.string.title_profile_driver)
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_orange)
        toolbar.setNavigationOnClickListener { presenter.onBackClicked() }

        tv_share.setOnClickListener { presenter.onShareClicked() }

        photoDialog = AlertDialog.Builder(this.context!!, R.style.AppTheme_AlertDialog)
                .setTitle(R.string.title_profile_passenger_dialog)
                .setMessage(R.string.message_dialog_profile_passenger)
                .setPositiveButton(R.string.pos_btn_dialog_profile_passenger_delete, { _, _ ->
                    presenter.onDeletePhotoClicked()
                })
                .setNegativeButton(R.string.neg_btn_dialog_profile_driver_delete, { _, _ -> })
                .setCancelable(false).create()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        when (requestCode) {
            CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE -> {
                val result = CropImage.getActivityResult(data)
                if (resultCode == Activity.RESULT_OK) {
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


    // View
    override fun bind() {
        // Driver is not able to change photo
        // iv_avatar.clicks()
        //         .doOnSubscribe(presenter::unsubscribeOnDetach)
        //         .subscribe { presenter.onPhotoClicked() }

        // Driver is not able to edit profile
        // btn_profile_edit.clicks()
        //         .doOnSubscribe(presenter::unsubscribeOnDetach)
        //         .subscribe { presenter.onEditProfileClicked() }

        tv_orders_history.clicks()
                .doOnSubscribe(presenter::unsubscribeOnDetach)
                .subscribe { presenter.onOrderHistory() }

        btn_profile_sign_out.clicks()
                .doOnSubscribe(presenter::unsubscribeOnDetach)
                .subscribe { presenter.onSignOutClicked() }
    }

    override fun showProfile(driver: Driver) {
        tv_name_last.text = driver.surname
        tv_name_first.text = driver.name

        val rating = driver.rating ?: 0.0
        tv_rating.text = this.getString(R.string.tv_profile_rating, rating)

        tv_balance.text = this.getString(R.string.mask_balance, driver.balance)

        if (driver.photo.exists()) {
            Picasso.with(this.context)
                    .load(driver.photo.thumb.url).fit().centerCrop()
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

    override fun showPhotoPicker() {
        resultHandler = { presenter.onPhotoResult(it) }
        ImagePickerHelper.setup(context!!, 1, 1).start(activity!!)
    }

    override fun showPhotoDialog() {
        photoDialog?.show()
    }

    // Moxy
    @ProvidePresenter fun providePresenter(): ProfileDriverPresenter =
            Toothpick.openScope(Scopes.APP).getInstance(ProfileDriverPresenter::class.java)
}