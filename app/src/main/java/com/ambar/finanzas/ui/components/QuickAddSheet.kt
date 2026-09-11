package com.ambar.finanzas.ui.components

import android.app.DatePickerDialog
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ambar.finanzas.data.repository.FinanceRepository
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuickAddSheet(repository: FinanceRepository, onDismiss: () -> Unit, onShowSnackbar: (String) -> Unit,
    initialExpense: Boolean = true, initialRuleId: Long? = null, initialRepeat: Boolean = false) {
    val vm: QuickAddViewModel = viewModel(factory = QuickAddViewModel.Factory(repository))
    val save by vm.state.collectAsStateWithLifecycle()
    val rules by remember { repository.getActiveRecurringRules() }.collectAsStateWithLifecycle(emptyList())
    val categories by remember { repository.getCategories() }.collectAsStateWithLifecycle(emptyList())
    var expense by rememberSaveable { mutableStateOf(initialExpense) }
    var amount by rememberSaveable { mutableStateOf("") }
    var description by rememberSaveable { mutableStateOf("") }
    var note by rememberSaveable { mutableStateOf("") }
    var pending by rememberSaveable { mutableStateOf(false) }
    var repeat by rememberSaveable { mutableStateOf(initialRepeat) }
    var subscription by rememberSaveable { mutableStateOf(false) }
    var ruleId by rememberSaveable { mutableStateOf<Long?>(null) }
    var categoryId by rememberSaveable { mutableStateOf<Long?>(null) }
    var dateText by rememberSaveable { mutableStateOf(LocalDate.now().toString()) }
    var more by rememberSaveable { mutableStateOf(initialRepeat) }
    var discard by remember { mutableStateOf(false) }
    var categoryMenu by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val date = LocalDate.parse(dateText)
    val validAmount = amount.toLongOrNull()?.let { it in 1..99_999_999_999L } == true
    val validDate = pending || !date.isAfter(LocalDate.now())
    val requestClose = {
        if (!save.busy) {
            if (amount.isNotEmpty() || description.isNotBlank()) discard = true
            else { vm.consume(); onDismiss() }
        }
    }
    LaunchedEffect(initialRuleId, rules) {
        if (initialRuleId != null && ruleId == null && description.isEmpty()) {
            rules.find { it.id == initialRuleId }?.let {
                ruleId = it.id; description = it.description; amount = it.amount.toString(); categoryId = it.categoryId
            }
        }
    }
    LaunchedEffect(save.saved) {
        if (save.saved) { vm.consume(); onShowSnackbar("Movimiento guardado"); onDismiss() }
    }
    ModalBottomSheet(onDismissRequest = requestClose,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true,
            confirmValueChange = { !save.busy && it != SheetValue.Hidden })) {
        Column(Modifier.fillMaxWidth().imePadding().verticalScroll(rememberScrollState()).padding(horizontal = 20.dp).padding(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Nuevo movimiento", Modifier.weight(1f), style = MaterialTheme.typography.headlineSmall)
                IconButton(onClick = requestClose, enabled = !save.busy) { Icon(Icons.Default.Close, "Cerrar") }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(selected = expense, enabled = !save.busy, onClick = { expense = true }, label = { Text("Gasto") }, modifier = Modifier.weight(1f))
                FilterChip(selected = !expense, enabled = !save.busy, onClick = { expense = false; pending = false; repeat = false; ruleId = null },
                    label = { Text("Ingreso") }, modifier = Modifier.weight(1f))
            }
            OutlinedTextField(value = amount, onValueChange = { if (it.length <= 11 && it.all { c -> c in '0'..'9' }) amount = it },
                label = { Text("Monto en pesos") }, placeholder = { Text("$ 0") }, modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), singleLine = true, enabled = !save.busy,
                visualTransformation = com.ambar.finanzas.utils.CurrencyVisualTransformation(), textStyle = MaterialTheme.typography.headlineLarge,
                isError = amount.isNotEmpty() && !validAmount, supportingText = { if (amount.isNotEmpty() && !validAmount) Text("Ingresa un monto mayor que cero.") })
            OutlinedTextField(value = description, onValueChange = { description = it.take(120); ruleId = null }, enabled = !save.busy,
                label = { Text(if (expense) "¿En qué?" else "¿De dónde?") }, placeholder = { Text(if (expense) "Supermercado, almuerzo…" else "Sueldo, transferencia…") },
                modifier = Modifier.fillMaxWidth(), singleLine = true)
            val suggestions = rules.filter { it.description.contains(description, true) }.take(5)
            if (expense && suggestions.isNotEmpty() && ruleId == null) {
                Text("Tus gastos habituales", style = MaterialTheme.typography.labelLarge)
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(suggestions, key = { it.id }) { rule ->
                        SuggestionChip(enabled = !save.busy, onClick = {
                            ruleId = rule.id; description = rule.description; amount = rule.amount.toString(); categoryId = rule.categoryId
                        }, label = { Text(rule.description) })
                    }
                }
            }
            if (expense) Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(selected = !pending, enabled = !save.busy, onClick = { pending = false }, label = { Text("Ya lo pagué") })
                FilterChip(selected = pending, enabled = !save.busy, onClick = { pending = true }, label = { Text("Por pagar") })
            }
            TextButton(enabled = !save.busy, onClick = { more = !more }) { Text(if (more) "Menos detalles" else "Fecha, categoría y más") }
            AnimatedVisibility(more) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedButton(enabled = !save.busy, onClick = {
                        DatePickerDialog(context, { _, y, m, day -> dateText = LocalDate.of(y, m + 1, day).toString() },
                            date.year, date.monthValue - 1, date.dayOfMonth).show()
                    }) { Text((if (pending) "Vence: " else "Fecha: ") + date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))) }
                    ExposedDropdownMenuBox(expanded = categoryMenu, onExpandedChange = { if (!save.busy) categoryMenu = !categoryMenu }) {
                        OutlinedTextField(value = categories.find { it.id == categoryId }?.name ?: "Sin categoría", onValueChange = {},
                            readOnly = true, label = { Text("Categoría") }, modifier = Modifier.fillMaxWidth().menuAnchor(),
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(categoryMenu) })
                        ExposedDropdownMenu(expanded = categoryMenu, onDismissRequest = { categoryMenu = false }) {
                            DropdownMenuItem(text = { Text("Sin categoría") }, onClick = { categoryId = null; categoryMenu = false })
                            categories.forEach { cat -> DropdownMenuItem(text = { Text(cat.name) }, onClick = { categoryId = cat.id; categoryMenu = false }) }
                        }
                    }
                    OutlinedTextField(value = note, onValueChange = { note = it.take(500) }, enabled = !save.busy,
                        label = { Text("Nota opcional") }, modifier = Modifier.fillMaxWidth(), maxLines = 3)
                    if (expense && ruleId == null) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(checked = repeat, enabled = !save.busy, onCheckedChange = { repeat = it })
                            Text("Guardar como gasto habitual", Modifier.weight(1f))
                        }
                        if (repeat) {
                            Text("Quedará en Plan para volver a registrarlo cada mes. No se crearán cargos automáticos.", style = MaterialTheme.typography.bodySmall)
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Checkbox(checked = subscription, enabled = !save.busy, onCheckedChange = { subscription = it })
                                Text("Es una suscripción")
                            }
                        }
                    }
                }
            }
            if (!validDate) Text("Una fecha futura debe registrarse como pago pendiente.", color = MaterialTheme.colorScheme.error)
            save.error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
            if (!validAmount || description.isBlank()) Text("Completa el monto y la descripción para guardar.", style = MaterialTheme.typography.bodySmall)
            Button(enabled = !save.busy && !save.saved && validAmount && description.isNotBlank() && validDate,
                onClick = { vm.save(amount.toLong(), description, expense, date, pending, categoryId, note, repeat, subscription, ruleId) },
                modifier = Modifier.fillMaxWidth().heightIn(min = 56.dp)) {
                if (save.busy) CircularProgressIndicator(Modifier.size(22.dp), color = MaterialTheme.colorScheme.onPrimary, strokeWidth = 2.dp)
                else Text(if (pending) "Guardar pendiente" else if (expense) "Guardar gasto" else "Guardar ingreso")
            }
        }
    }
    if (discard) AlertDialog(onDismissRequest = { discard = false }, title = { Text("¿Salir sin guardar?") },
        text = { Text("Se perderá lo que escribiste en este movimiento.") },
        confirmButton = { TextButton(onClick = { vm.consume(); onDismiss() }) { Text("Salir sin guardar") } },
        dismissButton = { TextButton(onClick = { discard = false }) { Text("Seguir anotando") } })
}
