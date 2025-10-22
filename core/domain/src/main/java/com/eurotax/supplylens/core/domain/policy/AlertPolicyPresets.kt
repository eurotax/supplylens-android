package com.eurotax.supplylens.core.domain.policy

/**
 * Preset alert policies for different risk tolerance levels
 */
object AlertPolicyPresets {

    /**
     * Conservative preset - more risk-averse thresholds
     */
    val CONSERVATIVE = AlertPolicy(
        lpLock = LpLockThresholds(
            highRiskDays = 14,
            highRiskLockedPct = 40.0,
            mediumRiskDaysMin = 14,
            mediumRiskDaysMax = 45,
            mediumRiskLockedPctMin = 40.0,
            mediumRiskLockedPctMax = 70.0,
            lowRiskDays = 45,
            lowRiskLockedPct = 70.0,
            criticalLpAmountUsd = 1_500_000.0
        ),
        taxes = TaxThresholds(
            highRiskPct = 8.0,
            mediumRiskPctMin = 3.0,
            mediumRiskPctMax = 8.0,
            lowRiskPct = 3.0,
            criticalTaxDeltaPp24h = 7.0
        ),
        topHolders = TopHoldersThresholds(
            highRiskTop10Pct = 60.0,
            highRiskSingleAddrPct = 25.0,
            mediumRiskTop10PctMin = 40.0,
            mediumRiskTop10PctMax = 60.0,
            lowRiskTop10Pct = 40.0
        ),
        liquidity = LiquidityThresholds(
            highRiskLpUsd = 2_000_000.0,
            highRiskDepth2PctUsd = 400_000.0,
            mediumRiskLpUsdMin = 2_000_000.0,
            mediumRiskLpUsdMax = 3_000_000.0,
            lowRiskLpUsd = 3_000_000.0
        ),
        unlocks = UnlockThresholds(
            highRiskDays = 14,
            highRiskCircPct = 8.0,
            highRiskFdvPct = 4.0,
            mediumRiskDaysMin = 15,
            mediumRiskDaysMax = 30,
            mediumRiskCircPctMin = 4.0,
            mediumRiskCircPctMax = 8.0,
            lowRiskDays = 30,
            lowRiskCircPct = 4.0
        ),
        futures = FuturesThresholds(
            highRiskFundingPct8h = 0.08,
            highRiskOiIncreasePct24h = 25.0,
            mediumRiskFundingPct8hMin = 0.04,
            mediumRiskFundingPct8hMax = 0.08,
            lowRiskFundingPct8h = 0.04
        ),
        volumeAnomaly = VolumeAnomalyThresholds(
            highRiskVolDropPct = 40.0,
            highRiskPriceRisePct = 10.0,
            mediumRiskCv7d = 0.8,
            lowRiskCv7d = 0.8
        )
    )

    /**
     * Balanced preset - default/recommended thresholds
     */
    val BALANCED = AlertPolicy(
        lpLock = LpLockThresholds(
            highRiskDays = 7,
            highRiskLockedPct = 30.0,
            mediumRiskDaysMin = 7,
            mediumRiskDaysMax = 30,
            mediumRiskLockedPctMin = 30.0,
            mediumRiskLockedPctMax = 60.0,
            lowRiskDays = 30,
            lowRiskLockedPct = 60.0,
            criticalLpAmountUsd = 500_000.0
        ),
        taxes = TaxThresholds(
            highRiskPct = 10.0,
            mediumRiskPctMin = 5.0,
            mediumRiskPctMax = 10.0,
            lowRiskPct = 5.0,
            criticalTaxDeltaPp24h = 10.0
        ),
        topHolders = TopHoldersThresholds(
            highRiskTop10Pct = 70.0,
            highRiskSingleAddrPct = 30.0,
            mediumRiskTop10PctMin = 50.0,
            mediumRiskTop10PctMax = 70.0,
            lowRiskTop10Pct = 50.0
        ),
        liquidity = LiquidityThresholds(
            highRiskLpUsd = 1_000_000.0,
            highRiskDepth2PctUsd = 250_000.0,
            mediumRiskLpUsdMin = 1_000_000.0,
            mediumRiskLpUsdMax = 2_000_000.0,
            lowRiskLpUsd = 2_000_000.0
        ),
        unlocks = UnlockThresholds(
            highRiskDays = 7,
            highRiskCircPct = 10.0,
            highRiskFdvPct = 5.0,
            mediumRiskDaysMin = 8,
            mediumRiskDaysMax = 21,
            mediumRiskCircPctMin = 5.0,
            mediumRiskCircPctMax = 10.0,
            lowRiskDays = 21,
            lowRiskCircPct = 5.0
        ),
        futures = FuturesThresholds(
            highRiskFundingPct8h = 0.10,
            highRiskOiIncreasePct24h = 30.0,
            mediumRiskFundingPct8hMin = 0.05,
            mediumRiskFundingPct8hMax = 0.10,
            lowRiskFundingPct8h = 0.05
        ),
        volumeAnomaly = VolumeAnomalyThresholds(
            highRiskVolDropPct = 50.0,
            highRiskPriceRisePct = 15.0,
            mediumRiskCv7d = 1.0,
            lowRiskCv7d = 1.0
        )
    )

