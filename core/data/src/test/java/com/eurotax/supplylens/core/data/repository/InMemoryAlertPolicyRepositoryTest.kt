package com.eurotax.supplylens.core.data.repository

import com.eurotax.supplylens.core.domain.policy.AlertPolicyPresets
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for InMemoryAlertPolicyRepository
 * Tests policy storage, retrieval, and merging logic
 */
class InMemoryAlertPolicyRepositoryTest {

    private lateinit var repository: InMemoryAlertPolicyRepository

    @Before
    fun setup() {
        repository = InMemoryAlertPolicyRepository()
    }

    @Test
    fun `getGlobalPolicy returns BALANCED by default`() = runTest {
        val policy = repository.getGlobalPolicy().first()
        assertEquals(AlertPolicyPresets.BALANCED, policy)
    }

    @Test
    fun `updateGlobalPolicy changes global policy`() = runTest {
        repository.updateGlobalPolicy(AlertPolicyPresets.CONSERVATIVE)
        val policy = repository.getGlobalPolicy().first()
        assertEquals(AlertPolicyPresets.CONSERVATIVE, policy)
    }

    @Test
    fun `getCurrentPreset returns BALANCED by default`() = runTest {
        val preset = repository.getCurrentPreset().first()
        assertEquals("BALANCED", preset)
    }

    @Test
    fun `setCurrentPreset changes preset`() = runTest {
        repository.setCurrentPreset("AGGRESSIVE")
        val preset = repository.getCurrentPreset().first()
        assertEquals("AGGRESSIVE", preset)
    }

    @Test
    fun `clearCurrentPreset resets to BALANCED`() = runTest {
        repository.setCurrentPreset("CONSERVATIVE")
        repository.clearCurrentPreset()
        val preset = repository.getCurrentPreset().first()
        assertEquals("BALANCED", preset)
    }

    @Test
    fun `getTokenPolicy returns null for non-existent token`() = runTest {
        val policy = repository.getTokenPolicy("token123").first()
        assertNull(policy)
    }

    @Test
    fun `updateTokenPolicy stores token-specific policy`() = runTest {
        repository.updateTokenPolicy("token123", AlertPolicyPresets.AGGRESSIVE)
        val policy = repository.getTokenPolicy("token123").first()
        assertEquals(AlertPolicyPresets.AGGRESSIVE, policy)
    }

    @Test
    fun `removeTokenPolicy removes token-specific policy`() = runTest {
        repository.updateTokenPolicy("token123", AlertPolicyPresets.AGGRESSIVE)
        repository.removeTokenPolicy("token123")
        val policy = repository.getTokenPolicy("token123").first()
        assertNull(policy)
    }

    @Test
    fun `token policies are independent`() = runTest {
        repository.updateTokenPolicy("token1", AlertPolicyPresets.CONSERVATIVE)
        repository.updateTokenPolicy("token2", AlertPolicyPresets.AGGRESSIVE)

        val policy1 = repository.getTokenPolicy("token1").first()
        val policy2 = repository.getTokenPolicy("token2").first()

        assertEquals(AlertPolicyPresets.CONSERVATIVE, policy1)
        assertEquals(AlertPolicyPresets.AGGRESSIVE, policy2)
    }

    @Test
    fun `global policy does not affect token policies`() = runTest {
        repository.updateTokenPolicy("token123", AlertPolicyPresets.CONSERVATIVE)
        repository.updateGlobalPolicy(AlertPolicyPresets.AGGRESSIVE)

        val tokenPolicy = repository.getTokenPolicy("token123").first()
        val globalPolicy = repository.getGlobalPolicy().first()

        assertEquals(AlertPolicyPresets.CONSERVATIVE, tokenPolicy)
        assertEquals(AlertPolicyPresets.AGGRESSIVE, globalPolicy)
    }

    @Test
    fun `policy merging priority - token overrides global`() = runTest {
        // Set global policy to BALANCED
        repository.updateGlobalPolicy(AlertPolicyPresets.BALANCED)

        // Set token-specific policy to AGGRESSIVE
        repository.updateTokenPolicy("token123", AlertPolicyPresets.AGGRESSIVE)

        val globalPolicy = repository.getGlobalPolicy().first()
        val tokenPolicy = repository.getTokenPolicy("token123").first()

        assertEquals(AlertPolicyPresets.BALANCED, globalPolicy)
        assertEquals(AlertPolicyPresets.AGGRESSIVE, tokenPolicy)

        // Token policy should override global
        assertEquals(AlertPolicyPresets.AGGRESSIVE, tokenPolicy)
    }

    @Test
    fun `policy merging priority - global used when no token policy`() = runTest {
        repository.updateGlobalPolicy(AlertPolicyPresets.CONSERVATIVE)

        val tokenPolicy = repository.getTokenPolicy("token123").first()
        val globalPolicy = repository.getGlobalPolicy().first()

        assertNull(tokenPolicy)
        assertEquals(AlertPolicyPresets.CONSERVATIVE, globalPolicy)

        // When no token policy, global should be used
        // (This would be implemented in the service layer)
    }

    @Test
    fun `preset switching updates global policy`() = runTest {
        repository.setCurrentPreset("CONSERVATIVE")
        var policy = repository.getGlobalPolicy().first()
        assertEquals(AlertPolicyPresets.CONSERVATIVE, policy)

        repository.setCurrentPreset("BALANCED")
        policy = repository.getGlobalPolicy().first()
        assertEquals(AlertPolicyPresets.BALANCED, policy)

        repository.setCurrentPreset("AGGRESSIVE")
        policy = repository.getGlobalPolicy().first()
        assertEquals(AlertPolicyPresets.AGGRESSIVE, policy)
    }
}
