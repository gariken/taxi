package taxi.eskar.eskartaxi.ui.profileedit.driver

import android.os.Bundle
import android.view.View
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.jakewharton.rxbinding2.view.clicks
import com.jakewharton.rxbinding2.widget.textChanges
import kotlinx.android.synthetic.main.fragment_profile_driver_edit.*
import taxi.eskar.eskartaxi.R
import taxi.eskar.eskartaxi.base.BaseFragment
import taxi.eskar.eskartaxi.data.model.Driver
import taxi.eskar.eskartaxi.injection.Scopes
import taxi.eskar.eskartaxi.util.filters.LetterDigitSpaceFilter
import taxi.eskar.eskartaxi.util.filters.LetterFilter
import taxi.eskar.eskartaxi.util.filters.LicensePlateFilter
import timber.log.Timber
import toothpick.Toothpick


class ProfileDriverEditFragment : BaseFragment(), ProfileDriverEditView {

    companion object {
        fun newInstance(): ProfileDriverEditFragment {
            val args = Bundle()
            val fragment = ProfileDriverEditFragment()
            fragment.arguments = args
            return fragment
        }
    }


    @InjectPresenter lateinit var presenter: ProfileDriverEditPresenter


    // Android
    override val layoutResId: Int = R.layout.fragment_profile_driver_edit

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        toolbar.setTitle(R.string.title_profile_edit)
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_orange)
        toolbar.setNavigationOnClickListener { presenter.onBackClicked() }

        et_profile_edit_name_last.filters = arrayOf(LetterFilter())
        et_profile_edit_name_first.filters = arrayOf(LetterFilter())
        et_profile_edit_driver_car_model.filters = arrayOf(LetterDigitSpaceFilter())
        et_profile_edit_driver_license_plate.filters = arrayOf(LicensePlateFilter())
    }

    override fun onPause() {
        this.view?.findFocus()?.let { this.hideKeyboard(it) }
        super.onPause()
    }


    // View
    override fun bind() {
        et_profile_edit_name_last.textChanges().skipInitialValue()
                .doOnSubscribe(presenter::unsubscribeOnDetach)
                .map { it.toString() }
                .subscribe { presenter.onLastNameChanged(it) }

        et_profile_edit_name_first.textChanges().skipInitialValue()
                .doOnSubscribe(presenter::unsubscribeOnDetach)
                .map { it.toString() }
                .subscribe { presenter.onFirstNameChanged(it) }

        et_profile_edit_driver_car_model.textChanges().skipInitialValue()
                .doOnSubscribe(presenter::unsubscribeOnDetach)
                .map { it.toString() }
                .subscribe { presenter.onCarModelChanged(it) }

        et_profile_edit_driver_car_color.textChanges().skipInitialValue()
                .doOnSubscribe(presenter::unsubscribeOnDetach)
                .map { it.toString() }
                .subscribe { presenter.onCarColorChanged(it) }

        et_profile_edit_driver_license_plate.textChanges().skipInitialValue()
                .doOnSubscribe(presenter::unsubscribeOnDetach)
                .map { it.toString() }
                .doOnNext { Timber.i(it) }
                .subscribe { presenter.onLicensePlateChanged(it) }

        btn_profile_edit_save.clicks()
                .doOnSubscribe(presenter::unsubscribeOnDetach)
                .subscribe { presenter.onSaveClicked() }
    }

    override fun showProfile(driver: Driver) {
        et_profile_edit_name_last.setText(driver.surname)
        et_profile_edit_name_first.setText(driver.name)
        et_profile_edit_driver_car_model.setText(driver.carModel)
        et_profile_edit_driver_car_color.setText(driver.carColor)
        et_profile_edit_driver_license_plate.setText(driver.licencePlate)
    }


    @ProvidePresenter fun providePresenter(): ProfileDriverEditPresenter =
            Toothpick.openScope(Scopes.APP).getInstance(ProfileDriverEditPresenter::class.java)
}