package com.eurotax.supplylens.core.domain.policy

/**
 * Alert severity levels
 */
enum class AlertSeverity {
    LOW,
    MEDIUM,
    HIGH,
    CRITICAL
}

/**
 * Alert policy configuration for risk assessment
 * Defines thresholds for different risk indicators
 */
data class AlertPolicy(
    val lpLock: LpLockThresholds = LpLockThresholds(),
    val taxes: TaxThresholds = TaxThresholds(),
    val topHolders: TopHoldersThresholds = TopHoldersThresholds(),
    val liquidity: LiquidityThresholds = LiquidityThresholds(),
    val unlocks: UnlockThresholds = UnlockThresholds(),
    val futures: FuturesThresholds = FuturesThresholds(),
    val volumeAnomaly: VolumeAnomalyThresholds = VolumeAnomalyThresholds()
)

data class LpLockThresholds(
    // High risk: lock duration < X days OR locked % < Y%
    val highRiskDays: Int = 7,
    val highRiskLockedPct: Double = 30.0,
    // Medium risk: lock duration X-Y days OR locked % Y-Z%
    val mediumRiskDaysMin: Int = 7,
    val mediumRiskDaysMax: Int = 30,
    val mediumRiskLockedPctMin: Double = 30.0,
    val mediumRiskLockedPctMax: Double = 60.0,
    // Low risk: >= Y days AND >= Z%
    val lowRiskDays: Int = 30,
    val lowRiskLockedPct: Double = 60.0,
    // Critical: no lock and LP < $X
    val criticalLpAmountUsd: Double = 500_000.0
)

data class TaxThresholds(
    // High risk: tax > X%
    val highRiskPct: Double = 10.0,
    // Medium risk: tax X-Y%
    val mediumRiskPctMin: Double = 5.0,
    val mediumRiskPctMax: Double = 10.0,
    // Low risk: tax <= X%
    val lowRiskPct: Double = 5.0,
    // Critical: delta tax >= X percentage points within 24h
    val criticalTaxDeltaPp24h: Double = 10.0
)

data class TopHoldersThresholds(
    // High risk: top10 > X% OR 1 address > Y%
    val highRiskTop10Pct: Double = 70.0,
    val highRiskSingleAddrPct: Double = 30.0,
    // Medium risk: top10 X-Y%
    val mediumRiskTop10PctMin: Double = 50.0,
    val mediumRiskTop10PctMax: Double = 70.0,
    // Low risk: top10 < X%
    val lowRiskTop10Pct: Double = 50.0
)

data class LiquidityThresholds(
    // High risk: LP < $X OR depth2% < $Y
    val highRiskLpUsd: Double = 1_000_000.0,
    val highRiskDepth2PctUsd: Double = 250_000.0,
    // Medium risk: LP $X-$Y
    val mediumRiskLpUsdMin: Double = 1_000_000.0,
    val mediumRiskLpUsdMax: Double = 2_000_000.0,
    // Low risk: LP > $X
    val lowRiskLpUsd: Double = 2_000_000.0
)

data class UnlockThresholds(
    // High risk: <= X days AND (> Y% circ OR > Z% FDV)
    val highRiskDays: Int = 7,
    val highRiskCircPct: Double = 10.0,
    val highRiskFdvPct: Double = 5.0,
    // Medium risk: X-Y days AND Z-W% circ
    val mediumRiskDaysMin: Int = 8,
    val mediumRiskDaysMax: Int = 21,
    val mediumRiskCircPctMin: Double = 5.0,
    val mediumRiskCircPctMax: Double = 10.0,
    // Low risk: > X days OR < Y% circ
    val lowRiskDays: Int = 21,
    val lowRiskCircPct: Double = 5.0
)

data class FuturesThresholds(
    // High risk: |funding| > X%/8h OR OI +Y%/24h
    val highRiskFundingPct8h: Double = 0.10,
    val highRiskOiIncreasePct24h: Double = 30.0,
    // Medium risk: |funding| X-Y%
    val mediumRiskFundingPct8hMin: Double = 0.05,
    val mediumRiskFundingPct8hMax: Double = 0.10,
    // Low risk: |funding| < X%
    val lowRiskFundingPct8h: Double = 0.05
)

data class VolumeAnomalyThresholds(
    // High risk: > X% d/d drop with > Y% price rise
    val highRiskVolDropPct: Double = 50.0,
    val highRiskPriceRisePct: Double = 15.0,
    // Medium risk: CV > X (7d)
    val mediumRiskCv7d: Double = 1.0,
    // Low risk: CV <= X
    val lowRiskCv7d: Double = 1.0
)
