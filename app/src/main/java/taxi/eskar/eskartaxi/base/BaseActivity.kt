package taxi.eskar.eskartaxi.base

import com.arellomobile.mvp.MvpAppCompatActivity

abstract class BaseActivity : MvpAppCompatActivity(), BaseView {

    override fun showLoading(show: Boolean) {

    }

    override fun showSystemMessage(message: String) {

    }

    override fun hideKeyboard() {

    }

}