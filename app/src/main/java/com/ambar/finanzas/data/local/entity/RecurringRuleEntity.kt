package com.ambar.finanzas.data.local.entity

import androidx.room.*

@Entity(
    tableName = "recurring_rules",
    foreignKeys = [
        ForeignKey(
            entity = CategoryEntity::class,
            parentColumns = ["id"],
            childColumns = ["categoryId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [Index("categoryId")]
)
data class RecurringRuleEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val uuid: String,
    val description: String,
    val amount: Long,
    val categoryId: Long? = null,
    val dayOfMonth: Int = 1,
    val active: Boolean = true,
    val isSubscription: Boolean = false,
    val startMonth: String, // "2026-09"
    val endMonth: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
