package com.ambar.finanzas.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.ambar.finanzas.data.repository.FinanceRepository
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuickAddSheet(
    repository: FinanceRepository,
    onDismiss: () -> Unit,
    onShowSnackbar: (String) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()
    val focusRequester = remember { FocusRequester() }

    var isExpense by remember { mutableStateOf(true) }
    var amountText by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var isSaving by remember { mutableStateOf(false) }

    var showMoreOptions by remember { mutableStateOf(false) }
    var note by remember { mutableStateOf("") }
    var status by remember { mutableStateOf("PAID") }
    var isRecurring by remember { mutableStateOf(false) }
    var isSubscription by remember { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = isExpense,
                    onClick = { isExpense = true },
                    label = { Text("Gasto") },
                    modifier = Modifier.weight(1f)
                )
                FilterChip(
                    selected = !isExpense,
                    onClick = { isExpense = false },
                    label = { Text("Ingreso") },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                if (isExpense) "¿Cuánto gastaste?" else "¿Cuánto recibiste?",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = amountText,
                onValueChange = { newVal ->
                    if (newVal.all { c -> c.isDigit() } && newVal.length <= 11) amountText = newVal
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                visualTransformation = com.ambar.finanzas.utils.CurrencyVisualTransformation(),
                textStyle = MaterialTheme.typography.headlineMedium,
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(if (isExpense) "¿En qué?" else "¿De dónde?", style = MaterialTheme.typography.titleMedium)

            Spacer(modifier = Modifier.height(8.dp))

            var expanded by remember { mutableStateOf(false) }
            val activeRules by repository.getActiveRecurringRules().collectAsState(initial = emptyList())

            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it; expanded = true },
                    modifier = Modifier.fillMaxWidth().menuAnchor(),
                    placeholder = { Text(if (isExpense) "Ej: Uber, Almuerzo..." else "Ej: Sueldo, Transferencia...") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )

                if (activeRules.isNotEmpty()) {
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        activeRules.forEach { rule ->
                            DropdownMenuItem(
                                text = { Text("${rule.description} (${com.ambar.finanzas.utils.CurrencyUtils.formatCLP(rule.amount)})") },
                                onClick = {
                                    description = rule.description
                                    amountText = rule.amount.toString()
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))

            TextButton(onClick = { showMoreOptions = !showMoreOptions }) {
                Text("Más opciones")
                Icon(
                    imageVector = if (showMoreOptions) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = null
                )
            }

            AnimatedVisibility(visible = showMoreOptions) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = note,
                        onValueChange = { note = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Nota") },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(checked = isRecurring, onCheckedChange = { isRecurring = it })
                        Text("Repetir mensualmente")
                    }
                    if (isExpense && isRecurring) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(checked = isSubscription, onCheckedChange = { isSubscription = it })
                            Text("Es suscripción")
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    val amount = amountText.toLongOrNull()
                    if (amount == null || amount <= 0 || description.isBlank()) return@Button
                    isSaving = true

                    scope.launch {
                        try {
                            if (isExpense) {
                                repository.addQuickExpense(amount, description.trim(), null, note, isRecurring, isSubscription)
                            } else {
                                repository.addQuickIncome(amount, description.trim(), null, note, isRecurring)
                            }
                            onShowSnackbar("Guardado ✓")
                            onDismiss()
                        } catch (_: Exception) {
                            isSaving = false
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = !isSaving && amountText.isNotBlank() && description.isNotBlank(),
                shape = RoundedCornerShape(12.dp)
            ) {
                if (isSaving) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("GUARDAR", style = MaterialTheme.typography.titleMedium)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
}