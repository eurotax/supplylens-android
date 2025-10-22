package com.eurotax.supplylens.feature.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.eurotax.supplylens.core.domain.policy.AlertPolicy
import com.eurotax.supplylens.core.domain.policy.AlertPolicyPresets

/**
 * Alert Thresholds Settings Screen
 * Shows FREE (read-only) or PRO (editable) view
 */
@Composable
fun AlertThresholdsScreen(
    isPro: Boolean = false,
    currentPreset: String = "BALANCED",
    onPresetSelected: (String) -> Unit = {},
    onRestoreDefaults: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Alert Thresholds",
            style = MaterialTheme.typography.headlineMedium
        )

        CurrentPresetCard(currentPreset = currentPreset)

        PresetSelectionSection(
            isPro = isPro,
            currentPreset = currentPreset,
            onPresetSelected = onPresetSelected
        )

        RestoreDefaultsButton(onRestoreDefaults = onRestoreDefaults)

        if (isPro) {
            PerTokenOverrideSwitch()
        }

        ThresholdSummaryCard(
            preset = AlertPolicyPresets.getPreset(currentPreset),
            isEditable = isPro
        )
    }
}

@Composable
private fun CurrentPresetCard(currentPreset: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Current Preset",
                style = MaterialTheme.typography.labelMedium
            )
            Text(
                text = currentPreset,
                style = MaterialTheme.typography.titleLarge
            )
        }
    }
}

@Composable
private fun PresetSelectionSection(
    isPro: Boolean,
    currentPreset: String,
    onPresetSelected: (String) -> Unit
) {
    if (isPro) {
        Text(
            text = "Select Preset",
            style = MaterialTheme.typography.titleMedium
        )

        AlertPolicyPresets.getPresetNames().forEach { preset ->
            PresetButton(
                name = preset,
                isSelected = preset == currentPreset,
                onClick = { onPresetSelected(preset) }
            )
        }
    } else {
        ProFeatureCard()
    }
}

@Composable
private fun ProFeatureCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "PRO Feature",
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = "Upgrade to PRO to unlock preset selection and custom threshold editing",
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = { /* Navigate to upgrade */ }) {
                Text("Upgrade to PRO")
            }
        }
    }
}

@Composable
private fun RestoreDefaultsButton(onRestoreDefaults: () -> Unit) {
    OutlinedButton(
        onClick = onRestoreDefaults,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text("Restore Defaults")
    }
}

@Composable
private fun PerTokenOverrideSwitch() {
    var perTokenOverride by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("Apply to this token only")
        Switch(
            checked = perTokenOverride,
            onCheckedChange = { perTokenOverride = it }
        )
    }
}

@Composable
private fun PresetButton(
    name: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val buttonModifier = Modifier.fillMaxWidth()

    if (isSelected) {
        Button(onClick = onClick, modifier = buttonModifier) {
            Text(name)
        }
    } else {
        OutlinedButton(onClick = onClick, modifier = buttonModifier) {
            Text(name)
        }
    }
}

@Composable
private fun ThresholdSummaryCard(
    preset: AlertPolicy,
    isEditable: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = if (isEditable) "Edit Thresholds" else "Threshold Summary",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            // LP Lock thresholds
            ThresholdItem(
                category = "LP Lock",
                high = "< ${preset.lpLock.highRiskDays} days or < ${preset.lpLock.highRiskLockedPct}%",
                medium = "${preset.lpLock.mediumRiskDaysMin}-${preset.lpLock.mediumRiskDaysMax} days",
                low = "≥ ${preset.lpLock.lowRiskDays} days and ≥ ${preset.lpLock.lowRiskLockedPct}%"
            )

            // Tax thresholds
            ThresholdItem(
                category = "Taxes",
                high = "> ${preset.taxes.highRiskPct}%",
                medium = "${preset.taxes.mediumRiskPctMin}-${preset.taxes.mediumRiskPctMax}%",
                low = "≤ ${preset.taxes.lowRiskPct}%"
            )

            // Top holders thresholds
            ThresholdItem(
                category = "Top Holders",
                high = "> ${preset.topHolders.highRiskTop10Pct}% (top 10)",
                medium = "${preset.topHolders.mediumRiskTop10PctMin}-${preset.topHolders.mediumRiskTop10PctMax}%",
                low = "< ${preset.topHolders.lowRiskTop10Pct}%"
            )

            if (!isEditable) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Upgrade to PRO to edit individual thresholds",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
        }
    }
}

@Composable
private fun ThresholdItem(
    category: String,
    high: String,
    medium: String,
    low: String
) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(
            text = category,
            style = MaterialTheme.typography.titleSmall
        )
        Text("High: $high", style = MaterialTheme.typography.bodySmall)
        Text("Medium: $medium", style = MaterialTheme.typography.bodySmall)
        Text("Low: $low", style = MaterialTheme.typography.bodySmall)
    }
}
