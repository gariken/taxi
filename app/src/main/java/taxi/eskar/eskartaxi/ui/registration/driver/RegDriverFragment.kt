package taxi.eskar.eskartaxi.ui.registration.driver

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.jakewharton.rxbinding2.view.clicks
import com.jakewharton.rxbinding2.widget.textChanges
import com.theartofdev.edmodo.cropper.CropImage
import kotlinx.android.synthetic.main.fragment_reg_driver.*
import ru.terrakok.cicerone.Router
import taxi.eskar.eskartaxi.R
import taxi.eskar.eskartaxi.base.BaseFragment
import taxi.eskar.eskartaxi.data.model.Driver
import taxi.eskar.eskartaxi.data.repository.profile.ProfileRepository
import taxi.eskar.eskartaxi.injection.Scopes
import taxi.eskar.eskartaxi.util.ImagePickerHelper
import taxi.eskar.eskartaxi.util.filters.LetterDigitSpaceFilter
import taxi.eskar.eskartaxi.util.filters.LetterFilter
import taxi.eskar.eskartaxi.util.filters.LicensePlateFilter
import toothpick.Toothpick

class RegDriverFragment : BaseFragment(), RegDriverView {

    companion object {
        private const val ARG_DRIVER = "args.driver"
        fun newInstance(driver: Driver): RegDriverFragment {
            val args = Bundle()
            args.putSerializable(ARG_DRIVER, driver)

            val fragment = RegDriverFragment()
            fragment.arguments = args
            return fragment
        }
    }

    @InjectPresenter lateinit var presenter: RegDriverPresenter

    private var resultHandler: (uri: Uri) -> Unit = { }

    // =============================================================================================
    //   Android
    // =============================================================================================
    override val layoutResId: Int = R.layout.fragment_reg_driver

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        toolbar.setTitle(R.string.title_reg_driver)
        toolbar.inflateMenu(R.menu.fragment_registration)
        toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.action_sign_out -> {
                    presenter.onSignOutClicked()
                    true
                }
                else -> false
            }
        }

        et_reg_driver_name_first.filters = arrayOf(LetterFilter())
        et_reg_driver_name_last.filters = arrayOf(LetterFilter())
        et_reg_driver_car_model.filters = arrayOf(LetterDigitSpaceFilter())
        et_reg_driver_license_plate.filters = arrayOf(LicensePlateFilter())
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
        this.view?.findFocus()?.let { this.hideKeyboard(it) }
        super.onPause()
    }


    // =============================================================================================
    //   View
    // =============================================================================================
    override fun bind() {
        et_reg_driver_name_last.textChanges().skipInitialValue()
                .doOnSubscribe(presenter::unsubscribeOnDetach)
                .map { it.toString() }
                .subscribe { presenter.onLastNameChanged(it) }
        et_reg_driver_name_first.textChanges().skipInitialValue()
                .doOnSubscribe(presenter::unsubscribeOnDetach)
                .map { it.toString() }
                .subscribe { presenter.onFirstNameChanged(it) }

        // btn_reg_driver_upload_photo.clicks()
        //         .doOnSubscribe(presenter::unsubscribeOnDetach)
        //         .subscribe { presenter.onUploadPhotoClicked() }

        et_reg_driver_car_model.textChanges().skipInitialValue()
                .doOnSubscribe(presenter::unsubscribeOnDetach)
                .map { it.toString() }
                .subscribe { presenter.onCarModelChanged(it) }

        et_reg_driver_car_color.textChanges().skipInitialValue()
                .doOnSubscribe(presenter::unsubscribeOnDetach)
                .map { it.toString() }
                .subscribe { presenter.onCarColorChanged(it) }

        et_reg_driver_license_plate.textChanges().skipInitialValue()
                .doOnSubscribe(presenter::unsubscribeOnDetach)
                .map { it.toString() }
                .subscribe { presenter.onLicensePlateChaged(it) }

        // btn_reg_driver_upload_license.clicks()
        //         .doOnSubscribe(presenter::unsubscribeOnDetach)
        //         .subscribe { presenter.onUploadLicenseClicked() }

        btn_reg_driver_save.clicks()
                .doOnSubscribe(presenter::unsubscribeOnDetach)
                .subscribe { presenter.onSaveClicked() }
    }

    override fun showDriver(driver: Driver) {
        et_reg_driver_name_first.setText(driver.name)
        et_reg_driver_name_last.setText(driver.surname)
        et_reg_driver_car_model.setText(driver.carModel)
        et_reg_driver_car_color.setText(driver.carColor)
        et_reg_driver_license_plate.setText(driver.licencePlate)

        if (driver.photo.exists()) this.showPhotoUploaded()
        if (driver.license.exists()) this.showLicenseUploaded()
    }

    override fun showPhotoPicker() {
        resultHandler = { presenter.onPhotoResult(it) }
        ImagePickerHelper.setup(context!!, 1, 1).start(activity!!)
    }

    override fun showPhotoUploaded() {
        // val color = ContextCompat.getColor(this.context!!, R.color.light_gray)
        // btn_reg_driver_upload_photo.setTextColor(color)
        // btn_reg_driver_upload_photo.isEnabled = false
    }

    override fun showLicensePicker() {
        resultHandler = { presenter.onLicenseResult(it) }
        ImagePickerHelper.setup(context!!).start(activity!!)
    }

    override fun showLicenseUploaded() {
        // val color = ContextCompat.getColor(this.context!!, R.color.light_gray)
        // btn_reg_driver_upload_license.setTextColor(color)
        // btn_reg_driver_upload_license.isEnabled = false
    }


    // =============================================================================================
    //   Moxy
    // =============================================================================================
    @ProvidePresenter fun providePresenter(): RegDriverPresenter {
        val profileRepository = Toothpick.openScope(Scopes.APP)
                .getInstance(ProfileRepository::class.java)
        val driver = arguments?.getSerializable(ARG_DRIVER) as Driver
        val router = Toothpick.openScope(Scopes.APP).getInstance(Router::class.java)
        return RegDriverPresenter(profileRepository, driver, router)
    }
}