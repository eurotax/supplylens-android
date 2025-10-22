package com.eurotax.supplylens.core.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.eurotax.supplylens.core.domain.policy.AlertPolicy
import com.eurotax.supplylens.core.domain.policy.AlertPolicyPresets
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.alertPolicyDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "alert_policy_preferences"
)

/**
 * DataStore-based implementation of AlertPolicyRepository
 */
class AlertPolicyRepositoryImpl(
    private val context: Context
) : AlertPolicyRepository {

    private val globalPolicyKey = stringPreferencesKey("global_policy_preset")
    private val tokenPolicyPrefix = "token_policy_"

    override fun getGlobalPolicy(): Flow<AlertPolicy> {
        return context.alertPolicyDataStore.data.map { preferences ->
            val presetName = preferences[globalPolicyKey] ?: "BALANCED"
            AlertPolicyPresets.getPreset(presetName)
        }
    }

    override suspend fun updateGlobalPolicy(policy: AlertPolicy) {
        // For simplicity, we determine which preset matches best
        // In a real implementation, you might store custom policies as JSON
        val matchingPreset = when (policy) {
            AlertPolicyPresets.CONSERVATIVE -> "CONSERVATIVE"
            AlertPolicyPresets.BALANCED -> "BALANCED"
            AlertPolicyPresets.AGGRESSIVE -> "AGGRESSIVE"
            else -> "BALANCED" // Custom policies default to balanced
        }

        context.alertPolicyDataStore.edit { preferences ->
            preferences[globalPolicyKey] = matchingPreset
        }
    }

    override fun getTokenPolicy(tokenId: String): Flow<AlertPolicy?> {
        return context.alertPolicyDataStore.data.map { preferences ->
            val key = stringPreferencesKey("$tokenPolicyPrefix$tokenId")
            preferences[key]?.let { presetName ->
                AlertPolicyPresets.getPreset(presetName)
            }
        }
    }

    override suspend fun updateTokenPolicy(tokenId: String, policy: AlertPolicy) {
        val matchingPreset = when (policy) {
            AlertPolicyPresets.CONSERVATIVE -> "CONSERVATIVE"
            AlertPolicyPresets.BALANCED -> "BALANCED"
            AlertPolicyPresets.AGGRESSIVE -> "AGGRESSIVE"
            else -> "BALANCED"
        }

        context.alertPolicyDataStore.edit { preferences ->
            val key = stringPreferencesKey("$tokenPolicyPrefix$tokenId")
            preferences[key] = matchingPreset
        }
    }

    override suspend fun removeTokenPolicy(tokenId: String) {
        context.alertPolicyDataStore.edit { preferences ->
            val key = stringPreferencesKey("$tokenPolicyPrefix$tokenId")
            preferences.remove(key)
        }
    }

    override fun getCurrentPreset(): Flow<String?> {
        return context.alertPolicyDataStore.data.map { preferences ->
            preferences[globalPolicyKey] ?: "BALANCED"
        }
    }

    override suspend fun setCurrentPreset(presetName: String) {
        context.alertPolicyDataStore.edit { preferences ->
            preferences[globalPolicyKey] = presetName
        }
    }

    override suspend fun clearCurrentPreset() {
        context.alertPolicyDataStore.edit { preferences ->
            preferences.remove(globalPolicyKey)
        }
    }
}

/**
 * In-memory implementation for testing or when DataStore is not available
 */
class InMemoryAlertPolicyRepository : AlertPolicyRepository {

    private var globalPreset = "BALANCED"
    private val tokenPolicies = mutableMapOf<String, String>()

    override fun getGlobalPolicy(): Flow<AlertPolicy> {
        return kotlinx.coroutines.flow.flowOf(AlertPolicyPresets.getPreset(globalPreset))
    }

    override suspend fun updateGlobalPolicy(policy: AlertPolicy) {
        globalPreset = when (policy) {
            AlertPolicyPresets.CONSERVATIVE -> "CONSERVATIVE"
            AlertPolicyPresets.BALANCED -> "BALANCED"
            AlertPolicyPresets.AGGRESSIVE -> "AGGRESSIVE"
            else -> "BALANCED"
        }
    }

    override fun getTokenPolicy(tokenId: String): Flow<AlertPolicy?> {
        return kotlinx.coroutines.flow.flowOf(
            tokenPolicies[tokenId]?.let { AlertPolicyPresets.getPreset(it) }
        )
    }

    override suspend fun updateTokenPolicy(tokenId: String, policy: AlertPolicy) {
        tokenPolicies[tokenId] = when (policy) {
            AlertPolicyPresets.CONSERVATIVE -> "CONSERVATIVE"
            AlertPolicyPresets.BALANCED -> "BALANCED"
            AlertPolicyPresets.AGGRESSIVE -> "AGGRESSIVE"
            else -> "BALANCED"
        }
    }

    override suspend fun removeTokenPolicy(tokenId: String) {
        tokenPolicies.remove(tokenId)
    }

    override fun getCurrentPreset(): Flow<String?> {
        return kotlinx.coroutines.flow.flowOf(globalPreset)
    }

    override suspend fun setCurrentPreset(presetName: String) {
        globalPreset = presetName
    }

    override suspend fun clearCurrentPreset() {
        globalPreset = "BALANCED"
    }
}
