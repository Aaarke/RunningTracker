package com.roshi.runningtrackerapp.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.PolylineOptions
import com.roshi.runningtrackerapp.R
import com.roshi.runningtrackerapp.other.Constant.ACTION_PAUSE_SERVICE
import com.roshi.runningtrackerapp.other.Constant.ACTION_RESUME__SERVICE
import com.roshi.runningtrackerapp.other.Constant.ACTION_SHOW_TRACKING_FRAGMENT
import com.roshi.runningtrackerapp.other.Constant.ACTION_START_OR_RESUME_SERVICE
import com.roshi.runningtrackerapp.other.Constant.MAP_ZOOM
import com.roshi.runningtrackerapp.other.Constant.POLY_LINE_COLOR
import com.roshi.runningtrackerapp.other.Constant.POLY_LINE_WIDTH
import com.roshi.runningtrackerapp.other.TrackingUtility
import com.roshi.runningtrackerapp.service.PolyLine
import com.roshi.runningtrackerapp.service.PolyLines
import com.roshi.runningtrackerapp.service.TrackingService
import com.roshi.runningtrackerapp.ui.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_tracking.*

@AndroidEntryPoint
class TrackingFragment : Fragment(R.layout.fragment_tracking) {
    private val viewModel: MainViewModel by viewModels()
    private var isTracking:Boolean=false
    private var pathPoints= mutableListOf<PolyLine>()
    private var map: GoogleMap? = null
    private var currentTimeInmillis=0L
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mapView?.onCreate(savedInstanceState)
        btnToggleRun.setOnClickListener {
           toggleRun()
        }
        mapView.getMapAsync {
            map = it
            addAllPolyLines()
        }
        subscribeToObserver()

    }


    private fun sendCommandToService(action: String) =
        Intent(requireContext(), TrackingService::class.java).also {
            it.action = action
            requireContext().startService(it)
        }

    private fun addLatestPolyLines(){
        if (pathPoints.isNotEmpty()&&pathPoints.last().size>1){
            val preLastLatLng=pathPoints.last()[pathPoints.last().size-2]
            var lastLatLng=pathPoints.last().last()
            val ployLinesOptions=PolylineOptions()
                .color(POLY_LINE_COLOR)
                .width(POLY_LINE_WIDTH)
                .add(preLastLatLng)
                .add(lastLatLng)
            map?.addPolyline(ployLinesOptions)
        }
    }

    private fun moveCameraToUser(){
        if(pathPoints.isNotEmpty()&&pathPoints.last().isNotEmpty()){
            map?.animateCamera(CameraUpdateFactory.newLatLngZoom(pathPoints.last().last(),MAP_ZOOM))
        }
    }

    private fun updateTracking(isTracking:Boolean){
        this.isTracking=isTracking
        if (!isTracking){
            btnToggleRun.text="Start"
            btnFinishRun.visibility=View.VISIBLE
        }else{
            btnToggleRun.text="Stop"
            btnFinishRun.visibility=View.GONE

        }
    }

    private fun toggleRun(){
        if (isTracking){
            sendCommandToService(ACTION_PAUSE_SERVICE)
        }else{
            sendCommandToService(ACTION_START_OR_RESUME_SERVICE)
        }
    }

    private fun subscribeToObserver(){
        TrackingService.isTracking.observe(viewLifecycleOwner, Observer {
            updateTracking(it)
        })

        TrackingService.pathPoints.observe(viewLifecycleOwner, Observer {
            pathPoints=it
            addLatestPolyLines()
            moveCameraToUser()

        })

        TrackingService.timeRunInMillis.observe(viewLifecycleOwner, Observer {
            currentTimeInmillis=it
            val formattedTimes=TrackingUtility.getFormattedStopWatchTime(currentTimeInmillis,false)
            tvTimer.text = formattedTimes
        })
    }

    private fun addAllPolyLines(){
        for (polyline in pathPoints){
            val polyLineOptions=PolylineOptions().color(POLY_LINE_COLOR).width(POLY_LINE_WIDTH).addAll(polyline)
            map?.addPolyline(polyLineOptions)
        }
    }


    override fun onResume() {
        super.onResume()
        mapView?.onResume()
    }

    override fun onStart() {
        super.onStart()
        mapView?.onStart()
    }

    override fun onStop() {
        super.onStop()
        mapView?.onStop()
    }

    override fun onPause() {
        super.onPause()
        mapView?.onPause()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView?.onLowMemory()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView?.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView?.onSaveInstanceState(outState)
    }


}