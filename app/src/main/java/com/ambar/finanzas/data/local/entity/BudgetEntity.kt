package com.ambar.finanzas.data.local.entity

import androidx.room.*

@Entity(tableName = "budgets")
data class BudgetEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val monthlyLimit: Long,
    val alert75: Boolean = true,
    val alert90: Boolean = true,
    val alert100: Boolean = true,
    val updatedAt: Long = System.currentTimeMillis()
)
