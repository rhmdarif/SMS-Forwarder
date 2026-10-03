package id.majopay.ngateway.data.local.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import android.content.Context
import id.majopay.ngateway.data.local.converter.Converters
import id.majopay.ngateway.data.local.dao.RuleDao
import id.majopay.ngateway.data.local.dao.HistoryDao
import id.majopay.ngateway.data.local.entity.RuleEntity
import id.majopay.ngateway.data.local.entity.HistoryEntity

/**
 * Room database for Majopay Gateway.
 * Contains rules and history tables with their respective DAOs.
 * 
 * Version 4: Added support for notifications with source type and package filtering.
 * Version 5: Sumber SMS dihapus; aturan dan riwayat SMS lama dibuang (skema tidak berubah).
 */
@Database(
    entities = [RuleEntity::class, HistoryEntity::class],
    version = 5,
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
         * Buang aturan dan riwayat bersumber SMS. Fitur SMS sudah dihapus, jadi baris lama
         * tidak akan pernah diproses lagi; tanpa migrasi ini data notifikasi ikut terhapus
         * oleh fallbackToDestructiveMigration.
         */
        val MIGRATION_4_5 = object : Migration(4, 5) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("DELETE FROM rules WHERE source = 'SMS'")
                db.execSQL("DELETE FROM forwarding_history WHERE source_type = 'SMS'")
            }
        }
        
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
                    .addMigrations(MIGRATION_4_5)
                    .fallbackToDestructiveMigration() // For development - replace with proper migrations in production
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
} 