    /**
     * Aggressive preset - more tolerant of risk
     */
    val AGGRESSIVE = AlertPolicy(
        lpLock = LpLockThresholds(
            highRiskDays = 3,
            highRiskLockedPct = 20.0,
            mediumRiskDaysMin = 3,
            mediumRiskDaysMax = 14,
            mediumRiskLockedPctMin = 20.0,
            mediumRiskLockedPctMax = 40.0,
            lowRiskDays = 14,
            lowRiskLockedPct = 40.0,
            criticalLpAmountUsd = 250_000.0
        ),
        taxes = TaxThresholds(
            highRiskPct = 15.0,
            mediumRiskPctMin = 7.0,
            mediumRiskPctMax = 15.0,
            lowRiskPct = 7.0,
            criticalTaxDeltaPp24h = 15.0
        ),
        topHolders = TopHoldersThresholds(
            highRiskTop10Pct = 80.0,
            highRiskSingleAddrPct = 40.0,
            mediumRiskTop10PctMin = 60.0,
            mediumRiskTop10PctMax = 80.0,
            lowRiskTop10Pct = 60.0
        ),
        liquidity = LiquidityThresholds(
            highRiskLpUsd = 600_000.0,
            highRiskDepth2PctUsd = 150_000.0,
            mediumRiskLpUsdMin = 600_000.0,
            mediumRiskLpUsdMax = 1_500_000.0,
            lowRiskLpUsd = 1_500_000.0
        ),
        unlocks = UnlockThresholds(
            highRiskDays = 7,
            highRiskCircPct = 15.0,
            highRiskFdvPct = 7.0,
            mediumRiskDaysMin = 8,
            mediumRiskDaysMax = 21,
            mediumRiskCircPctMin = 8.0,
            mediumRiskCircPctMax = 15.0,
            lowRiskDays = 21,
            lowRiskCircPct = 8.0
        ),
        futures = FuturesThresholds(
            highRiskFundingPct8h = 0.15,
            highRiskOiIncreasePct24h = 40.0,
            mediumRiskFundingPct8hMin = 0.08,
            mediumRiskFundingPct8hMax = 0.15,
            lowRiskFundingPct8h = 0.08
        ),
        volumeAnomaly = VolumeAnomalyThresholds(
            highRiskVolDropPct = 60.0,
            highRiskPriceRisePct = 20.0,
            mediumRiskCv7d = 1.2,
            lowRiskCv7d = 1.2
        )
    )

    /**
     * Get preset by name
     */
    fun getPreset(name: String): AlertPolicy {
        return when (name.uppercase()) {
            "CONSERVATIVE" -> CONSERVATIVE
            "BALANCED" -> BALANCED
            "AGGRESSIVE" -> AGGRESSIVE
            else -> BALANCED
        }
    }

    /**
     * Get all preset names
     */
    fun getPresetNames(): List<String> = listOf("CONSERVATIVE", "BALANCED", "AGGRESSIVE")
}
