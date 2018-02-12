package taxi.eskar.eskartaxi.ui.threedsecure

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import kotlinx.android.synthetic.main.fragment_three_d_secure.*
import kotlinx.android.synthetic.main.layout_appbar.*
import taxi.eskar.eskartaxi.R
import taxi.eskar.eskartaxi.base.BaseFragment
import taxi.eskar.eskartaxi.data.model.results.BindResult
import taxi.eskar.eskartaxi.injection.Scopes
import taxi.eskar.eskartaxi.injection.ThreeDSecureModule
import taxi.eskar.eskartaxi.util.AlertDialogHelper
import taxi.eskar.eskartaxi.util.CookieManagerHelper
import toothpick.Toothpick

class ThreeDSecureFragment : BaseFragment(), ThreeDSecureView {

    companion object {
        private const val ARG_RESULT = "args.result"
        private const val JS = "javascript:window.HtmlViewer.showHTML(document.getElementsByTagName('pre')[0].innerHTML);"

        fun newInstance(result: BindResult.Success3dsE): ThreeDSecureFragment {
            return ThreeDSecureFragment().apply {
                arguments = Bundle().apply { putSerializable(ARG_RESULT, result) }
            }
        }
    }


    // region moxy

    @InjectPresenter lateinit var presenter: ThreeDSecurePresenter

    @ProvidePresenter fun providePresenter(): ThreeDSecurePresenter {
        val scopeName = ThreeDSecureFragment::class.java.simpleName
        val result = arguments?.get(ARG_RESULT) as BindResult.Success3dsE
        val scope = Toothpick.openScopes(Scopes.APP, scopeName)
        scope.installModules(ThreeDSecureModule(result))
        val presenter = scope.getInstance(ThreeDSecurePresenter::class.java)
        Toothpick.closeScope(scopeName)
        return presenter
    }

    // endregion


    // region android

    override val layoutResId: Int = R.layout.fragment_three_d_secure

    @SuppressLint("SetJavaScriptEnabled")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        toolbar.apply {
            setTitle(R.string.title_three_d_secure)
            setNavigationIcon(R.drawable.ic_arrow_back_orange)
            setNavigationOnClickListener { presenter.onBackClicked() }
        }
        webView.apply {
            CookieManagerHelper.clear()
            addJavascriptInterface(JsInterface(), "HtmlViewer")
            settings.apply {
                javaScriptEnabled = true
            }
            webViewClient = object : WebViewClient() {

                override fun onPageStarted(view: WebView, url: String, favicon: Bitmap?) {
                    super.onPageStarted(view, url, favicon)
                    presenter.onPageLoadingStarted(url)
                }

                override fun onPageFinished(view: WebView, url: String) {
                    super.onPageFinished(view, url)
                    presenter.onPageLoadingFinished(url)
                    view.loadUrl(JS)
                }
            }
        }
    }

    // endregion


    // region view

    override fun loadPage(url: String, postData: String) {
        webView.postUrl(url, postData.toByteArray(Charsets.UTF_8))
    }

    override fun showWebView(show: Boolean) {
        webView.visibility = if (show) View.VISIBLE else View.INVISIBLE
    }

    override fun showSystemMessage(message: String) {
        AlertDialogHelper.showSystemMessage(context, message,
                DialogInterface.OnClickListener { _, _ -> presenter.onSuccessConfirmed() })
    }

    // endregion

    private inner class JsInterface {

        @JavascriptInterface
        fun showHTML(json: String) {
            presenter.onResponse(json)
        }

    }

}