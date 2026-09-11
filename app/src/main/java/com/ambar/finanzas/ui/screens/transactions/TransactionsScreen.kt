package com.ambar.finanzas.ui.screens.transactions

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ambar.finanzas.data.local.entity.TransactionEntity
import com.ambar.finanzas.ui.components.*
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionsScreen(viewModel: TransactionsViewModel, onAdd: () -> Unit) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var selected by remember { mutableStateOf<TransactionEntity?>(null) }
    var delete by remember { mutableStateOf(false) }
    var busy by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val snackbar = remember { SnackbarHostState() }
    Scaffold(topBar = { TopAppBar(title = { Text("Movimientos") }) }, snackbarHost = { SnackbarHost(snackbar) }) { padding ->
        LazyColumn(Modifier.fillMaxSize().padding(padding), contentPadding = PaddingValues(start = 20.dp, end = 20.dp, bottom = 100.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)) {
            item { MonthSelector(state.monthKey, viewModel::previousMonth, viewModel::nextMonth, viewModel::currentMonth) }
            item {
                OutlinedTextField(value = state.searchQuery, onValueChange = viewModel::setSearchQuery,
                    modifier = Modifier.fillMaxWidth(), label = { Text("Buscar en este mes") }, singleLine = true,
                    leadingIcon = { Icon(Icons.Default.Search, null) },
                    trailingIcon = { if (state.searchQuery.isNotEmpty()) IconButton(onClick = { viewModel.setSearchQuery("") }) { Icon(Icons.Default.Close, "Limpiar búsqueda") } })
            }
            item {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(listOf("ALL" to "Todos", "EXPENSE" to "Gastos", "INCOME" to "Ingresos", "PENDING" to "Pendientes")) { (key, label) ->
                        FilterChip(selected = state.filterType == key, onClick = { viewModel.setFilterType(key) }, label = { Text(label) })
                    }
                }
            }
            if (state.transactions.isEmpty()) item {
                EmptyMessage("Sin movimientos para mostrar", "Prueba otro mes o cambia los filtros. También puedes registrar un movimiento.",
                    "Anotar movimiento", onAdd)
            }
            val groups = state.transactions.groupBy { Instant.ofEpochMilli(it.date).atZone(ZoneId.systemDefault()).toLocalDate() }
            groups.forEach { (date, transactions) ->
                item(key = date.toString()) { Text(date.format(DateTimeFormatter.ofPattern("EEEE d 'de' MMMM", Locale("es", "CL"))),
                    style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary) }
                items(transactions, key = { it.id }) { tx -> TransactionItem(tx) { selected = tx } }
            }
        }
    }
    selected?.let { tx ->
        if (delete) ConfirmRemoval("¿Eliminar este movimiento?", "Se eliminará del historial y el resumen se recalculará. Esta acción no se puede deshacer.",
            { if (!busy) delete = false }, {
                if (!busy) { busy = true; scope.launch {
                    try { viewModel.delete(tx); selected = null; delete = false; snackbar.showSnackbar("Movimiento eliminado") }
                    catch (e: Exception) { snackbar.showSnackbar("No se pudo eliminar. Inténtalo nuevamente.") }
                    finally { busy = false }
                } }
            })
        else AlertDialog(onDismissRequest = { if (!busy) selected = null }, title = { Text(tx.description) },
            text = { Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(money(tx.amount), style = MaterialTheme.typography.headlineMedium)
                Text(if (tx.type == "INCOME") "Ingreso recibido" else if (tx.status == "PAID") "Gasto pagado" else "Pago pendiente")
                if (tx.note.isNotBlank()) Text(tx.note)
                if (tx.installmentPlanId != null) Text("Pago de una cuota. Se conserva para mantener la coherencia del plan.")
                else TextButton(onClick = { delete = true }, enabled = !busy) { Text("Eliminar movimiento", color = MaterialTheme.colorScheme.error) }
            } },
            confirmButton = {
                if (tx.type == "EXPENSE" && tx.status != "PAID") TextButton(enabled = !busy, onClick = {
                    busy = true; scope.launch {
                        try { viewModel.markPaid(tx); selected = null; snackbar.showSnackbar("Pago registrado con fecha de hoy") }
                        catch (e: Exception) { snackbar.showSnackbar("No se pudo registrar el pago.") }
                        finally { busy = false }
                    }
                }) { Text("Lo pagué hoy") }
                else TextButton(onClick = { selected = null }) { Text("Listo") }
            }, dismissButton = { if (tx.status != "PAID") TextButton(enabled = !busy, onClick = { selected = null }) { Text("Cerrar") } })
    }
}

@Composable
fun TransactionItem(tx: TransactionEntity, onClick: () -> Unit) {
    Card(onClick = onClick, modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(tx.description, style = MaterialTheme.typography.titleMedium)
            Text((if (tx.type == "EXPENSE") "− " else "+ ") + money(tx.amount),
                style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold,
                color = if (tx.type == "EXPENSE") MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.primary)
            Text(if (tx.type == "INCOME") "Ingreso" else if (tx.status == "PAID") "Gasto pagado" else "Pendiente · toca para registrar el pago",
                style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
