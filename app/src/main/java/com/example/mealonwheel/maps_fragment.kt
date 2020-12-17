package com.example.mealonwheel

import androidx.fragment.app.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.mealonwheel.places_fragment.Companion.latitude
import com.example.mealonwheel.places_fragment.Companion.longitude
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions


class maps_fragment : Fragment() {
    private val callback = OnMapReadyCallback { googleMap ->



        val myLocation = LatLng(latitude, longitude)
         googleMap.addMarker(MarkerOptions().position(myLocation).title("I'm here"))
             googleMap.moveCamera(CameraUpdateFactory.newLatLng(myLocation))
             googleMap.animateCamera( CameraUpdateFactory.zoomTo( 17.0f ) )
               googleMap.cameraPosition.target
              googleMap.projection.visibleRegion.latLngBounds.center

    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_maps_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
    }
}