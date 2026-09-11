package com.ambar.finanzas.ui.screens.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ambar.finanzas.ui.theme.ExpenseRed
import com.ambar.finanzas.ui.theme.IncomeGreen
import com.ambar.finanzas.utils.CurrencyUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(viewModel: HomeViewModel) {
    val state by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Hola, \u00c1mbar \ud83d\udc4b") },
                actions = {
                    BadgedBox(badge = {
                        if (state.unreadAlerts > 0) Badge { Text("${state.unreadAlerts}") }
                    }) {
                        IconButton(onClick = { /* TODO: alerts */ }) {
                            Icon(Icons.Default.Notifications, "Alertas")
                        }
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Month selector
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { viewModel.previousMonth() }) {
                    Icon(Icons.Default.ChevronLeft, "Mes anterior")
                }
                Text(
                    text = state.monthDisplay,
                    style = MaterialTheme.typography.titleMedium
                )
                IconButton(onClick = { viewModel.nextMonth() }) {
                    Icon(Icons.Default.ChevronRight, "Mes siguiente")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Main card - ME QUEDA
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "ME QUEDA",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = CurrencyUtils.formatCLP(state.available),
                        style = MaterialTheme.typography.displayLarge.copy(fontSize = 40.sp),
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = if (state.totalPending > 0) "considerando tus pr\u00f3ximos pagos"
                               else "disponible este mes",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.6f)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Puedes gastar hoy: ${CurrencyUtils.formatCLP(state.canSpendToday)}",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Summary row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                SummaryChip("Ingres\u00e9", CurrencyUtils.formatCLP(state.totalIncome), IncomeGreen)
                SummaryChip("Gast\u00e9", CurrencyUtils.formatCLP(state.totalExpenses), ExpenseRed)
                SummaryChip("Pendiente", CurrencyUtils.formatCLP(state.totalPending), MaterialTheme.colorScheme.tertiary)
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Upcoming payments
            if (state.pendingPayments.isNotEmpty()) {
                Text(
                    text = "PR\u00d3XIMOS PAGOS",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                state.pendingPayments.forEach { payment ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(payment.description, style = MaterialTheme.typography.bodyLarge)
                            Text(
                                CurrencyUtils.formatCLP(payment.amount),
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Bold,
                                color = ExpenseRed
                            )
                        }
                    }
                }
            }

            // Empty state
            if (state.totalIncome == 0L && state.totalExpenses == 0L) {
                Spacer(modifier = Modifier.height(48.dp))
                Text(
                    text = "No tienes movimientos todav\u00eda.\n\nToca + para anotar tu primer gasto.",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun SummaryChip(label: String, value: String, color: androidx.compose.ui.graphics.Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(modifier = Modifier.height(4.dp))
        Text(value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = color)
    }
}
