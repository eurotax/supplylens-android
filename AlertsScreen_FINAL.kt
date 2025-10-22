package com.eurotax.supplylens.feature.alerts

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.eurotax.supplylens.core.billing.SubscriptionStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlertsScreen(
    subscriptionStatus: SubscriptionStatus = SubscriptionStatus.FREE,
    onUpgradeClick: () -> Unit = {}
) {
    val viewModel: AlertsViewModel = viewModel()
    val uiState by viewModel.uiState.collectAsState()
    
    val canEdit = subscriptionStatus == SubscriptionStatus.PRO || 
                  subscriptionStatus == SubscriptionStatus.TRIAL_PRO
    
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Alert Thresholds") })
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = if (canEdit) {
                            MaterialTheme.colorScheme.secondaryContainer
                        } else {
                            MaterialTheme.colorScheme.primaryContainer
                        }
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = if (canEdit) {
                                "PRO: Edit thresholds"
                            } else {
                                "FREE: View only"
                            },
                            style = MaterialTheme.typography.titleMedium
                        )
                        
                        if (!canEdit) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Button(
                                onClick = onUpgradeClick,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Upgrade to PRO to edit")
                            }
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
            }
            
            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            "Current Preset: ${uiState.currentPreset.displayName}",
                            style = MaterialTheme.typography.titleMedium
                        )
                        
                        if (canEdit) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                AlertPreset.values().forEach { preset ->
                                    FilterChip(
                                        selected = preset == uiState.currentPreset,
                                        onClick = { viewModel.selectPreset(preset) },
                                        label = { Text(preset.displayName) }
                                    )
                                }
                            }
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
            }
            
            item {
                Text(
                    "Threshold Categories",
                    style = MaterialTheme.typography.titleLarge
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
            
            items(uiState.thresholds) { threshold ->
                ThresholdCategoryCard(
                    threshold = threshold,
                    canEdit = canEdit,
                    onEditClick = { }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThresholdCategoryCard(
    threshold: AlertThreshold,
    canEdit: Boolean,
    onEditClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        onClick = if (canEdit) onEditClick else {}
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = threshold.category,
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = threshold.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
            
            Text(
                text = threshold.value,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

class AlertsViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(AlertsUiState())
    val uiState: StateFlow<AlertsUiState> = _uiState.asStateFlow()
    
    init {
        loadDefaultThresholds()
    }
    
    fun selectPreset(preset: AlertPreset) {
        _uiState.value = _uiState.value.copy(currentPreset = preset)
        loadThresholdsForPreset(preset)
    }
    
    private fun loadDefaultThresholds() {
        val thresholds = listOf(
            AlertThreshold("LP Lock", "Minimum liquidity lock duration", "30 days"),
            AlertThreshold("Taxes", "Maximum buy/sell tax percentage", "10%"),
            AlertThreshold("Top Holders", "Maximum single holder concentration", "5%"),
            AlertThreshold("Liquidity", "Minimum liquidity pool size", "$50K"),
            AlertThreshold("Unlocks", "Token unlock schedule monitoring", "Enabled"),
            AlertThreshold("Futures", "Funding rate anomaly detection", "±0.1%"),
            AlertThreshold("Volume Anomaly", "Unusual volume spike detection", "5x avg")
        )
        
        _uiState.value = _uiState.value.copy(thresholds = thresholds)
    }
    
    private fun loadThresholdsForPreset(preset: AlertPreset) {
        loadDefaultThresholds()
    }
}

data class AlertsUiState(
    val currentPreset: AlertPreset = AlertPreset.BALANCED,
    val thresholds: List<AlertThreshold> = emptyList()
)

data class AlertThreshold(
    val category: String,
    val description: String,
    val value: String
)

enum class AlertPreset(val displayName: String) {
    CONSERVATIVE("Conservative"),
    BALANCED("Balanced"),
    AGGRESSIVE("Aggressive")
}
