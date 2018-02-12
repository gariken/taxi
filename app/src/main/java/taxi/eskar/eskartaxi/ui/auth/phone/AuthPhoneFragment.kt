package taxi.eskar.eskartaxi.ui.auth.phone

import android.os.Bundle
import android.view.View
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.jakewharton.rxbinding2.view.clicks
import com.redmadrobot.inputmask.MaskedTextChangedListener
import kotlinx.android.synthetic.main.fragment_auth_phone.*
import taxi.eskar.eskartaxi.R
import taxi.eskar.eskartaxi.base.BaseFragment
import taxi.eskar.eskartaxi.injection.Scopes
import toothpick.Toothpick

class AuthPhoneFragment : BaseFragment(), AuthPhoneView {

    companion object {
        private const val MASK_PHONE = "+{7} ([000]) [000]-[00]-[00]"

        fun newInstance() = AuthPhoneFragment()
    }

    @InjectPresenter lateinit var presenter: AuthPhonePresenter


    // =============================================================================================
    //   Android
    // =============================================================================================

    override val layoutResId: Int = R.layout.fragment_auth_phone

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val textChangedListener = MaskedTextChangedListener(MASK_PHONE, true,
                et_auth_phone, null, object : MaskedTextChangedListener.ValueListener {
            override fun onTextChanged(maskFilled: Boolean, extractedValue: String) {
                presenter.onPhoneChanged(maskFilled, extractedValue)
            }
        })

        et_auth_phone.addTextChangedListener(textChangedListener)
        et_auth_phone.onFocusChangeListener = textChangedListener
    }

    override fun onPause() {
        this.hideKeyboard(et_auth_phone)
        super.onPause()
    }


    // =============================================================================================
    //   View
    // =============================================================================================

    override fun bind() {
        btn_auth_phone_next_passenger.clicks().map { Unit }
                .doOnSubscribe(presenter::unsubscribeOnDetach)
                .subscribe(presenter::onNextButtonClicked)

        btn_auth_phone_next_driver.clicks().map { Unit }
                .doOnSubscribe(presenter::unsubscribeOnDetach)
                .subscribe(presenter::onNextDriverButtonClicked)
    }

    override fun showButtonsEnabled(maskFilled: Boolean) {
        btn_auth_phone_next_passenger.isEnabled = maskFilled
        btn_auth_phone_next_driver.isEnabled = maskFilled
    }


    // =============================================================================================
    //   Moxy
    // =============================================================================================

    @ProvidePresenter fun providePresenter(): AuthPhonePresenter =
            Toothpick.openScope(Scopes.APP).getInstance(AuthPhonePresenter::class.java)
}