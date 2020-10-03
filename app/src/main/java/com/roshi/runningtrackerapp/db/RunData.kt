package com.roshi.runningtrackerapp.db

import android.graphics.Bitmap
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "running_table")
class RunData(
    var image: Bitmap? = null,
    var timeStamp: Long = 0L,
    var averageSpeed: Float,
    var distanceInMeters: Int = 0,
    var timeInmilis: Long = 0L,
    var caloriesBurned: Int = 0
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null
}