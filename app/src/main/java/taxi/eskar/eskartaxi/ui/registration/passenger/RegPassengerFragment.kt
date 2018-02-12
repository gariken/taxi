package taxi.eskar.eskartaxi.ui.registration.passenger

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.jakewharton.rxbinding2.view.clicks
import com.jakewharton.rxbinding2.widget.textChanges
import com.myhexaville.smartimagepicker.ImagePicker
import com.theartofdev.edmodo.cropper.CropImage
import kotlinx.android.synthetic.main.fragment_reg_passenger.*
import ru.terrakok.cicerone.Router
import taxi.eskar.eskartaxi.R
import taxi.eskar.eskartaxi.base.BaseFragment
import taxi.eskar.eskartaxi.data.model.Passenger
import taxi.eskar.eskartaxi.data.model.Sex
import taxi.eskar.eskartaxi.data.repository.profile.ProfileRepository
import taxi.eskar.eskartaxi.injection.Scopes
import taxi.eskar.eskartaxi.util.ImagePickerHelper
import taxi.eskar.eskartaxi.util.adapters.SexAdapter
import taxi.eskar.eskartaxi.util.filters.LetterFilter
import toothpick.Toothpick

class RegPassengerFragment : BaseFragment(), RegPassengerView {

    companion object {
        private const val ARG_PASSENGER = "args.passenger"
        fun newInstance(passenger: Passenger): RegPassengerFragment {
            val args = Bundle()
            args.putSerializable(ARG_PASSENGER, passenger)
            val fragment = RegPassengerFragment()
            fragment.arguments = args
            return fragment
        }
    }


    @InjectPresenter lateinit var presenter: RegPassengerPresenter

    private val adapter = SexAdapter({ pos, paymentType ->
        presenter.onSexClicked(pos, paymentType)
    })

    private var picker: ImagePicker? = null

    private var resultHandler: (uri: Uri) -> Unit = { }


    // =============================================================================================
    //   Android
    // =============================================================================================
    override val layoutResId: Int = R.layout.fragment_reg_passenger

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        toolbar.setTitle(R.string.title_reg_passenger)
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

        et_reg_passenger_name_first.filters = arrayOf(LetterFilter())
        et_reg_passenger_name_last.filters = arrayOf(LetterFilter())

        rv_sex.layoutManager = LinearLayoutManager(this.context)
        rv_sex.adapter = adapter
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

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        picker?.handlePermission(requestCode, grantResults)
    }

    override fun onPause() {
        this.view?.findFocus()?.let { this.hideKeyboard(it) }
        super.onPause()
    }

    // =============================================================================================
    //   View
    // =============================================================================================
    override fun bind() {
        et_reg_passenger_name_last.textChanges().skipInitialValue()
                .doOnSubscribe(presenter::unsubscribeOnDetach)
                .map { it.toString() }
                .subscribe { presenter.onLastNameChanged(it) }
        et_reg_passenger_name_first.textChanges().skipInitialValue()
                .doOnSubscribe(presenter::unsubscribeOnDetach)
                .map { it.toString() }
                .subscribe { presenter.onFirstNameChanged(it) }

        btn_reg_passenger_upload_photo.clicks()
                .doOnSubscribe(presenter::unsubscribeOnDetach)
                .subscribe { presenter.onUploadPhotoClicked() }

        btn_reg_passenger_save.clicks()
                .doOnSubscribe(presenter::unsubscribeOnDetach)
                .subscribe { presenter.onSaveClicked() }
    }

    override fun showAllSex(sexList: List<Sex>) {
        adapter.replaceItems(sexList)
    }

    override fun showPassenger(passenger: Passenger) {
        et_reg_passenger_name_first.setText(passenger.name)
        et_reg_passenger_name_last.setText(passenger.surname)

        adapter.setSelected(Sex.getFor(passenger.sex).id)

        if (passenger.photo.exists()) this.showPhotoUploaded()
    }

    override fun setSelectedSex(pos: Int, sex: Sex) {
        adapter.setSelected(pos)
    }

    override fun showPhotoPicker() {
        resultHandler = { presenter.onPhotoResult(it) }
        ImagePickerHelper.setup(context!!, 1, 1).start(activity!!)
    }

    override fun showPhotoUploaded() {
        val color = ContextCompat.getColor(this.context!!, R.color.light_gray)
        btn_reg_passenger_upload_photo.setTextColor(color)
        btn_reg_passenger_upload_photo.isEnabled = false
    }


    // =============================================================================================
    //   Moxy
    // =============================================================================================
    @ProvidePresenter fun providePresenter(): RegPassengerPresenter {
        val profileRepository = Toothpick.openScope(Scopes.APP)
                .getInstance(ProfileRepository::class.java)
        val passenger = arguments?.getSerializable(ARG_PASSENGER) as Passenger
        val router = Toothpick.openScope(Scopes.APP).getInstance(Router::class.java)
        return RegPassengerPresenter(profileRepository, passenger, router)
    }
}