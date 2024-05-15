package woowacourse.shopping.presentation.ui.shoppingcart

import android.content.Context
import android.content.Intent
import android.view.MenuItem
import androidx.activity.viewModels
import woowacourse.shopping.R
import woowacourse.shopping.databinding.ActivityShoppingCartBinding
import woowacourse.shopping.presentation.base.BindingActivity
import woowacourse.shopping.presentation.base.ViewModelFactory
import woowacourse.shopping.presentation.base.observeEvent
import woowacourse.shopping.presentation.ui.shoppingcart.adapter.OrderListAdapter

class ShoppingCartActivity : BindingActivity<ActivityShoppingCartBinding>() {
    override val layoutResourceId: Int get() = R.layout.activity_shopping_cart

    private val viewModel: ShoppingCartViewModel by viewModels { ViewModelFactory() }

    private val adapter: OrderListAdapter by lazy { OrderListAdapter(viewModel) }

    override fun initStartView() {
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            title = getString(R.string.cart_title)
        }

        binding.apply {
            vm = viewModel
            lifecycleOwner = this@ShoppingCartActivity
        }
        initAdapter()
        initObserve()
    }

    private fun initAdapter() {
        binding.rvOrderList.adapter = adapter
    }

    private fun initObserve() {
        viewModel.uiState.observe(this) { uiState ->
            uiState.pagingOrder?.let { pagingOrder ->
                adapter.updateOrderList(pagingOrder.orderList)
            }
        }

        viewModel.message.observeEvent(this) { message ->
            when (message) {
                is ShoppingCartMessage.DefaultErrorMessage -> showSnackbar(message.toString(this))
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        finish()
        return true
    }

    companion object {
        fun startActivity(context: Context) {
            val intent = Intent(context, ShoppingCartActivity::class.java)
            context.startActivity(intent)
        }
    }
}
