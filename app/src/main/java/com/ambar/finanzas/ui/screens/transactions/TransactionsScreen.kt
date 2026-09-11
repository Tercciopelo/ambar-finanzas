package com.ambar.finanzas.ui.screens.transactions

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.ambar.finanzas.ui.theme.ExpenseRed
import com.ambar.finanzas.ui.theme.IncomeGreen
import com.ambar.finanzas.utils.CurrencyUtils
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionsScreen(viewModel: TransactionsViewModel) {
    val state by viewModel.uiState.collectAsState()
    val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale("es", "CL"))

    Scaffold(
        topBar = { TopAppBar(title = { Text("Movimientos") }) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            // Search
            OutlinedTextField(
                value = state.searchQuery,
                onValueChange = { viewModel.setSearch(it) },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Buscar...") },
                leadingIcon = { Icon(Icons.Default.Search, "Buscar") },
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Filter chips
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(
                    selected = state.filterType == null,
                    onClick = { viewModel.setFilter(null) },
                    label = { Text("Todos") }
                )
                FilterChip(
                    selected = state.filterType == "EXPENSE",
                    onClick = { viewModel.setFilter(if (state.filterType == "EXPENSE") null else "EXPENSE") },
                    label = { Text("Gastos") }
                )
                FilterChip(
                    selected = state.filterType == "INCOME",
                    onClick = { viewModel.setFilter(if (state.filterType == "INCOME") null else "INCOME") },
                    label = { Text("Ingresos") }
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (state.transactions.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "No tienes movimientos todav\u00eda.\n\nToca + para anotar tu primer gasto.",
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(state.transactions, key = { it.id }) { tx ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        tx.description,
                                        style = MaterialTheme.typography.bodyLarge,
                                        fontWeight = FontWeight.Medium
                                    )
                                    Text(
                                        dateFormat.format(Date(tx.date)),
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Text(
                                    text = if (tx.type == "INCOME") "+${CurrencyUtils.formatCLP(tx.amount)}"
                                           else "-${CurrencyUtils.formatCLP(tx.amount)}",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = if (tx.type == "INCOME") IncomeGreen else ExpenseRed
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
