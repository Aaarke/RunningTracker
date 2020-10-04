package com.roshi.runningtrackerapp.ui.viewmodel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.roshi.runningtrackerapp.db.RunData
import com.roshi.runningtrackerapp.repository.MainRepository
import kotlinx.coroutines.launch

class MainViewModel @ViewModelInject constructor(val mainRepository: MainRepository) : ViewModel() {


    val runSortedByDate=mainRepository.getAllRunSortedByDate()
    fun insertRun(runData: RunData) = viewModelScope.launch {
        mainRepository.insertRun(runData)
    }

}