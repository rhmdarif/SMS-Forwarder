package id.majopay.gateway.data.local.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import android.content.Context
import id.majopay.gateway.data.local.converter.Converters
import id.majopay.gateway.data.local.dao.RuleDao
import id.majopay.gateway.data.local.dao.HistoryDao
import id.majopay.gateway.data.local.entity.RuleEntity
import id.majopay.gateway.data.local.entity.HistoryEntity

/**
 * Room database for SMS Forwarder app.
 * Contains rules and history tables with their respective DAOs.
 * 
 * Version 4: Added support for notifications with source type and package filtering.
 * - Updated RuleEntity with source and packageFilter fields
 * - Updated HistoryEntity with comprehensive SMS and notification support
 */
@Database(
    entities = [RuleEntity::class, HistoryEntity::class],
    version = 4,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class SmsForwarderDatabase : RoomDatabase() {
    
    /**
     * DAO for rules table operations.
     */
    abstract fun ruleDao(): RuleDao
    
    /**
     * DAO for history table operations.
     */
    abstract fun historyDao(): HistoryDao
    
    companion object {
        /**
         * Database name.
         */
        const val DATABASE_NAME = "sms_forwarder_database"
        
        /**
         * Singleton instance of the database.
         */
        @Volatile
        private var INSTANCE: SmsForwarderDatabase? = null
        
        /**
         * Get the database instance.
         * Creates the database if it doesn't exist.
         * 
         * @param context Application context
         * @return Database instance
         */
        fun getDatabase(context: Context): SmsForwarderDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SmsForwarderDatabase::class.java,
                    DATABASE_NAME
                )
                    .fallbackToDestructiveMigration() // For development - replace with proper migrations in production
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
} 