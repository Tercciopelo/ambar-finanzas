package com.ambar.finanzas.data.local.entity

import androidx.room.*

@Entity(tableName = "tags")
data class TagEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String
)
