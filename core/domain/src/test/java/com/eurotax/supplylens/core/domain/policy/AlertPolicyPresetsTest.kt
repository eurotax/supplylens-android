package com.eurotax.supplylens.core.domain.policy

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Unit tests for AlertPolicyPresets
 */
class AlertPolicyPresetsTest {

    @Test
    fun `getPreset returns CONSERVATIVE for conservative name`() {
        val preset = AlertPolicyPresets.getPreset("CONSERVATIVE")
        assertEquals(AlertPolicyPresets.CONSERVATIVE, preset)
    }

    @Test
    fun `getPreset returns BALANCED for balanced name`() {
        val preset = AlertPolicyPresets.getPreset("BALANCED")
        assertEquals(AlertPolicyPresets.BALANCED, preset)
    }

    @Test
    fun `getPreset returns AGGRESSIVE for aggressive name`() {
        val preset = AlertPolicyPresets.getPreset("AGGRESSIVE")
        assertEquals(AlertPolicyPresets.AGGRESSIVE, preset)
    }

    @Test
    fun `getPreset returns BALANCED for unknown name`() {
        val preset = AlertPolicyPresets.getPreset("UNKNOWN")
        assertEquals(AlertPolicyPresets.BALANCED, preset)
    }

    @Test
    fun `getPreset is case insensitive`() {
        val preset1 = AlertPolicyPresets.getPreset("conservative")
        val preset2 = AlertPolicyPresets.getPreset("CoNsErVaTiVe")
        assertEquals(AlertPolicyPresets.CONSERVATIVE, preset1)
        assertEquals(AlertPolicyPresets.CONSERVATIVE, preset2)
    }

    @Test
    fun `getPresetNames returns all preset names`() {
        val names = AlertPolicyPresets.getPresetNames()
        assertEquals(3, names.size)
        assertTrue(names.contains("CONSERVATIVE"))
        assertTrue(names.contains("BALANCED"))
        assertTrue(names.contains("AGGRESSIVE"))
    }

    @Test
    fun `CONSERVATIVE has stricter LP lock thresholds than BALANCED`() {
        val conservative = AlertPolicyPresets.CONSERVATIVE
        val balanced = AlertPolicyPresets.BALANCED

        assertTrue(conservative.lpLock.highRiskDays > balanced.lpLock.highRiskDays)
        assertTrue(conservative.lpLock.highRiskLockedPct > balanced.lpLock.highRiskLockedPct)
        assertTrue(conservative.lpLock.criticalLpAmountUsd > balanced.lpLock.criticalLpAmountUsd)
    }

    @Test
    fun `CONSERVATIVE has stricter tax thresholds than BALANCED`() {
        val conservative = AlertPolicyPresets.CONSERVATIVE
        val balanced = AlertPolicyPresets.BALANCED

        assertTrue(conservative.taxes.highRiskPct < balanced.taxes.highRiskPct)
        assertTrue(conservative.taxes.lowRiskPct < balanced.taxes.lowRiskPct)
    }

    @Test
    fun `AGGRESSIVE has more tolerant LP lock thresholds than BALANCED`() {
        val aggressive = AlertPolicyPresets.AGGRESSIVE
        val balanced = AlertPolicyPresets.BALANCED

        assertTrue(aggressive.lpLock.highRiskDays < balanced.lpLock.highRiskDays)
        assertTrue(aggressive.lpLock.highRiskLockedPct < balanced.lpLock.highRiskLockedPct)
        assertTrue(aggressive.lpLock.criticalLpAmountUsd < balanced.lpLock.criticalLpAmountUsd)
    }

    @Test
    fun `AGGRESSIVE has more tolerant tax thresholds than BALANCED`() {
        val aggressive = AlertPolicyPresets.AGGRESSIVE
        val balanced = AlertPolicyPresets.BALANCED

        assertTrue(aggressive.taxes.highRiskPct > balanced.taxes.highRiskPct)
        assertTrue(aggressive.taxes.lowRiskPct > balanced.taxes.lowRiskPct)
    }

    @Test
    fun `BALANCED preset has expected default values`() {
        val balanced = AlertPolicyPresets.BALANCED

        assertEquals(7, balanced.lpLock.highRiskDays)
        assertEquals(30.0, balanced.lpLock.highRiskLockedPct, 0.01)
        assertEquals(500_000.0, balanced.lpLock.criticalLpAmountUsd, 0.01)

        assertEquals(10.0, balanced.taxes.highRiskPct, 0.01)
        assertEquals(5.0, balanced.taxes.lowRiskPct, 0.01)

        assertEquals(70.0, balanced.topHolders.highRiskTop10Pct, 0.01)
        assertEquals(30.0, balanced.topHolders.highRiskSingleAddrPct, 0.01)
    }

    @Test
    fun `all presets have valid threshold ranges`() {
        val presets = listOf(
            AlertPolicyPresets.CONSERVATIVE,
            AlertPolicyPresets.BALANCED,
            AlertPolicyPresets.AGGRESSIVE
        )

        presets.forEach { preset ->
            // LP Lock
            assertTrue(preset.lpLock.highRiskDays > 0)
            assertTrue(preset.lpLock.lowRiskDays >= preset.lpLock.mediumRiskDaysMax)
            assertTrue(preset.lpLock.highRiskLockedPct >= 0.0)
            assertTrue(preset.lpLock.lowRiskLockedPct <= 100.0)

            // Taxes
            assertTrue(preset.taxes.lowRiskPct >= 0.0)
            assertTrue(preset.taxes.highRiskPct <= 100.0)
            assertTrue(preset.taxes.lowRiskPct <= preset.taxes.mediumRiskPctMin)

            // Top Holders
            assertTrue(preset.topHolders.lowRiskTop10Pct >= 0.0)
            assertTrue(preset.topHolders.highRiskTop10Pct <= 100.0)

            // Liquidity
            assertTrue(preset.liquidity.highRiskLpUsd > 0.0)
            assertTrue(preset.liquidity.lowRiskLpUsd >= preset.liquidity.mediumRiskLpUsdMax)
        }
    }

    @Test
    fun `AlertPolicy can be instantiated with default values`() {
        val policy = AlertPolicy()
        assertNotNull(policy.lpLock)
        assertNotNull(policy.taxes)
        assertNotNull(policy.topHolders)
        assertNotNull(policy.liquidity)
        assertNotNull(policy.unlocks)
        assertNotNull(policy.futures)
        assertNotNull(policy.volumeAnomaly)
    }
}
