package com.roshi.runningtrackerapp.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.roshi.runningtrackerapp.R
import com.roshi.runningtrackerapp.db.RunData
import com.roshi.runningtrackerapp.other.Constant.ACTION_PAUSE_SERVICE
import com.roshi.runningtrackerapp.other.Constant.ACTION_START_OR_RESUME_SERVICE
import com.roshi.runningtrackerapp.other.Constant.ACTION_STOP__SERVICE
import com.roshi.runningtrackerapp.other.Constant.MAP_ZOOM
import com.roshi.runningtrackerapp.other.Constant.POLY_LINE_COLOR
import com.roshi.runningtrackerapp.other.Constant.POLY_LINE_WIDTH
import com.roshi.runningtrackerapp.other.TrackingUtility
import com.roshi.runningtrackerapp.service.PolyLine
import com.roshi.runningtrackerapp.service.TrackingService
import com.roshi.runningtrackerapp.ui.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_tracking.*
import java.util.*
import javax.inject.Inject
import kotlin.math.round

@AndroidEntryPoint
class TrackingFragment : Fragment(R.layout.fragment_tracking) {
    private val viewModel: MainViewModel by viewModels()
    private var isTracking: Boolean = false
    private var pathPoints = mutableListOf<PolyLine>()
    private var map: GoogleMap? = null
    private var currentTimeInmillis = 0L
    private var menu: Menu? = null
    @set:Inject
    var weight = 60f

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        return super.onCreateView(inflater, container, savedInstanceState)

    }

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
        btnFinishRun.setOnClickListener {
            zoomToSeeWholeTrack()
            endRunAndSaveToDb()
        }
        subscribeToObserver()

    }


    private fun sendCommandToService(action: String) =
        Intent(requireContext(), TrackingService::class.java).also {
            it.action = action
            requireContext().startService(it)
        }

    private fun addLatestPolyLines() {
        if (pathPoints.isNotEmpty() && pathPoints.last().size > 1) {
            val preLastLatLng = pathPoints.last()[pathPoints.last().size - 2]
            val lastLatLng = pathPoints.last().last()
            val ployLinesOptions = PolylineOptions()
                .color(POLY_LINE_COLOR)
                .width(POLY_LINE_WIDTH)
                .add(preLastLatLng)
                .add(lastLatLng)
            map?.addPolyline(ployLinesOptions)
        }
    }

    private fun moveCameraToUser() {
        if (pathPoints.isNotEmpty() && pathPoints.last().isNotEmpty()) {
            map?.animateCamera(
                CameraUpdateFactory.newLatLngZoom(
                    pathPoints.last().last(),
                    MAP_ZOOM
                )
            )
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.toolbar_tracking_menu, menu)
        this.menu = menu
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        if (currentTimeInmillis > 0L) {
            this.menu?.getItem(0)?.isVisible = true
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.miCancelTracking -> {
                showCancelTrackingDialog()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun zoomToSeeWholeTrack() {
        val bound = LatLngBounds.builder()
        for (polyline in pathPoints) {
            for (pos in polyline) {
                bound.include(pos)
            }
        }
        map?.moveCamera(
            CameraUpdateFactory.newLatLngBounds(
                bound.build(),
                mapView.width,
                mapView.height,
                (mapView.height * 0.05f).toInt()
            )
        )
    }

    private fun endRunAndSaveToDb() {
        map?.snapshot { bmp ->
            var distanceInMeters = 0
            for (polyline in pathPoints) {
                distanceInMeters += TrackingUtility.calculatePolyLine(polyline).toInt()
            }
            val avgSpeed =
                round((distanceInMeters / 1000f) / (currentTimeInmillis / 1000f / 60 / 60) * 10) / 10f
            val dateTimeStamp = Calendar.getInstance().timeInMillis
            val caloriesBurned = ((distanceInMeters / 1000f) * weight).toInt()
            val run = RunData(
                bmp,
                timeStamp = dateTimeStamp,
                averageSpeed = avgSpeed,
                distanceInMeters = distanceInMeters,
                currentTimeInmillis,
                caloriesBurned = caloriesBurned
            )
            viewModel.insertRun(run)
            Snackbar.make(
                requireActivity().findViewById(R.id.rootView),
                getString(R.string.run_data_save),
                Snackbar.LENGTH_LONG
            ).show()
            stopRun()
        }
    }


    private fun showCancelTrackingDialog() {
        val dialog = MaterialAlertDialogBuilder(requireContext(), R.style.AlertDialogTheme)
            .setTitle(getString(R.string.cancel_run))
            .setMessage(getString(R.string.are_you_sure))
            .setIcon(R.drawable.ic_delete)
            .setPositiveButton(getString(R.string.yes)) { _, _ ->
                stopRun()
            }.setNegativeButton(getString(R.string.no)) { dialogInterFace, _ ->
                dialogInterFace.cancel()
            }
            .create()
        dialog.show()
    }

    private fun stopRun() {
        sendCommandToService(ACTION_STOP__SERVICE)
        findNavController().navigate(R.id.action_trackingFragment_to_runFragment)
    }

    private fun updateTracking(isTracking: Boolean) {
        this.isTracking = isTracking
        if (!isTracking) {
            btnToggleRun.text = getString(R.string.start)
            btnFinishRun.visibility = View.VISIBLE
        } else {
            btnToggleRun.text = getString(R.string.stop)
            menu?.getItem(0)?.isVisible = true
            btnFinishRun.visibility = View.GONE

        }
    }


    private fun toggleRun() {
        if (isTracking) {
            menu?.getItem(0)?.isVisible = true
            sendCommandToService(ACTION_PAUSE_SERVICE)
        } else {
            sendCommandToService(ACTION_START_OR_RESUME_SERVICE)
        }
    }

    private fun subscribeToObserver() {
        TrackingService.isTracking.observe(viewLifecycleOwner, {
            updateTracking(it)
        })

        TrackingService.pathPoints.observe(viewLifecycleOwner, {
            pathPoints = it
            addLatestPolyLines()
            moveCameraToUser()

        })

        TrackingService.timeRunInMillis.observe(viewLifecycleOwner, {
            currentTimeInmillis = it
            val formattedTimes =
                TrackingUtility.getFormattedStopWatchTime(currentTimeInmillis, false)
            tvTimer.text = formattedTimes
        })
    }

    private fun addAllPolyLines() {
        for (polyline in pathPoints) {
            val polyLineOptions =
                PolylineOptions().color(POLY_LINE_COLOR).width(POLY_LINE_WIDTH).addAll(polyline)
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