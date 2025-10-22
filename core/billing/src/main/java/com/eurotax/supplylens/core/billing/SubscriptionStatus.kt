package com.eurotax.supplylens.core.billing

/**
 * Represents the subscription status of the user.
 */
enum class SubscriptionStatus {
    /** User has no active subscription */
    FREE,
    
    /** User is in 3-day trial period */
    TRIAL_PRO,
    
    /** User has active paid PRO subscription */
    PRO,
    
    /** Subscription expired or was cancelled */
    EXPIRED
}
