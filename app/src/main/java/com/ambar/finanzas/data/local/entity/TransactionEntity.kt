package com.ambar.finanzas.data.local.entity

import androidx.room.*

@Entity(
    tableName = "transactions",
    foreignKeys = [
        ForeignKey(
            entity = CategoryEntity::class,
            parentColumns = ["id"],
            childColumns = ["categoryId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [Index("categoryId"), Index("date"), Index("monthKey")]
)
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val uuid: String,
    val type: String, // INCOME, EXPENSE
    val amount: Long, // CLP in integer
    val description: String,
    val categoryId: Long? = null,
    val date: Long, // epoch millis
    val monthKey: String, // "2026-09"
    val status: String = "PAID", // PAID, PENDING, OVERDUE
    val note: String = "",
    val isRecurring: Boolean = false,
    val recurringRuleId: Long? = null,
    val installmentPlanId: Long? = null,
    val installmentNumber: Int? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
