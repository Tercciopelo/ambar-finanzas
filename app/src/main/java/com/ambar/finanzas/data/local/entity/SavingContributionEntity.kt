package com.ambar.finanzas.data.local.entity

import androidx.room.*

@Entity(
    tableName = "saving_contributions",
    foreignKeys = [
        ForeignKey(
            entity = SavingsGoalEntity::class,
            parentColumns = ["id"],
            childColumns = ["goalId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("goalId")]
)
data class SavingContributionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val goalId: Long,
    val amount: Long,
    val date: Long = System.currentTimeMillis()
)
