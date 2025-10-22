package com.eurotax.supplylens.core.billing

import android.app.Activity
import android.content.Context
import com.android.billingclient.api.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

/**
 * Manages Google Play Billing integration for SupplyLens PRO subscriptions.
 * 
 * Features:
 * - 3-day PRO trial (configured in Google Play Console)
 * - Automatic subscription status detection
 * - StateFlow-based reactive subscription state
 * 
 * Usage:
 * ```
 * val billingManager = BillingManager(context)
 * billingManager.initialize()
 * 
 * // Observe subscription status
 * billingManager.subscriptionStatus.collect { status ->
 *     when (status) {
 *         SubscriptionStatus.FREE -> // Show paywall
 *         SubscriptionStatus.TRIAL_PRO -> // Trial UI
 *         SubscriptionStatus.PRO -> // Full access
 *         SubscriptionStatus.EXPIRED -> // Renewal prompt
 *     }
 * }
 * 
 * // Launch subscription flow
 * billingManager.launchSubscriptionFlow(activity)
 * ```
 */
class BillingManager(private val context: Context) {
    
    companion object {
        /** Product ID for PRO subscription (configure in Google Play Console) */
        private const val PRODUCT_ID_PRO = "supplylens_pro_monthly"
        
        /** Trial period: 3 days (configured in Play Console) */
        private const val TRIAL_DAYS = 3
    }
    
    private val _subscriptionStatus = MutableStateFlow(SubscriptionStatus.FREE)
    val subscriptionStatus: StateFlow<SubscriptionStatus> = _subscriptionStatus.asStateFlow()
    
    private var billingClient: BillingClient? = null
    private var productDetails: ProductDetails? = null
    
    /**
     * Initialize billing client and query subscription status.
     * Call this in Application.onCreate() or MainActivity.onCreate()
     */
    suspend fun initialize() {
        billingClient = BillingClient.newBuilder(context)
            .setListener { billingResult, purchases ->
                handlePurchasesUpdate(billingResult, purchases)
            }
            .enablePendingPurchases()
            .build()
        
        connectToBillingClient()
        queryProductDetails()
        queryActivePurchases()
    }
    
    /**
     * Launch subscription purchase flow for PRO subscription.
     * 
     * @param activity Activity context required for billing flow
     */
    suspend fun launchSubscriptionFlow(activity: Activity) {
        val details = productDetails ?: run {
            // Product not loaded yet, try to query again
            queryProductDetails()
            productDetails
        } ?: return
        
        val offerToken = details.subscriptionOfferDetails?.firstOrNull()?.offerToken ?: return
        
        val productDetailsParamsList = listOf(
            BillingFlowParams.ProductDetailsParams.newBuilder()
                .setProductDetails(details)
                .setOfferToken(offerToken)
                .build()
        )
        
        val billingFlowParams = BillingFlowParams.newBuilder()
            .setProductDetailsParamsList(productDetailsParamsList)
            .build()
        
        billingClient?.launchBillingFlow(activity, billingFlowParams)
    }
    
    /**
     * Refresh subscription status from Google Play.
     */
    suspend fun refreshSubscriptionStatus() {
        queryActivePurchases()
    }
    
    /**
     * Clean up resources when done.
     */
    fun destroy() {
        billingClient?.endConnection()
        billingClient = null
    }
    
    // Internal implementation
    
    private suspend fun connectToBillingClient() = suspendCancellableCoroutine { continuation ->
        billingClient?.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    continuation.resume(Unit)
                } else {
                    continuation.resume(Unit) // Continue anyway, user stays FREE
                }
            }
            
            override fun onBillingServiceDisconnected() {
                // Retry connection logic can be added here
            }
        })
    }
    
    private suspend fun queryProductDetails() {
        val productList = listOf(
            QueryProductDetailsParams.Product.newBuilder()
                .setProductId(PRODUCT_ID_PRO)
                .setProductType(BillingClient.ProductType.SUBS)
                .build()
        )
        
        val params = QueryProductDetailsParams.newBuilder()
            .setProductList(productList)
            .build()
        
        val result = suspendCancellableCoroutine { continuation ->
            billingClient?.queryProductDetailsAsync(params) { billingResult, productDetailsList ->
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    productDetails = productDetailsList.firstOrNull()
                }
                continuation.resume(Unit)
            }
        }
    }
    
    private suspend fun queryActivePurchases() {
        val params = QueryPurchasesParams.newBuilder()
            .setProductType(BillingClient.ProductType.SUBS)
            .build()
        
        val result = billingClient?.queryPurchasesAsync(params)
        result?.purchasesList?.let { purchases ->
            handlePurchases(purchases)
        }
    }
    
    private fun handlePurchasesUpdate(billingResult: BillingResult, purchases: List<Purchase>?) {
        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
            handlePurchases(purchases)
        } else {
            _subscriptionStatus.value = SubscriptionStatus.FREE
        }
    }
    
    private fun handlePurchases(purchases: List<Purchase>) {
        val proPurchase = purchases.firstOrNull { purchase ->
            purchase.products.contains(PRODUCT_ID_PRO) && 
            purchase.purchaseState == Purchase.PurchaseState.PURCHASED
        }
        
        if (proPurchase != null) {
            // Determine if user is in trial or paid subscription
            val isTrialPeriod = proPurchase.isAutoRenewing && 
                                (System.currentTimeMillis() - proPurchase.purchaseTime) < (TRIAL_DAYS * 24 * 60 * 60 * 1000L)
            
            _subscriptionStatus.value = if (isTrialPeriod) {
                SubscriptionStatus.TRIAL_PRO
            } else {
                SubscriptionStatus.PRO
            }
            
            // Acknowledge purchase if not already acknowledged
            if (!proPurchase.isAcknowledged) {
                acknowledgePurchase(proPurchase.purchaseToken)
            }
        } else {
            _subscriptionStatus.value = SubscriptionStatus.FREE
        }
    }
    
    private fun acknowledgePurchase(purchaseToken: String) {
        val params = AcknowledgePurchaseParams.newBuilder()
            .setPurchaseToken(purchaseToken)
            .build()
        
        billingClient?.acknowledgePurchase(params) { _ ->
            // Purchase acknowledged
        }
    }
}
