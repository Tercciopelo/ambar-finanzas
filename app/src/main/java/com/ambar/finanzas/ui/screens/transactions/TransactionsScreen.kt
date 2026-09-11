package com.ambar.finanzas.ui.screens.transactions

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ambar.finanzas.data.local.entity.TransactionEntity
import com.ambar.finanzas.utils.CurrencyUtils
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionsScreen(viewModel: TransactionsViewModel) {
    val state by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Movimientos") })
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            // Filters
            ScrollableTabRow(
                selectedTabIndex = 0,
                modifier = Modifier.fillMaxWidth(),
                edgePadding = 16.dp,
                divider = {}
            ) {
                FilterChip(selected = true, onClick = {}, label = { Text("Todos") })
                Spacer(modifier = Modifier.width(8.dp))
                FilterChip(selected = false, onClick = {}, label = { Text("Gastos") })
                Spacer(modifier = Modifier.width(8.dp))
                FilterChip(selected = false, onClick = {}, label = { Text("Ingresos") })
            }
            
            if (state.transactions.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No tienes movimientos todavía.")
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp)
                ) {
                    // We need to group by date
                    val grouped = state.transactions.groupBy { 
                        Instant.ofEpochMilli(it.date).atZone(ZoneId.systemDefault()).toLocalDate()
                    }
                    
                    grouped.forEach { (date, transactions) ->
                        item {
                            Text(
                                text = date.format(DateTimeFormatter.ofPattern("dd MMM yyyy")).uppercase(),
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }
                        items(transactions) { tx ->
                            TransactionItem(tx)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TransactionItem(tx: TransactionEntity) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(tx.description, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
                Text(if (tx.type == "EXPENSE") "Gasto" else "Ingreso", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Text(
                text = "${if (tx.type == "EXPENSE") "-" else "+"}${CurrencyUtils.formatCLP(tx.amount)}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = if (tx.type == "EXPENSE") Color(0xFFE53935) else Color(0xFF43A047)
            )
        }
    }
}
