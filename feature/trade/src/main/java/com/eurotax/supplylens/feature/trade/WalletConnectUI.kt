package com.eurotax.supplylens.feature.trade

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.eurotax.supplylens.core.network.wallet.WalletSessionState

/**
 * Wallet connection UI component
 */
@Composable
fun WalletConnectButton(
    sessionState: WalletSessionState,
    onConnect: () -> Unit,
    onDisconnect: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (sessionState.isConnected) {
        OutlinedButton(
            onClick = onDisconnect,
            modifier = modifier
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    modifier = Modifier.size(8.dp),
                    color = MaterialTheme.colorScheme.primary,
                    shape = CircleShape
                ) {}
                Text("Disconnect")
            }
        }
    } else {
        Button(
            onClick = onConnect,
            modifier = modifier
        ) {
            Text("Connect Wallet")
        }
    }
}

/**
 * Wallet session indicator showing connection status and details
 */
@Composable
fun WalletSessionIndicator(
    sessionState: WalletSessionState,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = if (sessionState.isConnected) {
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        } else {
            CardDefaults.cardColors()
        }
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Wallet Status",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Surface(
                    modifier = Modifier.size(12.dp),
                    color = if (sessionState.isConnected) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    },
                    shape = CircleShape
                ) {}
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (sessionState.isConnected) {
                sessionState.address?.let { address ->
                    Text(
                        text = "Address:",
                        style = MaterialTheme.typography.labelMedium
                    )
                    Text(
                        text = formatAddress(address),
                        style = MaterialTheme.typography.bodyMedium,
                        fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                    )
                }

                sessionState.chainId?.let { chainId ->
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Chain ID: $chainId",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            } else {
                Text(
                    text = "Not connected",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

/**
 * Trade screen with wallet integration
 */
@Composable
fun TradeScreen(
    sessionState: WalletSessionState,
    onConnect: () -> Unit,
    onDisconnect: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Trade",
            style = MaterialTheme.typography.headlineMedium
        )

        // Wallet connection section
        WalletSessionIndicator(sessionState = sessionState)

        WalletConnectButton(
            sessionState = sessionState,
            onConnect = onConnect,
            onDisconnect = onDisconnect,
            modifier = Modifier.fillMaxWidth()
        )

        // Trading interface (only shown when wallet is connected)
        if (sessionState.isConnected) {
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Trading Interface",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Place orders, manage positions, and execute trades",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { /* Navigate to trading interface */ },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Open Trading Interface")
                    }
                }
            }
        } else {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Connect your wallet to start trading",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

/**
 * Format wallet address for display (truncate middle)
 */
private fun formatAddress(address: String): String {
    return if (address.length > 13) {
        "${address.take(6)}...${address.takeLast(4)}"
    } else {
        address
    }
}
