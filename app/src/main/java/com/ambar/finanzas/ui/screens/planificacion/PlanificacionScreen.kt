package com.ambar.finanzas.ui.screens.planificacion

import android.app.DatePickerDialog
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ambar.finanzas.data.local.entity.RecurringRuleEntity
import com.ambar.finanzas.data.local.entity.SavingsGoalEntity
import com.ambar.finanzas.ui.components.AmountLine
import com.ambar.finanzas.ui.components.ConfirmRemoval
import com.ambar.finanzas.ui.components.EmptyMessage
import com.ambar.finanzas.ui.components.money
import com.ambar.finanzas.utils.CurrencyVisualTransformation
import com.ambar.finanzas.utils.PlanningCalculator
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlanificacionScreen(viewModel: PlanificacionViewModel, onAdd: () -> Unit, onRegister: (Long) -> Unit) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()
    val snackbar = remember { SnackbarHostState() }
    var showAddGoal by remember { mutableStateOf(false) }
    var contributeTo by remember { mutableStateOf<SavingsGoalEntity?>(null) }
    var goalToDelete by remember { mutableStateOf<SavingsGoalEntity?>(null) }
    var ruleToDelete by remember { mutableStateOf<RecurringRuleEntity?>(null) }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Planificación") }) },
        snackbarHost = { SnackbarHost(snackbar) },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showAddGoal = true },
                icon = { Icon(Icons.Default.Add, null) },
                text = { Text("Nueva meta") }
            )
        }
    ) { padding ->
        LazyColumn(
            Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 104.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item { PlanningSummary(state) }
            item {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text("Metas de ahorro", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
                        Text("Avanza con aportes que puedas sostener.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Icon(Icons.Default.Savings, null, tint = MaterialTheme.colorScheme.primary)
                }
            }
            if (state.goals.isEmpty()) {
                item {
                    EmptyMessage(
                        "Todavía no tienes metas",
                        "Crea un objetivo con monto y fecha. Ámbar calculará cuánto conviene apartar cada mes.",
                        "Crear mi primera meta"
                    ) { showAddGoal = true }
                }
            } else {
                items(state.goals, key = { it.id }) { goal ->
                    SavingsGoalCard(
                        goal = goal,
                        available = state.availableForSavings.coerceAtLeast(0L),
                        onContribute = { contributeTo = goal },
                        onDelete = { goalToDelete = goal }
                    )
                }
            }
            item {
                Spacer(Modifier.height(4.dp))
                HorizontalDivider()
                Spacer(Modifier.height(8.dp))
                Text("Gastos habituales", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
                Text(
                    "Úsalos como plantillas. No se descuentan hasta que los registres.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            if (state.recurringRules.isEmpty()) {
                item {
                    OutlinedButton(onClick = onAdd, modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp)) {
                        Icon(Icons.Default.Add, null)
                        Spacer(Modifier.width(8.dp))
                        Text("Crear gasto habitual")
                    }
                }
            } else {
                items(state.recurringRules, key = { "rule-${it.id}" }) { rule ->
                    RecurringRuleCard(rule, onRegister = { onRegister(rule.id) }, onDelete = { ruleToDelete = rule })
                }
                item {
                    OutlinedButton(onClick = onAdd, modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp)) {
                        Icon(Icons.Default.Add, null)
                        Spacer(Modifier.width(8.dp))
                        Text("Otro gasto habitual")
                    }
                }
            }
        }
    }

    if (showAddGoal) {
        AddGoalDialog(onDismiss = { showAddGoal = false }) { name, amount, date ->
            scope.launch {
                runCatching { viewModel.addGoal(name, amount, date) }
                    .onSuccess { showAddGoal = false; snackbar.showSnackbar("Meta creada") }
                    .onFailure { snackbar.showSnackbar(it.message ?: "No se pudo crear la meta") }
            }
        }
    }

    contributeTo?.let { goal ->
        ContributionDialog(goal, onDismiss = { contributeTo = null }) { amount ->
            scope.launch {
                runCatching { viewModel.addContribution(goal, amount) }
                    .onSuccess {
                        contributeTo = null
                        snackbar.showSnackbar(if (goal.savedAmount + amount >= goal.targetAmount) "¡Meta cumplida!" else "Aporte guardado")
                    }
                    .onFailure { snackbar.showSnackbar(it.message ?: "No se pudo guardar el aporte") }
            }
        }
    }

    goalToDelete?.let { goal ->
        ConfirmRemoval(
            "¿Eliminar esta meta?",
            "Se eliminará “${goal.name}” y su historial de aportes.",
            onDismiss = { goalToDelete = null },
            onConfirm = {
                goalToDelete = null
                scope.launch {
                    runCatching { viewModel.deleteGoal(goal) }
                        .onSuccess { snackbar.showSnackbar("Meta eliminada") }
                        .onFailure { snackbar.showSnackbar("No se pudo eliminar") }
                }
            }
        )
    }

    ruleToDelete?.let { rule ->
        ConfirmRemoval(
            "¿Eliminar este gasto habitual?",
            "Se quitará “${rule.description}” de Plan. Los movimientos registrados se conservarán.",
            onDismiss = { ruleToDelete = null },
            onConfirm = {
                ruleToDelete = null
                scope.launch {
                    runCatching { viewModel.deleteRule(rule) }
                        .onSuccess { snackbar.showSnackbar("Gasto habitual eliminado") }
                        .onFailure { snackbar.showSnackbar("No se pudo eliminar") }
                }
            }
        )
    }
}

