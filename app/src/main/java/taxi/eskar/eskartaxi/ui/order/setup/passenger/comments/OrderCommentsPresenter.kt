package taxi.eskar.eskartaxi.ui.order.setup.passenger.comments

import com.arellomobile.mvp.InjectViewState
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import ru.terrakok.cicerone.Router
import taxi.eskar.eskartaxi.base.BasePresenter
import taxi.eskar.eskartaxi.data.model.Order
import taxi.eskar.eskartaxi.data.model.OrderOption
import taxi.eskar.eskartaxi.data.model.results.OrderOptionsResult
import taxi.eskar.eskartaxi.data.repository.order.OrderRepository
import taxi.eskar.eskartaxi.ui.Results
import timber.log.Timber

@InjectViewState
class OrderCommentsPresenter(
        private val orderRepository: OrderRepository,
        private val order: Order, router: Router
) : BasePresenter<OrderCommentsView>(router) {

    private val options = mutableListOf<OrderOption>()
    private val selectedOptions = mutableListOf<Int>()

    init {
        this.fetchOptionsAndShow()
        viewState.showOrder(order)
    }

    fun onCommentChanged(comment: String) {
        order.comment = comment
    }

    fun onSelectedChanged(selected: List<Int>) {
        selectedOptions.clear()
        selectedOptions.addAll(selected)
        Timber.i(selectedOptions.toString())
    }

    fun onSaveClicked() {
        router.exitWithResult(Results.ORDER_DETAILS_COMMENTS, order.apply {
            orderOptions.clear()
            orderOptions.addAll(options.filter { selectedOptions.contains(it.id) }.apply {
                Timber.i(toString())
            })
        })
    }

    private fun fetchOptionsAndShow() {
        orderRepository.getOrderOptions()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::processOptionsResult, this::processError)
    }

    private fun processOptionsResult(result: OrderOptionsResult) {
        when (result) {
            is OrderOptionsResult.Success -> {
                if (result.options.isEmpty()) {
                    options.clear()
                    selectedOptions.clear()
                    order.orderOptions.clear()
                    viewState.showOptionsEmpty()
                } else {
                    options.clear()
                    options.addAll(result.options)
                    selectedOptions.clear()
                    selectedOptions.addAll(order.orderOptions.map { it.id })
                    viewState.showOptions(result.options, selectedOptions)
                }
            }
            is OrderOptionsResult.Fail -> this.processError(result.throwable)
        }
    }
}
