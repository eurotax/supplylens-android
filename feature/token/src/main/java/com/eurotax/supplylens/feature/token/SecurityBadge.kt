package com.eurotax.supplylens.feature.token

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.eurotax.supplylens.core.domain.model.SecurityReport
import com.eurotax.supplylens.core.domain.policy.AlertSeverity

// Risk level colors - in production these should be defined in the theme
private val LowRiskColor = Color(0xFF4CAF50)
private val MediumRiskColor = Color(0xFFFFC107)
private val HighRiskColor = Color(0xFFFF5722)
private val CriticalRiskColor = Color(0xFFD32F2F)

/**
 * Security badge component for token details
 */
@Composable
fun SecurityBadge(
    severity: AlertSeverity,
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val (color, label) = when (severity) {
        AlertSeverity.LOW -> LowRiskColor to "Low Risk"
        AlertSeverity.MEDIUM -> MediumRiskColor to "Medium Risk"
        AlertSeverity.HIGH -> HighRiskColor to "High Risk"
        AlertSeverity.CRITICAL -> CriticalRiskColor to "Critical Risk"
    }

    Surface(
        onClick = onClick,
        modifier = modifier,
        color = color,
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = Color.White
            )
        }
    }
}

/**
 * Security report card showing detailed risk assessment
 */
@Composable
fun SecurityReportCard(
    report: SecurityReport,
    modifier: Modifier = Modifier
) {
    var showCriticalWarning by remember { mutableStateOf(false) }

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Security Assessment",
                    style = MaterialTheme.typography.titleMedium
                )

                val severity = when {
                    report.score >= 75 -> AlertSeverity.LOW
                    report.score >= 50 -> AlertSeverity.MEDIUM
                    report.score >= 25 -> AlertSeverity.HIGH
                    else -> AlertSeverity.CRITICAL
                }

                SecurityBadge(
                    severity = severity,
                    onClick = {
                        if (severity == AlertSeverity.CRITICAL) {
                            showCriticalWarning = true
                        }
                    }
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Security score
            Text(
                text = "Security Score: ${report.score}/100",
                style = MaterialTheme.typography.bodyMedium
            )

            // Flags
            if (report.flags.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Warnings:",
                    style = MaterialTheme.typography.labelMedium
                )
                report.flags.forEach { flag ->
                    Text(
                        text = "• $flag",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }

            // LP Lock info
            report.lpLock?.let { lpLock ->
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "LP Lock: ${lpLock.lockedPercentage}% locked",
                    style = MaterialTheme.typography.bodySmall
                )
            }

            // Tax info
            report.taxes?.let { taxes ->
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Taxes: ${taxes.buyTax}% buy / ${taxes.sellTax}% sell",
                    style = MaterialTheme.typography.bodySmall
                )
            }

            // Ownership info
            report.ownership?.let { ownership ->
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Contract: ${if (ownership.renounced) "Renounced" else "Not renounced"}",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }

    // Critical warning dialog
    if (showCriticalWarning) {
        CriticalRiskWarningDialog(
            onDismiss = { showCriticalWarning = false },
            onConfirm = { showCriticalWarning = false }
        )
    }
}

@Composable
private fun CriticalRiskWarningDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("⚠️ Critical Risk Warning")
        },
        text = {
            Column {
                Text("This token has been flagged with CRITICAL risk level.")
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "Trading this token may result in total loss of funds. " +
                        "Please review the security assessment carefully before proceeding.",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        },
        confirmButton = {
            Button(onClick = onConfirm) {
                Text("I Understand")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

/**
 * Stub function to calculate severity from security report
 * In production, this would use the AlertPolicy thresholds
 */
fun calculateSeverity(report: SecurityReport): AlertSeverity {
    return when {
        report.score >= 75 -> AlertSeverity.LOW
        report.score >= 50 -> AlertSeverity.MEDIUM
        report.score >= 25 -> AlertSeverity.HIGH
        else -> AlertSeverity.CRITICAL
    }
}
