package com.ambar.finanzas.ui.screens.installments

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.ambar.finanzas.data.local.entity.InstallmentPlanEntity
import com.ambar.finanzas.utils.CurrencyUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InstallmentsScreen(viewModel: InstallmentsViewModel) {
    val state by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = { TopAppBar(title = { Text("Cuotas") }) }
    ) { padding ->
        if (state.activePlans.isEmpty() && state.completedPlans.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "No tienes cuotas activas \ud83c\udf89",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(vertical = 16.dp)
            ) {
                if (state.activePlans.isNotEmpty()) {
                    item {
                        Text(
                            "ACTIVAS",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                    }
                    items(state.activePlans, key = { it.id }) { plan ->
                        InstallmentCard(plan)
                    }
                }
                if (state.completedPlans.isNotEmpty()) {
                    item {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "FINALIZADAS",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                    }
                    items(state.completedPlans, key = { it.id }) { plan ->
                        InstallmentCard(plan)
                    }
                }
            }
        }
    }
}

@Composable
private fun InstallmentCard(plan: InstallmentPlanEntity) {
    val progress = plan.currentInstallment.toFloat() / plan.totalInstallments
    val remaining = plan.totalInstallments - plan.currentInstallment
    val remainingAmount = plan.installmentAmount * remaining

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(plan.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(
                    CurrencyUtils.formatCLP(plan.installmentAmount) + " / mes",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                "${plan.currentInstallment} / ${plan.totalInstallments}",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(8.dp))

            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp),
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    when {
                        remaining == 0 -> "\u00a1Completada! \ud83c\udf89"
                        remaining == 1 -> "\u00daltima cuota \ud83c\udf89"
                        remaining == 2 -> "Ya casi terminas"
                        else -> "Faltan $remaining cuotas"
                    },
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    "Restante: ${CurrencyUtils.formatCLP(remainingAmount)}",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}
