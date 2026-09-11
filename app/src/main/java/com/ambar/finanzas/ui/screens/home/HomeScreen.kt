package com.ambar.finanzas.ui.screens.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ambar.finanzas.ui.components.*
import com.ambar.finanzas.utils.CurrencyUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(viewModel: HomeViewModel, onAdd: (Boolean) -> Unit, onTransactions: () -> Unit, onPrivacy: () -> Unit) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var explainBalance by remember { mutableStateOf(false) }
    Scaffold(topBar = {
        TopAppBar(title = { Column {
            Text("Hola, Ámbar", style = MaterialTheme.typography.headlineSmall)
            Text("Tu dinero, a tu ritmo", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        } }, actions = {
            IconButton(onClick = onPrivacy) {
                Icon(if (LocalHideAmounts.current) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                    if (LocalHideAmounts.current) "Mostrar montos" else "Ocultar montos")
            }
        })
    }) { padding ->
        LazyColumn(Modifier.fillMaxSize().padding(padding), contentPadding = PaddingValues(start = 20.dp, end = 20.dp, bottom = 100.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)) {
            item { MonthSelector(state.monthKey, viewModel::previousMonth, viewModel::nextMonth, viewModel::currentMonth) }
            item {
                Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) {
                    Column(Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text("Balance del mes", style = MaterialTheme.typography.titleMedium)
                        Text(money(state.available), style = MaterialTheme.typography.displayLarge, fontWeight = FontWeight.Bold)
                        Text(if (state.available < 0) "Tus gastos y pendientes superan lo que ingresaste."
                            else "Ingresos menos gastos pagados y pendientes registrados.", style = MaterialTheme.typography.bodyMedium)
                        if (state.monthKey == CurrencyUtils.currentMonthKey() && state.totalIncome > 0 && state.available >= 0) {
                            HorizontalDivider(color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = .15f))
                            Text("Referencia diaria: " + money(state.canSpendToday), style = MaterialTheme.typography.titleMedium)
                            Text("Reparte este balance hasta fin de mes. No incluye pagos que aún no registraste.", style = MaterialTheme.typography.bodySmall)
                        }
                        TextButton(onClick = { explainBalance = true }, contentPadding = PaddingValues(0.dp)) { Text("¿Cómo se calcula?") }
                    }
                }
            }
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Button(onClick = { onAdd(true) }, modifier = Modifier.weight(1f).heightIn(min = 52.dp)) { Text("Anotar gasto") }
                    OutlinedButton(onClick = { onAdd(false) }, modifier = Modifier.weight(1f).heightIn(min = 52.dp)) { Text("Añadir ingreso") }
                }
            }
            item {
                Card {
                    Column(Modifier.padding(20.dp)) {
                        AmountLine("Ingresos recibidos", state.totalIncome)
                        AmountLine("Gastos pagados", state.totalExpenses)
                        AmountLine("Pagos pendientes", state.totalPending)
                    }
                }
            }
            if (state.totalIncome == 0L && state.totalExpenses == 0L && state.pendingPayments.isEmpty()) {
                item { EmptyMessage("Empecemos por lo simple", "Anota un ingreso o un gasto. Tu resumen se actualizará con tus propios datos.",
                    "Registrar un ingreso", { onAdd(false) }) }
            }
            if (state.pendingPayments.isNotEmpty()) {
                item { Text("Pendientes de este mes", style = MaterialTheme.typography.titleLarge) }
                items(state.pendingPayments.take(3), key = { it.id }) { payment ->
                    OutlinedCard {
                        Column(Modifier.padding(16.dp)) {
                            Text(payment.description, style = MaterialTheme.typography.titleMedium)
                            Text(money(payment.amount), style = MaterialTheme.typography.headlineSmall)
                            Text("Revisa y registra el pago en Movimientos.", style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }
            item { TextButton(onClick = onTransactions, modifier = Modifier.fillMaxWidth()) { Text("Ver mis movimientos") } }
        }
    }
    if (explainBalance) AlertDialog(onDismissRequest = { explainBalance = false },
        title = { Text("Un resumen, no tu saldo bancario") },
        text = { Text("El balance considera solo los movimientos del mes seleccionado. Resta gastos pagados y pendientes a ingresos recibidos. No arrastra saldos de otros meses ni descuenta automáticamente los gastos habituales o las cuotas sin registrar.") },
        confirmButton = { TextButton(onClick = { explainBalance = false }) { Text("Entendido") } })
}
