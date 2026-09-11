package com.ambar.finanzas.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ambar.finanzas.data.local.dao.CategoryTotal

@Composable
fun DonutChart(
    data: List<CategoryTotal>,
    modifier: Modifier = Modifier,
    colors: List<Color> = listOf(
        Color(0xFFE53935), Color(0xFF1E88E5), Color(0xFF8E24AA), Color(0xFFFF8F00), Color(0xFF43A047)
    )
) {
    if (data.isEmpty()) {
        Box(modifier = modifier, contentAlignment = Alignment.Center) {
            Text("Sin datos", color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        return
    }

    val total = data.sumOf { it.total }.toFloat()
    var currentAngle = -90f

    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            data.forEachIndexed { index, item ->
                val sweepAngle = (item.total.toFloat() / total) * 360f
                drawArc(
                    color = colors[index % colors.size],
                    startAngle = currentAngle,
                    sweepAngle = sweepAngle,
                    useCenter = false,
                    style = Stroke(width = 40f, cap = StrokeCap.Butt)
                )
                currentAngle += sweepAngle
            }
        }
        Text(
            text = "Total\n$total", // Simplified formatting
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold
        )
    }
}
