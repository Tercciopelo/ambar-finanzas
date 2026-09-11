package com.ambar.finanzas.data.local.entity

import androidx.room.*

@Entity(tableName = "settings")
data class SettingEntity(
    @PrimaryKey val key: String,
    val value: String
)
