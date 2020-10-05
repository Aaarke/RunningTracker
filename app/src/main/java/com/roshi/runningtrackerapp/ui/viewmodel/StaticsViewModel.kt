package com.roshi.runningtrackerapp.ui.viewmodel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import com.roshi.runningtrackerapp.repository.MainRepository

class StaticsViewModel @ViewModelInject constructor(val mainRepository: MainRepository) :
    ViewModel() {

    val getTotalTimeRun = mainRepository.getTotalTimeInMillis()
    val totalDistance = mainRepository.getTotalDistanceInMeters()
    val getTotalCalorieBurned = mainRepository.getTotalCalorieBurned()
    val getTotalAverageSpeed = mainRepository.getTotalAverageSpeed()

    val runsSortedByDate=mainRepository.getAllRunSortedByDate()

}