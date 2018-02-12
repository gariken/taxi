package taxi.eskar.eskartaxi.ui.order.setup.passenger.comments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import ru.terrakok.cicerone.NavigatorHolder
import ru.terrakok.cicerone.android.SupportAppNavigator
import taxi.eskar.eskartaxi.R
import taxi.eskar.eskartaxi.base.BaseActivity
import taxi.eskar.eskartaxi.data.model.Order
import taxi.eskar.eskartaxi.injection.Scopes
import toothpick.Toothpick
import javax.inject.Inject

class OrderCommentsActivity : BaseActivity() {

    companion object {
        private const val EXTRA_ORDER = "extras.openOrder"

        fun newIntent(context: Context, order: Order): Intent {
            val intent = Intent(context, OrderCommentsActivity::class.java)
            intent.putExtra(EXTRA_ORDER, order)
            return intent
        }
    }

    @Inject lateinit var holder: NavigatorHolder

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Toothpick.inject(this, Toothpick.openScope(Scopes.APP))

        val order = this.intent.getSerializableExtra(EXTRA_ORDER) as Order

        this.setContentView(R.layout.activity_order_comments)
        this.supportFragmentManager.beginTransaction()
                .replace(R.id.container_comments, OrderCommentsFragment.newInstance(order))
                .commitNow()
    }

    override fun onResumeFragments() {
        super.onResumeFragments()
        holder.setNavigator(navigator)
    }

    override fun onPause() {
        holder.removeNavigator()
        super.onPause()
    }

    private val navigator = object : SupportAppNavigator(this, R.id.container_address) {

        override fun createActivityIntent(screenKey: String?, data: Any?): Intent? = null

        override fun createFragment(screenKey: String?, data: Any?): Fragment =
                throw UnsupportedOperationException("No screen for key $screenKey")
    }

}