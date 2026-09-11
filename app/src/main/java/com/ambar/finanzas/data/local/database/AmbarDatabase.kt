package com.ambar.finanzas.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.ambar.finanzas.data.local.dao.*
import com.ambar.finanzas.data.local.entity.*

@Database(
    entities = [
        TransactionEntity::class,
        CategoryEntity::class,
        RecurringRuleEntity::class,
        InstallmentPlanEntity::class,
        BudgetEntity::class,
        CategoryBudgetEntity::class,
        SavingsGoalEntity::class,
        SavingContributionEntity::class,
        ReservedMoneyEntity::class,
        AlertEntity::class,
        SettingEntity::class,
        TagEntity::class,
        TransactionTagCrossRef::class
    ],
    version = 1,
    exportSchema = true
)
abstract class AmbarDatabase : RoomDatabase() {

    abstract fun transactionDao(): TransactionDao
    abstract fun categoryDao(): CategoryDao
    abstract fun recurringRuleDao(): RecurringRuleDao
    abstract fun installmentPlanDao(): InstallmentPlanDao
    abstract fun budgetDao(): BudgetDao
    abstract fun alertDao(): AlertDao
    abstract fun settingDao(): SettingDao

    companion object {
        @Volatile
        private var INSTANCE: AmbarDatabase? = null

        fun getInstance(context: Context): AmbarDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AmbarDatabase::class.java,
                    "ambar_finanzas.db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
