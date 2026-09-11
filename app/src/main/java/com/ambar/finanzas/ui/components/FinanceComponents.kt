package com.ambar.finanzas.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ambar.finanzas.utils.CurrencyUtils

val LocalHideAmounts = staticCompositionLocalOf { false }

@Composable
fun money(amount: Long): String = if (LocalHideAmounts.current) "$ •••••" else CurrencyUtils.formatCLP(amount)

@Composable
fun MonthSelector(monthKey: String, previous: () -> Unit, next: () -> Unit, today: () -> Unit) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween) {
        IconButton(onClick = previous) { Icon(Icons.Default.ChevronLeft, "Mes anterior") }
        TextButton(onClick = today) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(CurrencyUtils.monthKeyToDisplay(monthKey), style = MaterialTheme.typography.titleMedium)
                if (monthKey != CurrencyUtils.currentMonthKey()) Text("Volver a este mes", style = MaterialTheme.typography.labelSmall)
            }
        }
        IconButton(onClick = next) { Icon(Icons.Default.ChevronRight, "Mes siguiente") }
    }
}

@Composable
fun EmptyMessage(title: String, description: String, action: String? = null, onAction: () -> Unit = {}) {
    Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
        Column(Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(title, style = MaterialTheme.typography.titleLarge)
            Text(description, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
            if (action != null) Button(onClick = onAction) { Text(action) }
        }
    }
}

@Composable
fun AmountLine(label: String, amount: Long, modifier: Modifier = Modifier) {
    Row(modifier.fillMaxWidth().padding(vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
        Text(label, Modifier.weight(1f), color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.width(12.dp))
        Text(money(amount), fontWeight = FontWeight.SemiBold)
    }
}

@Composable
fun ConfirmRemoval(title: String, description: String, onDismiss: () -> Unit, onConfirm: () -> Unit) {
    AlertDialog(onDismissRequest = onDismiss, title = { Text(title) }, text = { Text(description) },
        confirmButton = { TextButton(onClick = onConfirm) { Text("Eliminar", color = MaterialTheme.colorScheme.error) } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Conservar") } })
}
