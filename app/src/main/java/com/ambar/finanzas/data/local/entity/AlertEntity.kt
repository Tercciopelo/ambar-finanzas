package com.ambar.finanzas.data.local.entity

import androidx.room.*

@Entity(tableName = "alerts")
data class AlertEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val type: String, // PAYMENT_DUE, PAYMENT_OVERDUE, INSTALLMENT, BUDGET, GOAL, SUBSCRIPTION
    val title: String,
    val message: String,
    val referenceId: Long? = null,
    val date: Long,
    val read: Boolean = false,
    val dismissed: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
