package com.ambar.finanzas.ui.screens.settings

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.TableView
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ambar.finanzas.AmbarApp
import com.ambar.finanzas.utils.BackupService
import kotlinx.coroutines.launch
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(viewModel: SettingsViewModel) {
    val hideAmounts by viewModel.hideAmounts.collectAsStateWithLifecycle()
    val theme by viewModel.theme.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val backup = remember { BackupService(context, (context.applicationContext as AmbarApp).database) }
    val scope = rememberCoroutineScope()
    val snackbar = remember { SnackbarHostState() }
    var busy by remember { mutableStateOf(false) }
    var confirmImport by remember { mutableStateOf(false) }

    fun runFileAction(label: String, block: suspend () -> Result<Unit>) {
        scope.launch {
            busy = true
            val result = block()
            busy = false
            snackbar.showSnackbar(if (result.isSuccess) label else result.exceptionOrNull()?.message ?: "No se pudo completar")
        }
    }

    val exportBackup = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("application/json")) { uri ->
        if (uri != null) runFileAction("Respaldo guardado") { backup.exportBackup(uri) }
    }
    val exportCsv = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("text/csv")) { uri ->
        if (uri != null) runFileAction("Archivo CSV guardado") { backup.exportCSV(uri) }
    }
    val importBackup = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri != null) runFileAction("Respaldo restaurado") { backup.importBackup(uri) }
    }

    Scaffold(topBar = { TopAppBar(title = { Text("Ajustes") }) }, snackbarHost = { SnackbarHost(snackbar) }) { padding ->
        LazyColumn(Modifier.fillMaxSize().padding(padding), contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)) {
            item { SectionTitle("Apariencia") }
            item {
                Card {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text("Tema", fontWeight = FontWeight.SemiBold)
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf("system" to "Sistema", "light" to "Claro", "dark" to "Oscuro").forEach { option ->
                                FilterChip(selected = theme == option.first, onClick = { viewModel.setTheme(option.first) },
                                    label = { Text(option.second) })
                            }
                        }
                        HorizontalDivider()
                        ListItem(headlineContent = { Text("Ocultar montos") },
                            supportingContent = { Text("Muestra $ ••••• cuando necesitas privacidad") },
                            leadingContent = { Icon(Icons.Default.Lock, null) },
                            trailingContent = { Switch(hideAmounts, onCheckedChange = { viewModel.setHideAmounts(it) }) })
                    }
                }
            }
            item { SectionTitle("Tus datos") }
            item {
                Card {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text("Todo permanece en este teléfono. Crea un respaldo antes de cambiarlo o reinstalar la aplicación.",
                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Button(enabled = !busy, onClick = {
                            exportBackup.launch("ambar-respaldo-" + LocalDate.now() + ".json")
                        }, modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp)) {
                            Icon(Icons.Default.FileDownload, null); Spacer(Modifier.width(8.dp)); Text("Guardar respaldo")
                        }
                        OutlinedButton(enabled = !busy, onClick = { confirmImport = true },
                            modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp)) {
                            Icon(Icons.Default.FileUpload, null); Spacer(Modifier.width(8.dp)); Text("Restaurar respaldo")
                        }
                        TextButton(enabled = !busy, onClick = {
                            exportCsv.launch("ambar-movimientos-" + LocalDate.now() + ".csv")
                        }, modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp)) {
                            Icon(Icons.Default.TableView, null); Spacer(Modifier.width(8.dp)); Text("Exportar movimientos a CSV")
                        }
                        if (busy) LinearProgressIndicator(Modifier.fillMaxWidth())
                    }
                }
            }
            item {
                Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) {
                    Column(Modifier.fillMaxWidth().padding(22.dp)) {
                        Text("Te amo hermanita", style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer, fontWeight = FontWeight.Bold)
                        Text("Hecha con cariño, para ti.", color = MaterialTheme.colorScheme.onPrimaryContainer)
                    }
                }
            }
            item { Text("Ámbar Finanzas · privada y 100% offline", style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant) }
        }
    }

    if (confirmImport) AlertDialog(onDismissRequest = { confirmImport = false },
        title = { Text("¿Restaurar un respaldo?") },
        text = { Text("Los datos actuales se reemplazarán por los del archivo. Esta acción no se puede deshacer.") },
        confirmButton = { Button(onClick = {
            confirmImport = false
            importBackup.launch(arrayOf("application/json", "text/plain"))
        }) { Text("Elegir archivo") } },
        dismissButton = { TextButton(onClick = { confirmImport = false }) { Text("Cancelar") } })
}

@Composable
private fun SectionTitle(text: String) {
    Text(text, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary,
        fontWeight = FontWeight.SemiBold)
}
