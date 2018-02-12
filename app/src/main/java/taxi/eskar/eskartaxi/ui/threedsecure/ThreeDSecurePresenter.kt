package taxi.eskar.eskartaxi.ui.threedsecure

import com.arellomobile.mvp.InjectViewState
import io.reactivex.android.schedulers.AndroidSchedulers
import ru.terrakok.cicerone.Router
import taxi.eskar.eskartaxi.base.BasePresenter
import taxi.eskar.eskartaxi.business.threedsecure.ThreeDSecureInteractor
import taxi.eskar.eskartaxi.data.model.results.BindResult
import taxi.eskar.eskartaxi.data.resources.MessageResource
import taxi.eskar.eskartaxi.ui.Screens
import java.net.URLEncoder
import javax.inject.Inject

@InjectViewState
class ThreeDSecurePresenter @Inject constructor(
        private val interactor: ThreeDSecureInteractor,
        private val messageResource: MessageResource,
        private val result: BindResult.Success3dsE, router: Router
) : BasePresenter<ThreeDSecureView>(router) {

    init {
        val charset = Charsets.UTF_8.displayName()
        val pareq = URLEncoder.encode(result.paReq, charset)
        val md = URLEncoder.encode(result.transactionId.toString(), charset)
        val termurl = URLEncoder.encode(result.termUrl, charset)
        val postDataString = "PaReq=$pareq&MD=$md&TermUrl=$termurl"
        viewState.loadPage(result.acsUrl, postDataString)
    }

    fun onPageLoadingStarted(url: String) {
        viewState.showLoading(true)
        if (url == result.termUrl) {
            viewState.showWebView(false)
        }
    }

    fun onPageLoadingFinished(url: String) {
        viewState.showLoading(false)
        if (url == result.termUrl) {

        }
    }

    fun onResponse(json: String) {
        interactor.onResponse(json)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(this::unsubscribeOnDestroy)
                .doOnEvent { _, _ -> viewState.showLoading(false) }
                .subscribe(this::processBindResult, this::processError)
    }

    private fun processBindResult(result: BindResult) {
        when (result) {
            is BindResult.Success3dsD ->
                viewState.showSystemMessage(messageResource.cardBindingSuccess())
            is BindResult.Code -> router.exitWithMessage(messageResource.error(result.code))
            else -> router.exitWithMessage(messageResource.cardBindingDataError())
        }
    }

    fun onSuccessConfirmed() {
        router.backTo(Screens.CARDS)
    }

}