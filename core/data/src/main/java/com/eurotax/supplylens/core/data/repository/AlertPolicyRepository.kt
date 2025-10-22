package com.eurotax.supplylens.core.data.repository

import com.eurotax.supplylens.core.domain.policy.AlertPolicy
import kotlinx.coroutines.flow.Flow

/**
 * Repository for managing alert policy configuration
 */
interface AlertPolicyRepository {

    /**
     * Get the current global alert policy
     */
    fun getGlobalPolicy(): Flow<AlertPolicy>

    /**
     * Update the global alert policy
     */
    suspend fun updateGlobalPolicy(policy: AlertPolicy)

    /**
     * Get policy for a specific token (if overridden)
     * Returns null if no token-specific policy exists
     */
    fun getTokenPolicy(tokenId: String): Flow<AlertPolicy?>

    /**
     * Set a token-specific policy override
     */
    suspend fun updateTokenPolicy(tokenId: String, policy: AlertPolicy)

    /**
     * Remove token-specific policy override
     */
    suspend fun removeTokenPolicy(tokenId: String)

    /**
     * Get the current preset name (CONSERVATIVE, BALANCED, AGGRESSIVE)
     * Returns null if using custom settings
     */
    fun getCurrentPreset(): Flow<String?>

    /**
     * Set the current preset
     */
    suspend fun setCurrentPreset(presetName: String)

    /**
     * Clear the current preset (switch to custom)
     */
    suspend fun clearCurrentPreset()
}
