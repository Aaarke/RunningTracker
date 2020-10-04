package com.roshi.runningtrackerapp.ui.viewmodel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.roshi.runningtrackerapp.db.RunData
import com.roshi.runningtrackerapp.other.SortType
import com.roshi.runningtrackerapp.repository.MainRepository
import kotlinx.coroutines.launch

class MainViewModel @ViewModelInject constructor(val mainRepository: MainRepository) : ViewModel() {


    private val runSortedByDate = mainRepository.getAllRunSortedByDate()
    private val runSortedByMiliSecond = mainRepository.getAllRunDataSortedByMiliSecond()
    private val runSortedByCalorie = mainRepository.getAllRunDataSortedByCalorie()
    private val runDataSortedByAverageSpeed = mainRepository.getAllRunDataSortedByAverageSpeed()
    private val runDataSortedByDistance = mainRepository.getAllRunDataSortedByDistance()


    val runs = MediatorLiveData<List<RunData>>()
    var sortType = SortType.DATE

    init {
        runs.addSource(runSortedByDate) { result ->
            if (sortType == SortType.DATE) {
                result?.let {
                    runs.value = it
                }
            }


        }
        runs.addSource(runDataSortedByAverageSpeed) { result ->
            if (sortType == SortType.AVG_SPEED) {
                result?.let {
                    runs.value = it
                }
            }
        }
        runs.addSource(runSortedByCalorie) { result ->
            if (sortType == SortType.CALORIES_BURNED) {
                result?.let {
                    runs.value = it
                }
            }
        }
        runs.addSource(runSortedByMiliSecond) { result ->
            if (sortType == SortType.RUNNING_TIME) {
                result?.let {
                    runs.value = it
                }
            }
        }

        runs.addSource(runDataSortedByDistance) { result ->
            if (sortType == SortType.DISTANCE) {
                result?.let {
                    runs.value = it
                }
            }
        }
    }

    fun insertRun(runData: RunData) = viewModelScope.launch {
        mainRepository.insertRun(runData)
    }

    fun sortRuns(sortType: SortType) = when (sortType) {
        SortType.DATE -> runSortedByDate.value?.let { runs.value = it }
        SortType.CALORIES_BURNED -> runSortedByCalorie.value?.let { runs.value = it }
        SortType.RUNNING_TIME -> runSortedByMiliSecond.value?.let { runs.value = it }
        SortType.DISTANCE -> runDataSortedByDistance.value?.let { runs.value = it }
        SortType.AVG_SPEED -> runDataSortedByAverageSpeed.value?.let { runs.value = it }
    }.also {
        this.sortType = sortType
    }

}