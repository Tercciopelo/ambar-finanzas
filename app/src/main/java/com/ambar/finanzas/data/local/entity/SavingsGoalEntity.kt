package com.ambar.finanzas.data.local.entity

import androidx.room.*

@Entity(tableName = "savings_goals")
data class SavingsGoalEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val icon: String = "savings",
    val targetAmount: Long,
    val savedAmount: Long = 0,
    val targetDate: Long? = null,
    val completed: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
