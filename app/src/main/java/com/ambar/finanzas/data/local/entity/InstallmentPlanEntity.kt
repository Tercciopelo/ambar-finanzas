package com.ambar.finanzas.data.local.entity

import androidx.room.*

@Entity(
    tableName = "installment_plans",
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
data class InstallmentPlanEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val uuid: String,
    val name: String,
    val installmentAmount: Long,
    val currentInstallment: Int,
    val totalInstallments: Int,
    val startMonth: String, // "2026-09"
    val categoryId: Long? = null,
    val status: String = "ACTIVE", // ACTIVE, COMPLETED
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
