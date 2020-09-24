package com.roshi.runningtrackerapp.ui.fragments

import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.roshi.runningtrackerapp.R
import com.roshi.runningtrackerapp.ui.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingFragment:Fragment(R.layout.fragment_setting) {
    private val viewModel: MainViewModel by viewModels()
}