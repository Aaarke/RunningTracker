package com.roshi.runningtrackerapp.other

import android.Manifest
import android.content.Context
import android.location.Location
import android.os.Build
import com.roshi.runningtrackerapp.service.PolyLine
import pub.devrel.easypermissions.EasyPermissions
import java.util.concurrent.TimeUnit

object TrackingUtility {
    fun hasLocationPermission(context: Context) =
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            EasyPermissions.hasPermissions(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        } else {
            EasyPermissions.hasPermissions(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            )
        }

    fun getFormattedStopWatchTime(ms: Long, includeMillis: Boolean): String {
        var miliseconds = ms
        var hours = TimeUnit.MILLISECONDS.toHours(miliseconds)
        miliseconds -= TimeUnit.HOURS.toMillis(hours)
        val miniutes = TimeUnit.MILLISECONDS.toMinutes(miliseconds)
        miliseconds -= TimeUnit.MINUTES.toMillis(miniutes)
        val seconds = TimeUnit.MILLISECONDS.toSeconds(miliseconds)
        if (!includeMillis) {
            return "${if (hours < 10) "0" else ""}$hours:" + "${if (miniutes < 10) "0" else ""}$miniutes" + "${if (seconds < 10) "0" else ""}$seconds"
        }
        miliseconds -= TimeUnit.SECONDS.toMillis(seconds)
        miliseconds /= 10

        return "${if (hours < 10) "0" else ""}$hours:" + "${if (miniutes < 10) "0" else ""}$miniutes" +
                "${if (seconds < 10) "0" else ""}$seconds:" +
                "${if (miliseconds < 10) "0" else ""}$miliseconds"


    }

    fun calculatePolyLine(polyLine: PolyLine): Float {
        var distance = 0F
        for (i in 0..polyLine.size - 2) {
            val pos1 = polyLine[i]
            val pos2 = polyLine[i + 1]
            var result = FloatArray(1)
            Location.distanceBetween(
                pos1.latitude,
                pos1.longitude,
                pos2.latitude,
                pos2.longitude,
                result
            )
            distance += result[0]


        }
        return distance
    }

}