package woowacourse.shopping.domain.repository

import woowacourse.shopping.domain.model.OrderList
import woowacourse.shopping.domain.model.Product

interface ShoppingCartRepository {
    fun addOrder(product: Product)

    fun removeOrder(orderId: Int)

    fun removeAllOrder()

    fun getPagingOrder(
        page: Int,
        pageSize: Int,
    ): Result<OrderList>
}
