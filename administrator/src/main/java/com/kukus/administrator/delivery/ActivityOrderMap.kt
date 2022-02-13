package com.kukus.administrator.delivery

import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import com.kukus.administrator.R
import com.kukus.library.Constant.Companion.bitmapDescriptorFromVector
import com.sdoward.rxgooglemap.MapObservableProvider
import kotlinx.android.synthetic.main.activity_order_map.*


class ActivityOrderMap : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var customerLocation: LatLng

    companion object {

        lateinit var mapObservableProvider: MapObservableProvider

    }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_order_map)

        customerLocation = LatLng(intent.getStringExtra("lat").toDouble(), intent.getStringExtra("lng").toDouble())

        initLocation()


    }

    private fun initLocation() {

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        mapObservableProvider = MapObservableProvider(mapFragment)

    }

    override fun onMapReady(googleMap: GoogleMap) {

        mapObservableProvider.mapReadyObservable

                .subscribe { map ->

                    map.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style))
                    map.uiSettings.isZoomControlsEnabled = true

                    progress.visibility = View.GONE

                    if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION), 1)
                        return@subscribe
                    }

                    map.isMyLocationEnabled = true
                    map.addMarker(MarkerOptions()
                            .position(customerLocation)
                            .title("order location")
                            .icon(bitmapDescriptorFromVector(this@ActivityOrderMap, R.drawable.ic_pin)))

                    map.moveCamera(CameraUpdateFactory.newLatLng(customerLocation))
                    map.animateCamera(CameraUpdateFactory.newLatLngZoom(customerLocation, 17f))

                }
    }

}