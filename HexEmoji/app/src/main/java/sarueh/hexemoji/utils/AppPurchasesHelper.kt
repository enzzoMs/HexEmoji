package sarueh.hexemoji.utils

import android.app.Activity
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClient.BillingResponseCode
import com.android.billingclient.api.BillingClient.ConnectionState
import com.android.billingclient.api.BillingClient.ProductType
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.Purchase.PurchaseState
import com.android.billingclient.api.QueryProductDetailsParams
import com.android.billingclient.api.QueryPurchaseHistoryParams
import com.android.billingclient.api.QueryPurchasesParams
import dagger.hilt.android.EntryPointAccessors
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import sarueh.hexemoji.data.repositories.EmojiRepository
import sarueh.hexemoji.data.repositories.PreferencesRepository
import sarueh.hexemoji.di.RepositoriesEntryPoint

class AppPurchasesHelper(
    private val onConnectionFail: () -> Unit,
    private val onPurchaseFlowError: () -> Unit,
    private val getActivity: () -> Activity
) {
    private val emojiRepository: EmojiRepository
    private val preferencesRepository: PreferencesRepository

    init {
        val repositoriesEntryPoint = EntryPointAccessors.fromApplication(
            getActivity().applicationContext, RepositoriesEntryPoint::class.java
        )

        emojiRepository = repositoriesEntryPoint.getEmojisRepository()
        preferencesRepository = repositoriesEntryPoint.getPreferencesRepository()
    }

    private val billingClientDelegate = lazy {
        BillingClient
            .newBuilder(getActivity().applicationContext)
            .enablePendingPurchases()
            .setListener { billingResult, purchases ->
                if (billingResult.responseCode == BillingResponseCode.OK && purchases != null) {
                    for (purchase in purchases) {
                        handleUnacknowledgedPurchase(purchase)
                    }
                } else {
                    onPurchaseFlowError()
                }
            }
            .build()
    }
    private val billingClient by billingClientDelegate

    fun launchPurchaseFlowForProduct(productId: String) {
        startConnection { queryProductDetails(productId, ::launchBillingFlow) }
    }

    fun handlePendingPurchases() {
        startConnection {
            queryPurchases { purchases ->
                grantAlreadyPurchasedProducts(purchases)
                purchases.forEach { handleUnacknowledgedPurchase(it) }
            }
        }
    }

    fun endConnection() {
        if (billingClientDelegate.isInitialized()) {
            billingClient.endConnection()
        }
    }

    private fun startConnection(onConnectionSuccess: () -> Unit) {
        if (billingClient.connectionState == ConnectionState.DISCONNECTED) {
            billingClient.startConnection(object : BillingClientStateListener {
                override fun onBillingSetupFinished(billingResult: BillingResult) {
                    if (billingResult.responseCode == BillingResponseCode.OK) {
                        onConnectionSuccess()
                    } else {
                        onConnectionFail()
                    }
                }

                override fun onBillingServiceDisconnected() {}
            })
        } else {
            onConnectionSuccess()
        }
    }

    private fun queryPurchases(onQuerySuccess: (List<Purchase>) -> Unit) {
        val purchasesHistoryParams = QueryPurchaseHistoryParams
            .newBuilder()
            .setProductType(ProductType.INAPP)
            .build()

        billingClient.queryPurchaseHistoryAsync(purchasesHistoryParams) { billingResult, _ ->
            if (billingResult.responseCode == BillingResponseCode.OK) {
                val purchasesParams = QueryPurchasesParams
                    .newBuilder()
                    .setProductType(ProductType.INAPP)
                    .build()

                billingClient.queryPurchasesAsync(purchasesParams) { _, purchasesResults ->
                    onQuerySuccess(purchasesResults)
                }
            } else {
                onPurchaseFlowError()
            }
        }
    }

    private fun queryProductDetails(productId: String, onQuerySuccess: (ProductDetails) -> Unit) {
        val queryProductDetailsParams = QueryProductDetailsParams
            .newBuilder()
            .setProductList(listOf(
                QueryProductDetailsParams.Product.newBuilder()
                    .setProductId(productId)
                    .setProductType(ProductType.INAPP)
                    .build()
            ))
            .build()

        billingClient.queryProductDetailsAsync(queryProductDetailsParams) { billingResult, productDetailsList ->
            when (billingResult.responseCode) {
                BillingResponseCode.OK -> {
                    productDetailsList.find { it.productId == productId }?.let { onQuerySuccess(it) }
                }
                else -> {
                    onPurchaseFlowError()
                }
            }
        }
    }

    private fun launchBillingFlow(productDetails: ProductDetails) {
        val productDetailsParams = listOf(
            BillingFlowParams.ProductDetailsParams.newBuilder()
                .setProductDetails(productDetails)
                .build()
        )

        val billingFlowParams = BillingFlowParams.newBuilder()
            .setProductDetailsParamsList(productDetailsParams)
            .build()

        val billingResult = billingClient.launchBillingFlow(getActivity(), billingFlowParams)

        if (billingResult.responseCode != BillingResponseCode.OK) {
            onPurchaseFlowError()
        }
    }

    private fun handleUnacknowledgedPurchase(purchase: Purchase) {
        if (purchase.purchaseState == PurchaseState.PURCHASED && !purchase.isAcknowledged) {
            CoroutineScope(Dispatchers.IO).launch {
                purchase.products.forEach { grantProduct(it) }
            }

            val acknowledgePurchaseParams = AcknowledgePurchaseParams
                .newBuilder()
                .setPurchaseToken(purchase.purchaseToken)
                .build()

            billingClient.acknowledgePurchase(acknowledgePurchaseParams) {}
        }
    }

    private fun grantAlreadyPurchasedProducts(purchases: List<Purchase>) {
        val grantedPurchasedProducts = preferencesRepository.getBoolean(
            PreferencesRepository.PREFERENCE_KEY_GRANTED_PURCHASED_PRODUCTS, false
        )

        val purchasedProducts = purchases.filter {
            it.isAcknowledged && it.purchaseState == PurchaseState.PURCHASED
        }

        if (purchasedProducts.isNotEmpty() && !grantedPurchasedProducts) {
            CoroutineScope(Dispatchers.IO).launch {
                purchasedProducts.map { it.products }.flatten().forEach { grantProduct(it) }

                preferencesRepository.putBoolean(
                    PreferencesRepository.PREFERENCE_KEY_GRANTED_PURCHASED_PRODUCTS, true
                )
            }
        }
    }

    private suspend fun grantProduct(productId: String) {
        when (productId) {
            PRODUCT_ID_UNLOCK_ALL_EMOJIS -> {
                emojiRepository.unlockAllEmoji()
                preferencesRepository.putString(
                    PreferencesRepository.PREFERENCE_KEY_NEXT_DAILY_EMOJI, ""
                )
            }
        }
    }

    companion object {
        const val PRODUCT_ID_UNLOCK_ALL_EMOJIS = "hx_product_unlock_emojis"
    }
}