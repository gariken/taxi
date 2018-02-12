package taxi.eskar.eskartaxi.ui.auth.code

import android.os.Bundle
import android.view.View
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.jakewharton.rxbinding2.view.clicks
import com.redmadrobot.inputmask.MaskedTextChangedListener
import kotlinx.android.synthetic.main.fragment_auth_code.*
import ru.terrakok.cicerone.Router
import taxi.eskar.eskartaxi.R
import taxi.eskar.eskartaxi.base.BaseFragment
import taxi.eskar.eskartaxi.data.repository.profile.ProfileRepository
import taxi.eskar.eskartaxi.injection.Scopes
import toothpick.Toothpick


class AuthCodeFragment : BaseFragment(), AuthCodeView {

    companion object {
        private const val ARG_MODE = "args.mode"
        private const val ARG_PHONE = "args.phone"

        private const val MASK_CODE = "[000099]"

        fun newInstancePassenger(phone: String): AuthCodeFragment {
            return newInstance(phone, AuthMode.PASSENGER)
        }

        fun newInstanceDriver(phone: String): AuthCodeFragment {
            return newInstance(phone, AuthMode.DRIVER)
        }

        private fun newInstance(phone: String, mode: Int): AuthCodeFragment {
            val args = Bundle()
            args.putInt(ARG_MODE, mode)
            args.putString(ARG_PHONE, phone)
            val fragment = AuthCodeFragment()
            fragment.arguments = args
            return fragment
        }
    }

    @InjectPresenter lateinit var presenter: AuthCodePresenter


    // Android
    override val layoutResId: Int = R.layout.fragment_auth_code

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        tv_auth_code_hint.text = this.getString(R.string.tv_auth_code_hint,
                arguments?.getString(ARG_PHONE, "12345678") as String)
        val textChangedListener = MaskedTextChangedListener(MASK_CODE, true,
                et_auth_code, null, object : MaskedTextChangedListener.ValueListener {
            override fun onTextChanged(maskFilled: Boolean, extractedValue: String) {
                presenter.onCodeChanged(maskFilled, extractedValue)
            }
        })

        et_auth_code.addTextChangedListener(textChangedListener)
        et_auth_code.onFocusChangeListener = textChangedListener
    }

    override fun onPause() {
        this.hideKeyboard(et_auth_code)
        super.onPause()
    }


    // View
    override fun bind() {
        btn_auth_code_next.clicks().map { Unit }
                .doOnSubscribe(presenter::unsubscribeOnDetach)
                .subscribe(presenter::onProceedButtonClicked)
    }

    override fun showButtonsEnabled(enabled: Boolean) {
        btn_auth_code_next.isEnabled = enabled
    }


    @ProvidePresenter fun providePresenter(): AuthCodePresenter {
        val profileRepository = Toothpick.openScope(Scopes.APP)
                .getInstance(ProfileRepository::class.java)
        val mode = arguments?.getInt(ARG_MODE, AuthMode.PASSENGER) as Int
        val phone = arguments?.getString(ARG_PHONE) as String
        val router = Toothpick.openScope(Scopes.APP).getInstance(Router::class.java)
        return AuthCodePresenter(mode, phone, profileRepository, router)
    }

}