package com.now.naaga.presentation.upload

import android.content.Context
import android.location.Location
import com.now.domain.model.Coordinate

interface LocationDelegate {
    fun setCoordinateListener(context: Context, getLocation: (location: Location) -> Unit)
    fun Location.toCoordinate(): Coordinate
}
