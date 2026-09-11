package com.ambar.finanzas.utils

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation

class CurrencyVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val raw = text.text
        if (raw.isEmpty() || raw.any { it !in '0'..'9' }) return TransformedText(text, OffsetMapping.Identity)
        val result = StringBuilder("$ ")
        val positions = IntArray(raw.length + 1)
        raw.forEachIndexed { index, char ->
            if (index > 0 && (raw.length - index) % 3 == 0) result.append('.')
            positions[index] = result.length
            result.append(char)
        }
        positions[raw.length] = result.length
        val formatted = result.toString()
        return TransformedText(AnnotatedString(formatted), object : OffsetMapping {
            override fun originalToTransformed(offset: Int) = positions[offset.coerceIn(0, raw.length)]
            override fun transformedToOriginal(offset: Int) = formatted.take(offset.coerceIn(0, formatted.length)).count { it in '0'..'9' }
        })
    }
}
