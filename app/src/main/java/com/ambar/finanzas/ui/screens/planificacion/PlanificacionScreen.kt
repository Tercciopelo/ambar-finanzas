package com.ambar.finanzas.ui.screens.planificacion

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ambar.finanzas.data.local.entity.RecurringRuleEntity
import com.ambar.finanzas.ui.components.AmountLine
import com.ambar.finanzas.ui.components.ConfirmRemoval
import com.ambar.finanzas.ui.components.EmptyMessage
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlanificacionScreen(viewModel: PlanificacionViewModel, onAdd: () -> Unit, onRegister: (Long) -> Unit) {
    val rules by viewModel.subscriptions.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()
    val snackbar = remember { SnackbarHostState() }
    var toDelete by remember { mutableStateOf<RecurringRuleEntity?>(null) }
    val total = rules.sumOf { it.amount }

    Scaffold(topBar = { TopAppBar(title = { Text("Plan") }) }, snackbarHost = { SnackbarHost(snackbar) },
        floatingActionButton = {
            ExtendedFloatingActionButton(onClick = onAdd, icon = { Icon(Icons.Default.Add, null) },
                text = { Text("Nuevo habitual") })
        }) { padding ->
        LazyColumn(Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 96.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)) {
            item {
                Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) {
                    Column(Modifier.padding(18.dp)) {
                        Text("Gastos habituales", style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer)
                        Spacer(Modifier.height(6.dp))
                        AmountLine("Total de tus plantillas", total)
                        Text("Es una referencia mensual: nada se descuenta hasta que tú lo registres.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer)
                    }
                }
            }
            if (rules.isEmpty()) item {
                EmptyMessage("Tu mes todavía no tiene plantillas",
                    "Guarda arriendo, servicios o suscripciones para anotarlos más rápido cuando corresponda.",
                    "Crear gasto habitual", onAdd)
            } else items(rules, key = { it.id }) { rule ->
                RecurringRuleCard(rule, onRegister = { onRegister(rule.id) }, onDelete = { toDelete = rule })
            }
        }
    }

    toDelete?.let { rule ->
        ConfirmRemoval("¿Eliminar este gasto habitual?",
            "Se quitará “" + rule.description + "” de Plan. Los movimientos ya registrados se conservarán.",
            onDismiss = { toDelete = null }, onConfirm = {
                toDelete = null
                scope.launch {
                    runCatching { viewModel.deleteRule(rule) }
                        .onSuccess { snackbar.showSnackbar("Gasto habitual eliminado") }
                        .onFailure { snackbar.showSnackbar("No se pudo eliminar") }
                }
            })
    }
}

@Composable
private fun RecurringRuleCard(rule: RecurringRuleEntity, onRegister: () -> Unit, onDelete: () -> Unit) {
    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Replay, null, tint = MaterialTheme.colorScheme.primary)
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text(rule.description, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    Text(if (rule.isSubscription) "Suscripción" else "Gasto habitual",
                        style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
                }
                IconButton(onClick = onDelete) { Icon(Icons.Default.DeleteOutline, "Eliminar") }
            }
            AmountLine("Monto habitual", rule.amount)
            OutlinedButton(onClick = onRegister, modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp)) {
                Text("Registrar este mes")
            }
        }
    }
}
