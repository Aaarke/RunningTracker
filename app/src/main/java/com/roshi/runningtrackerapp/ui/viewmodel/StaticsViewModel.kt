package com.roshi.runningtrackerapp.ui.viewmodel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import com.roshi.runningtrackerapp.repository.MainRepository

class StaticsViewModel @ViewModelInject constructor(val mainRepository: MainRepository):ViewModel() {

}