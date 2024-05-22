package woowacourse.shopping.domain.repository.local

import woowacourse.shopping.domain.model.Product

interface ShoppingCartRepository {
    fun insertCartProduct(
        productId: Long,
        name: String,
        price: Int,
        quantity: Int,
        imageUrl: String,
    ): Result<Unit>

    fun findCartProduct(productId: Long): Result<Product>

    fun getCartProductsPaged(
        page: Int,
        pageSize: Int,
    ): Result<List<Product>>

    fun getCartProductsTotal(): Result<Int>

    fun deleteCartProduct(productId: Long): Result<Unit>

    fun deleteAllCartProducts(): Result<Unit>
}