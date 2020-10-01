package com.roshi.runningtrackerapp.service

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_LOW
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.Build
import android.os.Looper
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY
import com.google.android.gms.location.LocationResult
import com.google.android.gms.maps.model.LatLng
import com.roshi.runningtrackerapp.R
import com.roshi.runningtrackerapp.other.Constant.ACTION_PAUSE_SERVICE
import com.roshi.runningtrackerapp.other.Constant.ACTION_SHOW_TRACKING_FRAGMENT
import com.roshi.runningtrackerapp.other.Constant.ACTION_START_OR_RESUME_SERVICE
import com.roshi.runningtrackerapp.other.Constant.ACTION_STOP__SERVICE
import com.roshi.runningtrackerapp.other.Constant.FASTEST_LOCATION_UPDATE_INTERVAL
import com.roshi.runningtrackerapp.other.Constant.LOCATION_UPDATE_INTERVAL
import com.roshi.runningtrackerapp.other.Constant.NOTIFICATION_CHANNEL_ID
import com.roshi.runningtrackerapp.other.Constant.NOTIFICATION_CHANNEL_NAME
import com.roshi.runningtrackerapp.other.Constant.NOTIFICATION_ID
import com.roshi.runningtrackerapp.other.TrackingUtility
import com.roshi.runningtrackerapp.ui.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

typealias PolyLine = MutableList<LatLng>
typealias PolyLines = MutableList<PolyLine>
@AndroidEntryPoint
class TrackingService : LifecycleService() {
    var isFirstRun: Boolean = true
    @Inject
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    private var timeRunInSeconds = MutableLiveData<Long>()
    @Inject
    lateinit var baseNotificationBuilder:NotificationCompat.Builder

    companion object {
        val timeRunInMillis = MutableLiveData<Long>()
        val isTracking = MutableLiveData<Boolean>()
        val pathPoints = MutableLiveData<PolyLines>()
    }

    override fun onCreate() {
        super.onCreate()
        postInitialValues()
        fusedLocationProviderClient = FusedLocationProviderClient(this)
        setAllObservers()
    }

    private fun setAllObservers() {
        isTracking.observe(this, {
            updateLocationTracking(it)
        })
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            when (it.action) {
                ACTION_START_OR_RESUME_SERVICE -> {
                    if (isFirstRun) {
                        startForGroundService()
                        isFirstRun = false
                    } else {
                        Timber.d("Resuming service")
                        startTimer()

                    }
                }
                ACTION_PAUSE_SERVICE -> {
                    pauseService()
                    Timber.d("Pause service")

                }
                ACTION_STOP__SERVICE -> {
                    Timber.d("Stop  service")

                }

            }
        }
        return super.onStartCommand(intent, flags, startId)

    }

    private fun postInitialValues() {
        isTracking.postValue(false)
        pathPoints.postValue(mutableListOf())
        timeRunInSeconds.postValue(0L)
        timeRunInMillis.postValue(0L)

    }

    private fun addEmptyPolyLines() = pathPoints.value?.apply {
        add(mutableListOf())
        pathPoints.postValue(this)
    } ?: pathPoints.postValue(mutableListOf(mutableListOf()))

    private fun startForGroundService() {
        startTimer()
        isTracking.postValue(true)
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(notificationManager)
        }


        startForeground(NOTIFICATION_ID, baseNotificationBuilder.build())
    }


    private fun addPathPoints(location: Location?) {
        location?.let {
            val pos = LatLng(location.latitude, location.longitude)
            pathPoints.value?.apply {
                last().add(pos)
                pathPoints.postValue(this)
            }
        }
    }

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult?) {
            super.onLocationResult(result)
            if (isTracking.value!!) {
                result?.locations?.let { locations ->
                    for (location in locations) {
                        addPathPoints(location)
                        Timber.d("NEW LOCATION: ${location.latitude},${location.longitude}")
                    }
                }
            }
        }

    }

    private var isTimerEnabled = false

    private var lapTime = 0L
    private var timeRun = 0L
    private var timeStarted = 0L
    private var lastSecondTimeStamp=0L

    private fun startTimer(){
        addEmptyPolyLines()
        isTracking.postValue(true)
        timeStarted=System.currentTimeMillis()
        isTimerEnabled=true
        CoroutineScope(Dispatchers.Main).launch {
            while (isTracking.value!!){
                //this is the time difference between time started and now
                lapTime=System.currentTimeMillis()-timeStarted
                //Post the new lapTime
                timeRunInMillis.postValue(timeRun+lapTime)
                if (timeRunInMillis.value!!>=lastSecondTimeStamp+1000L){
                    timeRunInSeconds.postValue(timeRunInSeconds.value!!+1)
                    lastSecondTimeStamp+=1000L
                }

                delay(50L)

            }

            timeRun+=lapTime
        }
    }

    private fun pauseService() {
        isTracking.postValue(false)
        isTimerEnabled=false

    }

    @SuppressLint("MissingPermission")
    private fun updateLocationTracking(isTracking: Boolean) {
        if (isTracking) {
            if (TrackingUtility.hasLocationPermission(this)) {
                val request = LocationRequest().apply {
                    interval = LOCATION_UPDATE_INTERVAL
                    fastestInterval = FASTEST_LOCATION_UPDATE_INTERVAL
                    priority = PRIORITY_HIGH_ACCURACY
                }
                fusedLocationProviderClient.requestLocationUpdates(
                    request,
                    locationCallback,
                    Looper.getMainLooper()
                )
            } else {
                fusedLocationProviderClient.removeLocationUpdates(locationCallback)
            }
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(notificationManager: NotificationManager) {
        val channel =
            NotificationChannel(NOTIFICATION_CHANNEL_ID, NOTIFICATION_CHANNEL_NAME, IMPORTANCE_LOW)
        notificationManager.createNotificationChannel(channel)
    }

}