@Composable
private fun PlanningSummary(state: PlanningUiState) {
    val available = state.availableForSavings.coerceAtLeast(0L)
    val marginAfterGoals = available - state.requiredForGoals
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Payments, null, tint = MaterialTheme.colorScheme.primary)
                Spacer(Modifier.width(10.dp))
                Text("Tu capacidad de ahorro", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
            }
            Text(money(available), style = MaterialTheme.typography.displaySmall, fontWeight = FontWeight.Bold)
            Text(
                if (state.income == 0L) "Registra un ingreso este mes para obtener una recomendación útil."
                else if (state.availableForSavings < 0L) "Tus compromisos superan los ingresos registrados por ${money(-state.availableForSavings)}."
                else "Es lo que queda este mes después de gastos, pendientes y cuotas aún no registradas.",
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            HorizontalDivider(color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = .18f))
            AmountLine("Ingresos del mes", state.income)
            AmountLine("Gastos ya pagados", state.paidExpenses)
            AmountLine("Pagos pendientes", state.pendingPayments)
            AmountLine("Cuotas aún no registradas", state.unpaidDebtInstallments)
            AmountLine("Aportes realizados este mes", state.savedThisMonth)
            if (state.totalDebtRemaining > 0L) AmountLine("Deuda total restante", state.totalDebtRemaining)
            if (state.goals.any { !it.completed }) {
                HorizontalDivider(color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = .18f))
                AmountLine("Aporte mensual para tus metas", state.requiredForGoals)
                Text(
                    when {
                        state.income == 0L -> "La viabilidad se actualizará cuando registres ingresos."
                        marginAfterGoals >= 0L -> "Tu plan es alcanzable este mes y conserva ${money(marginAfterGoals)} de margen."
                        else -> "Para cumplir todas las fechas faltan ${money(-marginAfterGoals)} este mes. Puedes ajustar un monto o una fecha."
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
    }
}

@Composable
private fun SavingsGoalCard(goal: SavingsGoalEntity, available: Long, onContribute: () -> Unit, onDelete: () -> Unit) {
    val remaining = (goal.targetAmount - goal.savedAmount).coerceAtLeast(0L)
    val monthly = PlanningCalculator.monthlyAmount(remaining, goal.targetDate)
    val progress = if (goal.targetAmount > 0L) goal.savedAmount.toFloat() / goal.targetAmount.toFloat() else 0f
    val date = goal.targetDate?.let {
        Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDate()
            .format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
    }
    OutlinedCard(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(if (goal.completed) Icons.Default.CheckCircle else Icons.Default.Flag, null,
                    tint = MaterialTheme.colorScheme.primary)
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text(goal.name, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
                    Text(if (goal.completed) "Meta cumplida" else "Fecha objetivo: $date",
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                IconButton(onClick = onDelete) { Icon(Icons.Default.DeleteOutline, "Eliminar meta") }
            }
            LinearProgressIndicator(
                progress = { progress.coerceIn(0f, 1f) },
                modifier = Modifier.fillMaxWidth().height(8.dp)
            )
            AmountLine("Ahorrado", goal.savedAmount)
            AmountLine("Objetivo", goal.targetAmount)
            if (!goal.completed) {
                AmountLine("Falta", remaining)
                AmountLine("Aporte mensual sugerido", monthly)
                Text(
                    if (available >= monthly) "✓ Cabe en tu capacidad mensual" else "Requiere ajustar el plan",
                    style = MaterialTheme.typography.labelLarge,
                    color = if (available >= monthly) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                )
                Button(onClick = onContribute, modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp)) {
                    Text("Registrar aporte")
                }
            }
        }
    }
}

