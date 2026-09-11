package com.ambar.finanzas.data.local.entity

import androidx.room.*

@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val icon: String = "category",
    val color: Long = 0xFFFFB300,
    val order: Int = 0,
    val hidden: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
