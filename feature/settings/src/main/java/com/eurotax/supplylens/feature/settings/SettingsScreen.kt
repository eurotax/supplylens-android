package com.eurotax.supplylens.feature.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.util.Locale

data class LanguageOption(
    val code: String,
    val displayName: String,
    val nativeName: String
)

val supportedLanguages = listOf(
    LanguageOption("en", "English", "English"),
    LanguageOption("pl", "Polish", "Polski"),
    LanguageOption("ru", "Russian", "Русский"),
    LanguageOption("zh", "Chinese", "中文"),
    LanguageOption("ko", "Korean", "한국어"),
    LanguageOption("de", "German", "Deutsch"),
    LanguageOption("es", "Spanish", "Español"),
    LanguageOption("fr", "French", "Français"),
    LanguageOption("pt", "Portuguese (Brazil)", "Português (Brasil)"),
    LanguageOption("tr", "Turkish", "Türkçe"),
    LanguageOption("hi", "Hindi", "हिन्दी"),
    LanguageOption("vi", "Vietnamese", "Tiếng Việt")
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen() {
    var showLanguageDialog by remember { mutableStateOf(false) }
    
    val currentLocale = remember {
        Locale.getDefault().language
    }
    
    val currentLanguage = supportedLanguages.find { it.code == currentLocale } 
        ?: supportedLanguages.first()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(
            text = "Settings",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { showLanguageDialog = true }
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Language",
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            text = "Language",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = currentLanguage.nativeName,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "Language changes require app restart",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }

    if (showLanguageDialog) {
        AlertDialog(
            onDismissRequest = { showLanguageDialog = false },
            title = { Text("Select Language") },
            text = {
                Column {
                    supportedLanguages.forEach { language ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    showLanguageDialog = false
                                }
                                .padding(vertical = 12.dp, horizontal = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = currentLocale == language.code,
                                onClick = null
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = language.nativeName,
                                    style = MaterialTheme.typography.bodyLarge
                                )
                                Text(
                                    text = language.displayName,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showLanguageDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
