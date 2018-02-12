package taxi.eskar.eskartaxi.base

import android.app.ProgressDialog
import android.content.Context
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.arellomobile.mvp.MvpAppCompatFragment
import taxi.eskar.eskartaxi.R


@Suppress("DEPRECATION")
abstract class BaseFragment : MvpAppCompatFragment(), BaseView {

    protected abstract val layoutResId: Int

    private lateinit var progressDialog: ProgressDialog

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        progressDialog = ProgressDialog(inflater.context, R.style.AppTheme_AlertDialog).apply {
            setCancelable(false)
            setCanceledOnTouchOutside(false)
            setMessage(this@BaseFragment.getString(R.string.dialog_progress_msg))
        }
        return inflater.inflate(layoutResId, container, false)
    }

    override fun showLoading(show: Boolean) {
        if (show) progressDialog.show()
        else progressDialog.dismiss()
    }

    override fun showSystemMessage(message: String) {

    }

    override fun hideKeyboard() {
        view?.let { this.hideKeyboard(it) }
    }

    protected fun hideKeyboard(view: View) {
        val imm = this.context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

}