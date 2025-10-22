package com.eurotax.supplylens.feature.watchlist

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
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
fun WatchlistScreen(
    subscriptionStatus: SubscriptionStatus = SubscriptionStatus.FREE,
    onSearchClick: () -> Unit = {},
    onUpgradeClick: () -> Unit = {},
    onTokenClick: (String, String) -> Unit = { _, _ -> }
) {
    val viewModel: WatchlistViewModel = viewModel()
    val uiState by viewModel.uiState.collectAsState()
    
    val maxTokens = when (subscriptionStatus) {
        SubscriptionStatus.FREE -> 1
        else -> 50
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Watchlist") },
                actions = {
                    IconButton(
                        onClick = {
                            if (uiState.tokens.size < maxTokens) {
                                onSearchClick()
                            } else {
                                onUpgradeClick()
                            }
                        }
                    ) {
                        Icon(Icons.Default.Add, "Add token")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = if (subscriptionStatus == SubscriptionStatus.FREE) {
                        MaterialTheme.colorScheme.primaryContainer
                    } else {
                        MaterialTheme.colorScheme.secondaryContainer
                    }
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = if (subscriptionStatus == SubscriptionStatus.FREE) {
                            "FREE: 1 token"
                        } else {
                            "PRO: Up to 50 tokens"
                        },
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${uiState.tokens.size} / $maxTokens tokens watched",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    
                    if (subscriptionStatus == SubscriptionStatus.FREE) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = onUpgradeClick,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Upgrade to PRO")
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            if (uiState.tokens.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            "No tokens in watchlist",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "Tap + to add a token",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                }
            } else {
                LazyColumn {
                    items(uiState.tokens) { token ->
                        WatchlistTokenItem(
                            token = token,
                            onTokenClick = { onTokenClick(token.chain, token.address) },
                            onRemoveClick = { viewModel.removeToken(token.id) }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WatchlistTokenItem(
    token: WatchlistToken,
    onTokenClick: () -> Unit,
    onRemoveClick: () -> Unit
) {
    Card(
        onClick = onTokenClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "${token.symbol} - ${token.name}",
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Price: $${String.format("%.2f", token.price)}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
            
            IconButton(onClick = onRemoveClick) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Remove",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

class WatchlistViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(WatchlistUiState())
    val uiState: StateFlow<WatchlistUiState> = _uiState.asStateFlow()
    
    fun removeToken(tokenId: String) {
        _uiState.value = _uiState.value.copy(
            tokens = _uiState.value.tokens.filter { it.id != tokenId }
        )
    }
    
    fun addToken(token: WatchlistToken) {
        _uiState.value = _uiState.value.copy(
            tokens = _uiState.value.tokens + token
        )
    }
}

data class WatchlistUiState(
    val tokens: List<WatchlistToken> = emptyList()
)

data class WatchlistToken(
    val id: String,
    val symbol: String,
    val name: String,
    val chain: String,
    val address: String,
    val price: Double
)
