package com.ontime.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.ontime.data.model.FocusSession

/**
 * Room database for storing focus sessions locally
 */
@Database(
    entities = [FocusSession::class],
    version = 2,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class OnTimeDatabase : RoomDatabase() {
    
    abstract fun focusSessionDao(): FocusSessionDao
    
    companion object {
        @Volatile
        private var INSTANCE: OnTimeDatabase? = null
        
        fun getDatabase(context: Context): OnTimeDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    OnTimeDatabase::class.java,
                    "ontime_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

import androidx.room.TypeConverter

class Converters {
    @TypeConverter
    fun fromList(value: List<String>?): String {
        return value?.joinToString(",") ?: ""
    }
    
    @TypeConverter
    fun toList(value: String): List<String> {
        return if (value.isEmpty()) emptyList() else value.split(",")
    }
}
