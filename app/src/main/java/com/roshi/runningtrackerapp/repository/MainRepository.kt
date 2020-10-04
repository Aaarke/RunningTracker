package com.roshi.runningtrackerapp.repository

import com.roshi.runningtrackerapp.db.RunDao
import com.roshi.runningtrackerapp.db.RunData
import javax.inject.Inject

class MainRepository @Inject constructor(val runDao: RunDao) {
    suspend fun insertRun(runData: RunData) = runDao.insertRunData(runData)
    suspend fun deleteRun(runData: RunData) = runDao.deleteRun(runData)
    fun getAllRunSortedByDate() = runDao.getAllRunDataSortedByDate()
    fun getAllRunDataSortedByMiliSecond() = runDao.getAllRunDataSortedByMiliSecond()
    fun getAllRunDataSortedByCalorie() = runDao.getAllRunDataSortedByCalorie()
    fun getAllRunDataSortedByAverageSpeed() = runDao.getAllRunDataSortedByAverageSpeed()
    fun getAllRunDataSortedByDistance()=runDao.getAllRunDataSortedByDistance()
    fun getTotalCalorieBurned() = runDao.getTotalCalorieBurned()
    fun getTotalDistanceInMeters() = runDao.getTotalDistanceInMeters()
    fun getTotalAverageSpeed() = runDao.getTotalAverageSpeed()


}