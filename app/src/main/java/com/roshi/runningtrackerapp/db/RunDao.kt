package com.roshi.runningtrackerapp.db

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface RunDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRunData(runData: RunData)

    @Delete
    suspend fun deleteRun(runData: RunData)

    @Query("SELECT * FROM running_table ORDER By timeStamp DESC")
    fun getAllRunDataSortedByDate(): LiveData<List<RunData>>

    @Query("SELECT * FROM running_table ORDER By timeInmilis DESC")
    fun getAllRunDataSortedByMiliSecond(): LiveData<List<RunData>>

    @Query("SELECT * FROM running_table ORDER By caloriesBurned DESC")
    fun getAllRunDataSortedByCalorie(): LiveData<List<RunData>>

    @Query("SELECT * FROM running_table ORDER By averageSpeed DESC")
    fun getAllRunDataSortedByAverageSpeed(): LiveData<List<RunData>>

    @Query("SELECT * FROM running_table ORDER By distanceInMeters DESC")
    fun getAllRunDataSortedByDistance(): LiveData<List<RunData>>

    @Query("SELECT SUM(timeInmilis) FROM running_table")
    fun getTotalTimeInMiliSecond(): LiveData<Long>

    @Query("SELECT SUM(caloriesBurned) FROM running_table")
    fun getTotalCalorieBurned(): LiveData<Int>

    @Query("SELECT SUM(distanceInMeters) FROM running_table")
    fun getTotalDistanceInMeters(): LiveData<Long>

    @Query("SELECT AVG(averageSpeed) FROM running_table")
    fun getTotalAverageSpeed(): LiveData<Float>

}