@Composable
private fun RecurringRuleCard(rule: RecurringRuleEntity, onRegister: () -> Unit, onDelete: () -> Unit) {
    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Replay, null, tint = MaterialTheme.colorScheme.primary)
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text(rule.description, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    Text(if (rule.isSubscription) "Suscripción" else "Gasto habitual",
                        style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
                }
                IconButton(onClick = onDelete) { Icon(Icons.Default.DeleteOutline, "Eliminar") }
            }
            AmountLine("Monto habitual", rule.amount)
            OutlinedButton(onClick = onRegister, modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp)) {
                Text("Registrar este mes")
            }
        }
    }
}

@Composable
private fun AddGoalDialog(onDismiss: () -> Unit, onAdd: (String, Long, Long) -> Unit) {
    var name by rememberSaveable { mutableStateOf("") }
    var amount by rememberSaveable { mutableStateOf("") }
    var dateText by rememberSaveable { mutableStateOf(LocalDate.now().plusMonths(6).toString()) }
    val context = LocalContext.current
    val date = LocalDate.parse(dateText)
    val amountValue = amount.toLongOrNull()
    val valid = name.isNotBlank() && amountValue != null && amountValue in 1L..99_999_999_999L && !date.isBefore(LocalDate.now())

    AlertDialog(
        onDismissRequest = onDismiss,
        icon = { Icon(Icons.Default.Savings, null) },
        title = { Text("Nueva meta de ahorro") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it.take(120) },
                    label = { Text("¿Para qué quieres ahorrar?") },
                    placeholder = { Text("Ej.: fondo de emergencia") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = amount,
                    onValueChange = { if (it.length <= 11 && it.all(Char::isDigit)) amount = it },
                    label = { Text("Monto objetivo") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    visualTransformation = CurrencyVisualTransformation(),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedButton(onClick = {
                    DatePickerDialog(context, { _, year, month, day ->
                        dateText = LocalDate.of(year, month + 1, day).toString()
                    }, date.year, date.monthValue - 1, date.dayOfMonth).apply {
                        datePicker.minDate = System.currentTimeMillis() - 1_000L
                    }.show()
                }, modifier = Modifier.fillMaxWidth()) {
                    Text("Cumplir el ${date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))}")
                }
                Text(
                    "Usaremos esta fecha para sugerir un aporte mensual y compararlo con tus ingresos y deudas.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        confirmButton = {
            Button(
                enabled = valid,
                onClick = {
                    val epoch = date.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
                    onAdd(name, amountValue!!, epoch)
                }
            ) { Text("Crear meta") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } }
    )
}

@Composable
private fun ContributionDialog(goal: SavingsGoalEntity, onDismiss: () -> Unit, onAdd: (Long) -> Unit) {
    var amount by rememberSaveable(goal.id) { mutableStateOf("") }
    val remaining = (goal.targetAmount - goal.savedAmount).coerceAtLeast(0L)
    val amountValue = amount.toLongOrNull()
    val valid = amountValue != null && amountValue in 1L..remaining
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Aportar a ${goal.name}") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("Te faltan ${money(remaining)} para completar esta meta.")
                OutlinedTextField(
                    value = amount,
                    onValueChange = { if (it.length <= 11 && it.all(Char::isDigit)) amount = it },
                    label = { Text("Monto del aporte") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    visualTransformation = CurrencyVisualTransformation(),
                    singleLine = true,
                    isError = amount.isNotEmpty() && !valid,
                    supportingText = {
                        if (amount.isNotEmpty() && !valid) Text("Ingresa un monto entre $ 1 y ${money(remaining)}.")
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = { Button(enabled = valid, onClick = { onAdd(amountValue!!) }) { Text("Guardar aporte") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } }
    )
}
