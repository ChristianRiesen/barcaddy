package com.christianriesen.barcaddy.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters

class Converters {
    @TypeConverter fun fromKind(value: CodeKind): String = value.name
    @TypeConverter fun toKind(value: String): CodeKind = CodeKind.valueOf(value)
}

@Database(entities = [Card::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class CardDatabase : RoomDatabase() {
    abstract fun cardDao(): CardDao

    companion object {
        @Volatile private var instance: CardDatabase? = null

        fun get(context: Context): CardDatabase = instance ?: synchronized(this) {
            instance ?: Room.databaseBuilder(
                context.applicationContext,
                CardDatabase::class.java,
                "barcaddy.db",
            ).build().also { instance = it }
        }
    }
}
