package com.eurotax.supplylens.feature.forecast

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.eurotax.supplylens.core.ai.AiForecastEngine
import com.eurotax.supplylens.core.ai.ForecastResult
import com.eurotax.supplylens.core.billing.SubscriptionStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiForecastScreen(
    subscriptionStatus: SubscriptionStatus = SubscriptionStatus.FREE,
    forecastEngine: AiForecastEngine? = null,
    onUpgradeClick: () -> Unit = {}
) {
    val viewModel: ForecastViewModel = viewModel()
    val uiState by viewModel.uiState.collectAsState()
    
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("AI Forecast") })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            QuotaCard(subscriptionStatus, uiState.daysUntilNext)
            Spacer(modifier = Modifier.height(24.dp))
            
            OutlinedTextField(
                value = uiState.tokenSymbol,
                onValueChange = { viewModel.updateTokenSymbol(it) },
                label = { Text("Token Symbol") },
                placeholder = { Text("e.g., ETH, BTC") },
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Button(
                onClick = {
                    if (forecastEngine?.canGenerateForecast(subscriptionStatus) == true) {
                        viewModel.generateForecast(forecastEngine, subscriptionStatus)
                    } else {
                        onUpgradeClick()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !uiState.isLoading && uiState.tokenSymbol.isNotBlank()
            ) {
                Text(if (uiState.isLoading) "Generating..." else "Generate Forecast")
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            when {
                uiState.isLoading -> CircularProgressIndicator()
                uiState.forecast != null -> ForecastCard(uiState.forecast!!)
                uiState.error != null -> Text(
                    text = uiState.error!!,
                    color = MaterialTheme.colorScheme.error,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun QuotaCard(status: SubscriptionStatus, daysUntilNext: Int) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = when (status) {
                    SubscriptionStatus.FREE -> "FREE: 1 forecast per 7 days"
                    SubscriptionStatus.TRIAL_PRO, SubscriptionStatus.PRO -> "PRO: Unlimited forecasts"
                    SubscriptionStatus.EXPIRED -> "Subscription expired"
                },
                style = MaterialTheme.typography.titleMedium
            )
            if (status == SubscriptionStatus.FREE && daysUntilNext > 0) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Next forecast available in $daysUntilNext days",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
        }
    }
}

@Composable
fun ForecastCard(forecast: ForecastResult) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "AI Forecast for ${forecast.tokenSymbol}",
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = forecast.prediction,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

class ForecastViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(ForecastUiState())
    val uiState: StateFlow<ForecastUiState> = _uiState.asStateFlow()
    
    fun updateTokenSymbol(symbol: String) {
        _uiState.value = _uiState.value.copy(tokenSymbol = symbol)
    }
    
    fun generateForecast(engine: AiForecastEngine, status: SubscriptionStatus) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                val forecast = engine.generateForecast(
                    tokenSymbol = _uiState.value.tokenSymbol.uppercase(),
                    currentPrice = 2500.0,
                    volume24h = 15000000000.0
                )
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    forecast = forecast,
                    daysUntilNext = engine.getDaysUntilNextForecast()
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Unknown error"
                )
            }
        }
    }
}

data class ForecastUiState(
    val tokenSymbol: String = "",
    val isLoading: Boolean = false,
    val forecast: ForecastResult? = null,
    val error: String? = null,
    val daysUntilNext: Int = 0
)
