package com.ambar.finanzas.ui.screens.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(viewModel: SettingsViewModel) {
    Scaffold(
        topBar = { TopAppBar(title = { Text("Ajustes") }) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            SettingsItem(Icons.Default.Category, "Categor\u00edas") { /* TODO */ }
            SettingsItem(Icons.Default.AccountBalanceWallet, "Presupuesto") { /* TODO */ }
            SettingsItem(Icons.Default.Repeat, "Gastos recurrentes") { /* TODO */ }
            SettingsItem(Icons.Default.Subscriptions, "Suscripciones") { /* TODO */ }
            SettingsItem(Icons.Default.Savings, "Metas de ahorro") { /* TODO */ }
            SettingsItem(Icons.Default.CalendarMonth, "Calendario") { /* TODO */ }

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            SettingsItem(Icons.Default.Notifications, "Alertas") { /* TODO */ }
            SettingsItem(Icons.Default.Fingerprint, "Seguridad") { /* TODO */ }
            SettingsItem(Icons.Default.Palette, "Tema") { /* TODO */ }

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            SettingsItem(Icons.Default.CloudUpload, "Exportar respaldo") { /* TODO */ }
            SettingsItem(Icons.Default.CloudDownload, "Importar respaldo") { /* TODO */ }
            SettingsItem(Icons.Default.TableChart, "Exportar CSV") { /* TODO */ }

            Spacer(modifier = Modifier.height(16.dp))
            Text(
                "\u00c1mbar Finanzas v1.0.0",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    }
}

@Composable
private fun SettingsItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(icon, contentDescription = title, tint = MaterialTheme.colorScheme.primary)
            Text(title, style = MaterialTheme.typography.bodyLarge)
        }
    }
}
