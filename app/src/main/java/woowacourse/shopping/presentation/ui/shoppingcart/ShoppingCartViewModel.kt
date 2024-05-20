package woowacourse.shopping.presentation.ui.shoppingcart

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import woowacourse.shopping.domain.repository.ShoppingCartRepository
import woowacourse.shopping.presentation.base.BaseViewModel
import woowacourse.shopping.presentation.base.BaseViewModelFactory
import woowacourse.shopping.presentation.base.MessageProvider
import woowacourse.shopping.presentation.ui.shoppingcart.adapter.ShoppingCartPagingSource

class ShoppingCartViewModel(private val repository: ShoppingCartRepository) :
    BaseViewModel(),
    ShoppingCartActionHandler {
    private val _uiState: MutableLiveData<ShoppingCartUiState> =
        MutableLiveData(ShoppingCartUiState())
    val uiState: LiveData<ShoppingCartUiState> get() = _uiState

    private val shoppingCartPagingSource = ShoppingCartPagingSource(repository)

    init {
        loadOrders(INIT_PAGE)
    }

    private fun loadOrders(page: Int) {
        shoppingCartPagingSource.load(page).onSuccess { pagingOrder ->
            _uiState.value =
                _uiState.value?.copy(pagingOrder = pagingOrder)
        }.onFailure { e ->
            showMessage(MessageProvider.DefaultErrorMessage)
        }
    }

    override fun removeOrder(orderId: Int) {
        repository.removeOrder(orderId)
        uiState.value?.let { state ->
            loadOrders(state.pagingOrder.currentPage)
        }
    }

    fun loadNextPage() {
        uiState.value?.let { state ->
            loadOrders(state.pagingOrder.currentPage + 1)
        }
    }

    fun loadPreviousPage() {
        uiState.value?.let { state ->
            loadOrders(state.pagingOrder.currentPage - 1)
        }
    }

    companion object {
        const val INIT_PAGE = 0

        fun factory(repository: ShoppingCartRepository): ViewModelProvider.Factory {
            return BaseViewModelFactory {
                ShoppingCartViewModel(repository)
            }
        }
    }
}
