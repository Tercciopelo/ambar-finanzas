package com.ambar.finanzas.ui.screens.installments

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.ambar.finanzas.data.local.entity.InstallmentPlanEntity
import com.ambar.finanzas.utils.CurrencyUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InstallmentsScreen(viewModel: InstallmentsViewModel) {
    val state by viewModel.uiState.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Cuotas Activas") }) },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Añadir Cuota")
            }
        }
    ) { padding ->
        if (state.activePlans.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("No tienes cuotas activas ??", style = MaterialTheme.typography.bodyLarge)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(16.dp)
            ) {
                items(state.activePlans) { plan ->
                    InstallmentCard(plan = plan, onDelete = { viewModel.deletePlan(plan) })
                }
            }
        }

        if (showAddDialog) {
            AddInstallmentDialog(
                onDismiss = { showAddDialog = false },
                onAdd = { name, amount, current, total ->
                    viewModel.addPlan(name, amount, current, total)
                    showAddDialog = false
                }
            )
        }
    }
}

@Composable
fun AddInstallmentDialog(onDismiss: () -> Unit, onAdd: (String, Long, Int, Int) -> Unit) {
    var name by remember { mutableStateOf("") }
    var amountText by remember { mutableStateOf("") }
    var currentText by remember { mutableStateOf("1") }
    var totalText by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Añadir Cuota") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Descripción (Ej: Falabella)") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = amountText,
                    onValueChange = { amountText = it.filter { c -> c.isDigit() } },
                    label = { Text("Monto de 1 cuota") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = currentText,
                        onValueChange = { currentText = it.filter { c -> c.isDigit() } },
                        label = { Text("Cuota Actual") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = totalText,
                        onValueChange = { totalText = it.filter { c -> c.isDigit() } },
                        label = { Text("Total Cuotas") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val amount = amountText.toLongOrNull() ?: 0L
                    val current = currentText.toIntOrNull() ?: 1
                    val total = totalText.toIntOrNull() ?: 1
                    if (name.isNotBlank() && amount > 0) {
                        onAdd(name, amount, current, total)
                    }
                }
            ) {
                Text("Guardar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}

@Composable
fun InstallmentCard(plan: InstallmentPlanEntity, onDelete: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(plan.name, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = MaterialTheme.colorScheme.error)
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(" mensual", style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(" / ", fontWeight = FontWeight.Bold)
            LinearProgressIndicator(
                progress = { plan.currentInstallment.toFloat() / plan.totalInstallments.toFloat() },
                modifier = Modifier.fillMaxWidth().height(8.dp).padding(vertical = 8.dp)
            )
            
            val remaining = plan.totalInstallments - plan.currentInstallment
            val remainingAmount = remaining * plan.installmentAmount
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Faltan:  cuotas", style = MaterialTheme.typography.bodySmall)
                Text("Restante: ", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
            }
        }
    }
}
