package com.ambar.finanzas.data.local.entity

import androidx.room.*

@Entity(tableName = "reserved_money")
data class ReservedMoneyEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val amount: Long,
    val icon: String = "account_balance_wallet",
    val createdAt: Long = System.currentTimeMillis()
)
