package com.eurotax.supplylens

import android.app.Application
import com.eurotax.supplylens.core.billing.BillingManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

/**
 * Main Application class for SupplyLens.
 * 
 * Initializes:
 * - BillingManager (Google Play Billing)
 * - Application-level dependencies
 */
class MainApplication : Application() {
    
    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    
    lateinit var billingManager: BillingManager
        private set
    
    override fun onCreate() {
        super.onCreate()
        
        // Initialize Billing
        billingManager = BillingManager(this)
        applicationScope.launch {
            billingManager.initialize()
        }
    }
    
    override fun onTerminate() {
        super.onTerminate()
        billingManager.destroy()
    }
}
