package com.ambar.finanzas.ui.screens.installments

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ambar.finanzas.data.local.entity.InstallmentPlanEntity
import com.ambar.finanzas.ui.components.AmountLine
import com.ambar.finanzas.ui.components.ConfirmRemoval
import com.ambar.finanzas.ui.components.EmptyMessage
import com.ambar.finanzas.ui.components.money
import com.ambar.finanzas.utils.CurrencyVisualTransformation
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InstallmentsScreen(viewModel: InstallmentsViewModel) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()
    val snackbar = remember { SnackbarHostState() }
    var showAdd by remember { mutableStateOf(false) }
    var toPay by remember { mutableStateOf<InstallmentPlanEntity?>(null) }
    var toDelete by remember { mutableStateOf<InstallmentPlanEntity?>(null) }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Cuotas") }) },
        snackbarHost = { SnackbarHost(snackbar) },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showAdd = true },
                icon = { Icon(Icons.Default.Add, null) },
                text = { Text("Nueva compra") }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 96.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text("Controla lo que ya pagaste y lo que todavía falta.",
                    style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            if (state.activePlans.isEmpty()) item {
                EmptyMessage("Sin compras en cuotas",
                    "Agrega una compra para ver su avance y registrar cada pago sin hacer cálculos.",
                    "Agregar compra") { showAdd = true }
            } else items(state.activePlans, key = { it.id }) { plan ->
                InstallmentCard(plan, onPay = { toPay = plan }, onDelete = { toDelete = plan })
            }
        }
    }

    if (showAdd) AddInstallmentDialog(onDismiss = { showAdd = false }) { name, amount, paid, total ->
        scope.launch {
            runCatching { viewModel.addPlan(name, amount, paid, total) }
                .onSuccess { showAdd = false; snackbar.showSnackbar("Compra en cuotas guardada") }
                .onFailure { snackbar.showSnackbar(it.message ?: "No se pudo guardar") }
        }
    }

    toPay?.let { plan ->
        AlertDialog(onDismissRequest = { toPay = null }, icon = { Icon(Icons.Default.Payments, null) },
            title = { Text("Registrar próxima cuota") },
            text = { Text("Se añadirá un gasto de " + money(plan.installmentAmount) + " para “" + plan.name + "”.") },
            confirmButton = { Button(onClick = {
                toPay = null
                scope.launch {
                    runCatching { viewModel.pay(plan) }
                        .onSuccess { snackbar.showSnackbar("Cuota registrada como gasto") }
                        .onFailure { snackbar.showSnackbar(it.message ?: "No se pudo registrar") }
                }
            }) { Text("Registrar pago") } },
            dismissButton = { TextButton(onClick = { toPay = null }) { Text("Cancelar") } })
    }

    toDelete?.let { plan ->
        ConfirmRemoval("¿Eliminar esta compra?",
            "Se eliminará el plan “" + plan.name + "”. Los pagos ya registrados se conservarán.",
            onDismiss = { toDelete = null }, onConfirm = {
                toDelete = null
                scope.launch {
                    runCatching { viewModel.deletePlan(plan) }
                        .onSuccess { snackbar.showSnackbar("Plan eliminado") }
                        .onFailure { snackbar.showSnackbar("No se pudo eliminar") }
                }
            })
    }
}

@Composable
private fun InstallmentCard(plan: InstallmentPlanEntity, onPay: () -> Unit, onDelete: () -> Unit) {
    val paid = plan.currentInstallment.coerceIn(0, plan.totalInstallments)
    val remaining = plan.totalInstallments - paid
    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text(plan.name, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
                    Text(paid.toString() + " de " + plan.totalInstallments + " cuotas pagadas",
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                IconButton(onClick = onDelete) { Icon(Icons.Default.DeleteOutline, "Eliminar plan") }
            }
            LinearProgressIndicator(progress = { paid.toFloat() / plan.totalInstallments.coerceAtLeast(1).toFloat() },
                modifier = Modifier.fillMaxWidth().height(8.dp))
            AmountLine("Valor por cuota", plan.installmentAmount)
            AmountLine("Saldo por pagar", remaining.toLong() * plan.installmentAmount)
            Button(onClick = onPay, modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp)) {
                Text("Registrar cuota " + (paid + 1) + " de " + plan.totalInstallments)
            }
        }
    }
}

@Composable
private fun AddInstallmentDialog(onDismiss: () -> Unit, onAdd: (String, Long, Int, Int) -> Unit) {
    var name by rememberSaveable { mutableStateOf("") }
    var amount by rememberSaveable { mutableStateOf("") }
    var paid by rememberSaveable { mutableStateOf("0") }
    var total by rememberSaveable { mutableStateOf("") }
    val amountValue = amount.toLongOrNull()
    val paidValue = paid.toIntOrNull()
    val totalValue = total.toIntOrNull()
    val valid = name.isNotBlank() && amountValue != null && amountValue > 0 &&
        paidValue != null && totalValue != null && totalValue in 1..1200 && paidValue in 0 until totalValue

    AlertDialog(onDismissRequest = onDismiss, title = { Text("Nueva compra en cuotas") }, text = {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            OutlinedTextField(name, { name = it.take(120) }, label = { Text("Nombre de la compra") },
                placeholder = { Text("Ej.: refrigerador") }, singleLine = true, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(amount, { if (it.length <= 11 && it.all(Char::isDigit)) amount = it },
                label = { Text("Monto de cada cuota") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                visualTransformation = CurrencyVisualTransformation(), singleLine = true, modifier = Modifier.fillMaxWidth())
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(paid, { if (it.length <= 4 && it.all(Char::isDigit)) paid = it },
                    label = { Text("Ya pagadas") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true, modifier = Modifier.weight(1f))
                OutlinedTextField(total, { if (it.length <= 4 && it.all(Char::isDigit)) total = it },
                    label = { Text("Total") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true, modifier = Modifier.weight(1f))
            }
            Text("Las cuotas ya pagadas permiten comenzar desde tu avance real.",
                style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }, confirmButton = {
        Button(onClick = { onAdd(name, amountValue!!, paidValue!!, totalValue!!) }, enabled = valid) { Text("Guardar") }
    }, dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } })
}
