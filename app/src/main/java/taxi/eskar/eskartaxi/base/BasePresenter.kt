package taxi.eskar.eskartaxi.base

import com.arellomobile.mvp.MvpPresenter
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import ru.terrakok.cicerone.Router
import timber.log.Timber

abstract class BasePresenter<View : BaseView>(
        protected val router: Router
) : MvpPresenter<View>() {

    private val destroyDisposable = CompositeDisposable()
    private val detachDisposable = CompositeDisposable()


    protected fun processError(throwable: Throwable) {
        throwable.apply {
            Timber.e(this)
        }
    }


    // region DisposableHolder
    fun unsubscribeOnDestroy(d: Disposable) {
        destroyDisposable.add(d)
    }

    fun unsubscribeOnDetach(d: Disposable) {
        detachDisposable.add(d)
    }

    // endregion

    // region Lifecycle

    override fun attachView(view: View) {
        super.attachView(view)
        view.bind()
    }

    override fun detachView(view: View) {
        detachDisposable.clear()
        super.detachView(view)
    }

    override fun onDestroy() {
        destroyDisposable.clear()
        super.onDestroy()
    }

    // endregion


    // region View

    fun onBackClicked() {
        router.exit()
    }

    // endregion
}