package com.ambar.finanzas.ui.screens.settings

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(viewModel: SettingsViewModel) {
    Scaffold(
        topBar = { TopAppBar(title = { Text("Ajustes") }) }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp)) {
            Text("Privacidad", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(8.dp))
            ListItem(
                headlineContent = { Text("Proteger con biometría") },
                trailingContent = { Switch(checked = false, onCheckedChange = {}) }
            )
            ListItem(
                headlineContent = { Text("Ocultar cantidades") },
                supportingContent = { Text("Modo privado ($•••••)") },
                trailingContent = { Switch(checked = false, onCheckedChange = {}) }
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            Text("Datos y Respaldos", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = { /* TODO */ }, modifier = Modifier.fillMaxWidth()) {
                Text("Exportar Respaldo (JSON)")
            }
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedButton(onClick = { /* TODO */ }, modifier = Modifier.fillMaxWidth()) {
                Text("Importar Respaldo")
            }
            Spacer(modifier = Modifier.height(8.dp))
            TextButton(onClick = { /* TODO */ }, modifier = Modifier.fillMaxWidth()) {
                Text("Exportar movimientos a CSV")
            }
        }
    }
}
