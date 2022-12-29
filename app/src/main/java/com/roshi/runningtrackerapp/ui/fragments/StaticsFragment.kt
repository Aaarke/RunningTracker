package com.roshi.runningtrackerapp.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.roshi.runningtrackerapp.R
import com.roshi.runningtrackerapp.other.TrackingUtility
import com.roshi.runningtrackerapp.ui.viewmodel.StaticsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_statics.*
import kotlin.math.round

@AndroidEntryPoint
class StaticsFragment : Fragment(R.layout.fragment_statics) {
    private val viewModel: StaticsViewModel by viewModels()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        subscribeToObserver()
    }

    private fun subscribeToObserver(){
        viewModel.getTotalTimeRun.observe(viewLifecycleOwner, {
            it?.let {
               val totalTimeRun=TrackingUtility.getFormattedStopWatchTime(it,false)
                tvTotalTime.text=totalTimeRun
            }
        })

        viewModel.getTotalCalorieBurned.observe(viewLifecycleOwner, {
            it?.let {
                val totalCaloriesBurned="${it}kcal"
                tvTotalCalories.text=totalCaloriesBurned
            }
        
        })

        viewModel.getTotalAverageSpeed.observe(viewLifecycleOwner) {
            val avgSped = round(it * 10f) / 10f
            val avgSpeedString = "${avgSped}Km/h"
            tvAverageSpeed.text = avgSpeedString

        }

        viewModel.totalDistance.observe(viewLifecycleOwner, {
            it?.let {
                val km=it/1000f
                val totalDistance= round(km*10f)/10f
                val totalDistanceString="${totalDistance}Km"
                tvTotalDistance.text=totalDistanceString

            }
        })

        viewModel.runsSortedByDate.observe(viewLifecycleOwner, {

        })
    }

}