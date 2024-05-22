package woowacourse.shopping.presentation.ui.productdetail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.repository.ProductRepository
import woowacourse.shopping.domain.repository.local.ShoppingCartRepository
import woowacourse.shopping.presentation.base.BaseViewModel
import woowacourse.shopping.presentation.base.BaseViewModelFactory
import woowacourse.shopping.presentation.base.Event
import woowacourse.shopping.presentation.base.MessageProvider
import woowacourse.shopping.presentation.base.emit
import woowacourse.shopping.presentation.common.ProductCountHandler
import woowacourse.shopping.presentation.ui.productdetail.ProductDetailActivity.Companion.PUT_EXTRA_PRODUCT_ID
import woowacourse.shopping.presentation.ui.shoppingcart.UpdatedProducts
import kotlin.concurrent.thread

class ProductDetailViewModel(
    savedStateHandle: SavedStateHandle,
    private val productRepository: ProductRepository,
    private val shoppingCartRepository: ShoppingCartRepository,
) : BaseViewModel(), ProductCountHandler {
    private val _product: MutableLiveData<Product> = MutableLiveData()
    val product: LiveData<Product> get() = _product

    private val _navigateAction: MutableLiveData<Event<ProductDetailNavigateAction>> =
        MutableLiveData(null)
    val navigateAction: LiveData<Event<ProductDetailNavigateAction>> get() = _navigateAction

    init {
        savedStateHandle.get<Long>(PUT_EXTRA_PRODUCT_ID)?.let { productId ->
            findByProductId(productId)
        }
    }

    private fun findByProductId(id: Long) {
        productRepository.findProductById(id).onSuccess { productValue ->
            _product.value = productValue
            findProduct(id)
        }.onFailure { e ->
            when (e) {
                is NoSuchElementException -> showMessage(ProductDetailMessage.NoSuchElementErrorMessage)
                else -> showMessage(MessageProvider.DefaultErrorMessage)
            }
        }
    }

    private fun findProduct(productId: Long) {
        thread {
            shoppingCartRepository.findCartProduct(productId = productId)
                .onSuccess { productValue ->
                    _product.value?.let { value ->
                        _product.postValue(value.copy(quantity = productValue.quantity))
                    }
                }.onFailure {
                    // TODO 예외 처리
                }
        }
    }

    fun addToCart() {
        product.value?.let { product ->
            thread {
                shoppingCartRepository.insertCartProduct(
                    productId = product.id,
                    name = product.name,
                    price = product.price,
                    quantity = product.quantity,
                    imageUrl = product.imageUrl,
                ).onSuccess {
                    showMessage(ProductDetailMessage.AddToCartSuccessMessage)
                }.onFailure {
                    showMessage(MessageProvider.DefaultErrorMessage)
                }
            }
        }
    }

    override fun addProductQuantity(
        productId: Long,
        position: Int,
    ) {
        _product.value?.let { value ->
            _product.value = value.copy(quantity = value.quantity + 1)
        }
    }

    override fun minusProductQuantity(
        productId: Long,
        position: Int,
    ) {
        _product.value?.let { value ->
            _product.value = value.copy(quantity = value.quantity - 1)
        }
    }

    fun navigateToProductList() {
        _product.value?.let { value ->
            val updatedProducts = UpdatedProducts(mutableMapOf(value.id to value))
            _navigateAction.emit(ProductDetailNavigateAction.NavigateToProductList(updatedProducts))
        }
    }

    companion object {
        fun factory(
            productRepository: ProductRepository,
            shoppingCartRepository: ShoppingCartRepository,
        ): ViewModelProvider.Factory {
            return BaseViewModelFactory { extras ->
                ProductDetailViewModel(
                    savedStateHandle = extras.createSavedStateHandle(),
                    productRepository = productRepository,
                    shoppingCartRepository = shoppingCartRepository,
                )
            }
        }
    }
}
