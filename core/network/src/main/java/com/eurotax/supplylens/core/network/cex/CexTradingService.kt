package com.eurotax.supplylens.core.network.cex

/**
 * CEX (Centralized Exchange) trading service interface
 * Provides order placement, cancellation, and symbol information
 */
interface CexTradingService {

    /**
     * Place a new order on the exchange
     * @param order The order to place
     * @return Result with order ID on success
     */
    suspend fun placeOrder(order: OrderRequest): Result<String>

    /**
     * Cancel an existing order
     * @param orderId The ID of the order to cancel
     * @return Result with success status
     */
    suspend fun cancelOrder(orderId: String): Result<Boolean>

    /**
     * Get available trading symbols
     * @return Result with list of symbols
     */
    suspend fun getSymbols(): Result<List<TradingSymbol>>

    /**
     * Get order status
     * @param orderId The ID of the order
     * @return Result with order status
     */
    suspend fun getOrderStatus(orderId: String): Result<OrderStatus>
}

/**
 * Order request data
 */
data class OrderRequest(
    val symbol: String,
    val side: OrderSide,
    val type: OrderType,
    val quantity: Double,
    val price: Double? = null // Required for limit orders
)

enum class OrderSide {
    BUY,
    SELL
}

enum class OrderType {
    MARKET,
    LIMIT
}

/**
 * Trading symbol information
 */
data class TradingSymbol(
    val symbol: String,
    val baseAsset: String,
    val quoteAsset: String,
    val status: String,
    val minQuantity: Double? = null,
    val maxQuantity: Double? = null
)

/**
 * Order status information
 */
data class OrderStatus(
    val orderId: String,
    val symbol: String,
    val status: String, // NEW, FILLED, PARTIALLY_FILLED, CANCELED, etc.
    val executedQty: Double,
    val price: Double?
)

/**
 * Mock implementation for Binance-style CEX
 * This is a stub for testing - no real API calls are made
 */
class MockBinanceTradingService(
    private val enableMock: Boolean = false
) : CexTradingService {

    override suspend fun placeOrder(order: OrderRequest): Result<String> {
        if (!enableMock) {
            return Result.failure(NotImplementedError("Binance integration not enabled"))
        }

        // Mock implementation
        val orderId = "MOCK_${System.currentTimeMillis()}"
        return Result.success(orderId)
    }

    override suspend fun cancelOrder(orderId: String): Result<Boolean> {
        if (!enableMock) {
            return Result.failure(NotImplementedError("Binance integration not enabled"))
        }

        return Result.success(true)
    }

    override suspend fun getSymbols(): Result<List<TradingSymbol>> {
        if (!enableMock) {
            return Result.failure(NotImplementedError("Binance integration not enabled"))
        }

        // Mock symbols
        return Result.success(
            listOf(
                TradingSymbol("BTCUSDT", "BTC", "USDT", "TRADING"),
                TradingSymbol("ETHUSDT", "ETH", "USDT", "TRADING")
            )
        )
    }

    override suspend fun getOrderStatus(orderId: String): Result<OrderStatus> {
        if (!enableMock) {
            return Result.failure(NotImplementedError("Binance integration not enabled"))
        }

        return Result.success(
            OrderStatus(
                orderId = orderId,
                symbol = "BTCUSDT",
                status = "FILLED",
                executedQty = 0.001,
                price = 50000.0
            )
        )
    }
}

/**
 * Mock implementation for KuCoin-style CEX
 * This is a stub for testing - no real API calls are made
 */
class MockKuCoinTradingService(
    private val enableMock: Boolean = false
) : CexTradingService {

    override suspend fun placeOrder(order: OrderRequest): Result<String> {
        if (!enableMock) {
            return Result.failure(NotImplementedError("KuCoin integration not enabled"))
        }

        val orderId = "KC_MOCK_${System.currentTimeMillis()}"
        return Result.success(orderId)
    }

    override suspend fun cancelOrder(orderId: String): Result<Boolean> {
        if (!enableMock) {
            return Result.failure(NotImplementedError("KuCoin integration not enabled"))
        }

        return Result.success(true)
    }

    override suspend fun getSymbols(): Result<List<TradingSymbol>> {
        if (!enableMock) {
            return Result.failure(NotImplementedError("KuCoin integration not enabled"))
        }

        return Result.success(
            listOf(
                TradingSymbol("BTC-USDT", "BTC", "USDT", "TRADING"),
                TradingSymbol("ETH-USDT", "ETH", "USDT", "TRADING")
            )
        )
    }

    override suspend fun getOrderStatus(orderId: String): Result<OrderStatus> {
        if (!enableMock) {
            return Result.failure(NotImplementedError("KuCoin integration not enabled"))
        }

        return Result.success(
            OrderStatus(
                orderId = orderId,
                symbol = "BTC-USDT",
                status = "FILLED",
                executedQty = 0.001,
                price = 50000.0
            )
        )
    }
}
