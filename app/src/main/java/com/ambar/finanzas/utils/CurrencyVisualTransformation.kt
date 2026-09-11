package com.ambar.finanzas.utils

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation

class CurrencyVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val originalText = text.text
        if (originalText.isEmpty()) {
            return TransformedText(text, OffsetMapping.Identity)
        }

        val formattedText = try {
            val amount = originalText.toLong()
            val formatted = String.format("%,d", amount).replace(',', '.')
            "$ formatted"
        } catch (e: Exception) {
            originalText
        }

        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                if (originalText.isEmpty()) return 0
                val textSubset = originalText.take(offset)
                val formattedSubset = try {
                    if (textSubset.isEmpty()) "" else String.format("%,d", textSubset.toLong()).replace(',', '.')
                } catch (e: Exception) { textSubset }
                // Adding 2 for the "$ " prefix
                return formattedSubset.length + 2
            }

            override fun transformedToOriginal(offset: Int): Int {
                if (offset <= 2) return 0
                val textWithoutPrefix = formattedText.substring(2).take(offset - 2)
                val dotsCount = textWithoutPrefix.count { it == '.' }
                return (offset - 2 - dotsCount).coerceIn(0, originalText.length)
            }
        }

        return TransformedText(AnnotatedString(formattedText), offsetMapping)
    }
}
