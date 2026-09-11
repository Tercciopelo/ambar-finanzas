package com.ambar.finanzas.ui.screens.installments

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ambar.finanzas.data.local.entity.InstallmentPlanEntity
import com.ambar.finanzas.utils.CurrencyUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InstallmentsScreen(viewModel: InstallmentsViewModel) {
    val state by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = { TopAppBar(title = { Text("Cuotas Activas") }) }
    ) { padding ->
        if (state.activePlans.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("No tienes cuotas activas \ud83c\udf89", style = MaterialTheme.typography.bodyLarge)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(16.dp)
            ) {
                items(state.activePlans) { plan ->
                    InstallmentCard(plan)
                }
            }
        }
    }
}

@Composable
fun InstallmentCard(plan: InstallmentPlanEntity) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(plan.name, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Text("${CurrencyUtils.formatCLP(plan.installmentAmount)} mensual", style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(12.dp))
            
            Text("${plan.currentInstallment} / ${plan.totalInstallments}", fontWeight = FontWeight.Bold)
            LinearProgressIndicator(
                progress = { plan.currentInstallment.toFloat() / plan.totalInstallments.toFloat() },
                modifier = Modifier.fillMaxWidth().height(8.dp).padding(vertical = 8.dp)
            )
            
            val remaining = plan.totalInstallments - plan.currentInstallment
            val remainingAmount = remaining * plan.installmentAmount
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Faltan: $remaining cuotas", style = MaterialTheme.typography.bodySmall)
                Text("Restante: ${CurrencyUtils.formatCLP(remainingAmount)}", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
            }
        }
    }
}
