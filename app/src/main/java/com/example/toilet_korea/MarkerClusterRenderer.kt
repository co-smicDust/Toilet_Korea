package com.example.toilet_korea

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import androidx.core.content.ContextCompat.getDrawable
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.clustering.view.DefaultClusterRenderer


class MarkerClusterRenderer(
    context: Context?,
    map: GoogleMap,
    clusterManager: ClusterManager<MyItem?>?
) :
    DefaultClusterRenderer<MyItem>(context, map, clusterManager) {

    val c = context

    override fun onBeforeClusterItemRendered(
        item: MyItem,
        markerOptions: MarkerOptions
    ) {
        markerOptions.title(item.title)
        markerOptions.draggable(true)
        super.onBeforeClusterItemRendered(item, markerOptions)
    }

    override fun onClusterItemRendered(item: MyItem, marker: Marker) {
        marker.tag = item.getTag()
        super.onClusterItemRendered(item, marker)
    }
}