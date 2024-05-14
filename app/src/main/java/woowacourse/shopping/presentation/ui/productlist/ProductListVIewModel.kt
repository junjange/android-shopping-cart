package woowacourse.shopping.presentation.ui.productlist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import woowacourse.shopping.data.repsoitory.DummyProductList
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.repository.ProductListRepository

class ProductListVIewModel(
    private val productListRepository: ProductListRepository = DummyProductList,
) : ViewModel(), ProductListActionHandler {
    private val _productList: MutableLiveData<List<Product>> = MutableLiveData(emptyList())
    val productList: LiveData<List<Product>> get() = _productList

    init {
        getProductList()
    }

    private fun getProductList() {
        _productList.value = productListRepository.getProductList()
    }
}