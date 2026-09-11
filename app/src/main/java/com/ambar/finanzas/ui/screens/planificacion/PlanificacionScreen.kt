package com.ambar.finanzas.ui.screens.planificacion

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ambar.finanzas.data.local.entity.RecurringRuleEntity
import com.ambar.finanzas.utils.CurrencyUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlanificacionScreen(viewModel: PlanificacionViewModel) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Recurrentes", "Metas", "Apartadas", "Calendario")
    val subs by viewModel.subscriptions.collectAsState()

    Scaffold(
        topBar = { TopAppBar(title = { Text("Planificacion") }) }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            ScrollableTabRow(
                selectedTabIndex = selectedTab,
                edgePadding = 16.dp,
                divider = {}
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { Text(title) }
                    )
                }
            }

            Box(modifier = Modifier.fillMaxSize()) {
                when (selectedTab) {
                    0 -> {
                        if (subs.isEmpty()) {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Text("No tienes gastos recurrentes \ud83c\udf89")
                            }
                        } else {
                            LazyColumn(contentPadding = PaddingValues(16.dp)) {
                                items(subs) { rule ->
                                    RecurringRuleCard(rule, onDelete = { viewModel.deleteRule(rule) })
                                }
                            }
                        }
                    }
                    1 -> Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("Metas de Ahorro") }
                    2 -> Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("Platas Apartadas") }
                    3 -> Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("Calendario de Pagos") }
                }
            }
        }
    }
}

@Composable
fun RecurringRuleCard(rule: RecurringRuleEntity, onDelete: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(rule.description, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                Text(CurrencyUtils.formatCLP(rule.amount), style = MaterialTheme.typography.bodyMedium)
                if (rule.isSubscription) {
                    Text("Suscripcion", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
                }
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = MaterialTheme.colorScheme.error)
            }
        }
    }
}
