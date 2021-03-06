package com.roshi.runningtrackerapp.repository

import com.roshi.runningtrackerapp.db.RunDao
import com.roshi.runningtrackerapp.db.RunData
import javax.inject.Inject

class MainRepository @Inject constructor(private val runDao: RunDao) {
    suspend fun insertRun(runData: RunData) = runDao.insertRunData(runData)
    suspend fun deleteRun(runData: RunData) = runDao.deleteRun(runData)
    fun getAllRunSortedByDate() = runDao.getAllRunDataSortedByDate()
    fun getAllRunDataSortedByMiliSecond() = runDao.getAllRunDataSortedByMiliSecond()
    fun getAllRunDataSortedByCalorie() = runDao.getAllRunDataSortedByCalorie()
    fun getAllRunDataSortedByAverageSpeed() = runDao.getAllRunDataSortedByAverageSpeed()
    fun getAllRunDataSortedByDistance()=runDao.getAllRunDataSortedByDistance()

    fun getTotalTimeInMillis() = runDao.getTotalTimeInMiliSecond()
    fun getTotalDistanceInMeters() = runDao.getTotalDistanceInMeters()
    fun getTotalCalorieBurned() = runDao.getTotalCalorieBurned()
    fun getTotalAverageSpeed() = runDao.getTotalAverageSpeed()





}