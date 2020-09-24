package com.roshi.runningtrackerapp.ui.fragments

import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.roshi.runningtrackerapp.R
import com.roshi.runningtrackerapp.ui.viewmodel.MainViewModel
import com.roshi.runningtrackerapp.ui.viewmodel.StaticsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class StaticsFragment:Fragment(R.layout.fragment_statics) {
    private val viewModel: StaticsViewModel by viewModels()

}