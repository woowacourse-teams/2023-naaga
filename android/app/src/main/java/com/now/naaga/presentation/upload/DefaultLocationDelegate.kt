package com.now.naaga.presentation.upload

import android.content.Context
import android.location.Location
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.CancellationToken
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.gms.tasks.OnTokenCanceledListener
import com.now.domain.model.Coordinate

class DefaultLocationDelegate : LocationDelegate {
    override fun setCoordinateListener(context: Context, getLocation: (location: Location) -> Unit) {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
        fusedLocationClient.getCurrentLocation(PRIORITY_HIGH_ACCURACY, createCancellationToken())
            .addOnSuccessListener { location -> getLocation(location) }
            .addOnFailureListener { }
    }

    private fun createCancellationToken(): CancellationToken {
        return object : CancellationToken() {
            override fun onCanceledRequested(p0: OnTokenCanceledListener) = CancellationTokenSource().token
            override fun isCancellationRequested() = false
        }
    }

    override fun Location.toCoordinate(): Coordinate {
        val latitude = roundToFourDecimalPlaces(latitude)
        val longitude = roundToFourDecimalPlaces(longitude)
        return Coordinate(latitude, longitude)
    }

    private fun roundToFourDecimalPlaces(number: Double): Double {
        return (number * 10_000).toLong().toDouble() / 10_000
    }

    companion object {
        private const val PRIORITY_HIGH_ACCURACY = 100
    }
}
