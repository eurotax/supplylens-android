package com.eurotax.supplylens.core.domain.model

/**
 * Security assessment report for a token
 */
data class SecurityReport(
    val score: Int, // 0-100 score
    val flags: List<String> = emptyList(),
    val lpLock: LiquidityPoolLock? = null,
    val taxes: TaxInfo? = null,
    val ownership: OwnershipInfo? = null
)

data class LiquidityPoolLock(
    val lockedPercentage: Double, // 0.0 - 100.0
    val unlockDate: Long? = null // Unix timestamp in milliseconds
)

data class TaxInfo(
    val buyTax: Double, // percentage 0.0 - 100.0
    val sellTax: Double // percentage 0.0 - 100.0
)

data class OwnershipInfo(
    val renounced: Boolean,
    val proxyUpgradeable: Boolean
)
