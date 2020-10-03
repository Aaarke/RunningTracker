package com.roshi.runningtrackerapp.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters

@Database(entities = [RunData::class], version = 1)
@TypeConverters(Converters::class)
abstract class RunningDataBase : RoomDatabase() {
    abstract fun getRunDao(): RunDao
}