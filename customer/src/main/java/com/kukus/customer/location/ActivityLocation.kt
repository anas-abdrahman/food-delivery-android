package com.kukus.customer.location

import android.app.Activity
import android.app.FragmentTransaction
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.MapFragment
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.kukus.customer.R
import com.kukus.customer.dialog.DialogAddress
import com.kukus.library.Constant
import com.kukus.library.Constant.Companion.GOOGLE_API_KEY
import com.kukus.library.Constant.Companion.LOCATION_PERMISSION_REQUEST_CODE
import com.kukus.library.remote.DirectionJSONInfo
import com.kukus.library.remote.IGeoCoordinates
import com.sdoward.rxgooglemap.MapObservableProvider
import kotlinx.android.synthetic.main.activity_location.*
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import rx.subscriptions.Subscriptions
import java.lang.StringBuilder

class ActivityLocation : AppCompatActivity(), DialogAddress.AddressDialogListener {

    private var deliveryCost = 5f
    private var marker: Marker? = null
    private var saveLocation: LatLng? = null
    private var isFirst = false

    private var address = ""
    private lateinit var snackbar: Snackbar
    private lateinit var manager: LocationManager
    private lateinit var lastLocation: Location
    private lateinit var locationCallback: LocationCallback
    private lateinit var locationRequest: LocationRequest
    private lateinit var mServices: IGeoCoordinates
    private var currentLatLng: LatLng? = null
    private val kedai_kukus = LatLng(30.042604, 31.336230)

    lateinit var mapFragment : SupportMapFragment

    private var mFusedLocationClient: FusedLocationProviderClient? = null

    lateinit var mapObservableProvider: MapObservableProvider

    var subscriptions = Subscriptions.from()!!

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_location)

        btn_save.startAnimation()

        mylocation.setOnClickListener {

            mapObservableProvider.mapReadyObservable

                    .subscribe { map ->

                        map.uiSettings.isZoomControlsEnabled = true

                        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION), 1)
                            return@subscribe
                        }

                        map.moveCamera(CameraUpdateFactory.newLatLng(kedai_kukus))
                        map.animateCamera(CameraUpdateFactory.newLatLngZoom(kedai_kukus, 17f))

                        btn_mylocation.visibility = View.VISIBLE

                        currentLatLng = kedai_kukus
                        marker = map.addMarker(MarkerOptions().position(currentLatLng!!).title("your location"))

                        map.moveCamera(CameraUpdateFactory.newLatLng(currentLatLng))
                        map.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 17f))

                    }

        }

        initLocation()

    }

    override fun onStart() {

        super.onStart()

        manager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {

            AlertDialog.Builder(this).setMessage("Your Locations Settings is set to 'OFF', Please Enable Location to use this app")
                    .setPositiveButton("Turn On") { _, _ ->

                        val myIntent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                        startActivity(myIntent)

                    }.setNegativeButton("Cancel") { _, _ ->

                        showSnackbar()

                    }.show()

        }

    }

    private fun initLocation() {

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
            return
        }

        buildLocationRequest()
        buildLocationCallBack()

        snackbar = Snackbar.make(btn_save, "Please Enable Location to use this app", Snackbar.LENGTH_INDEFINITE)
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        mServices = Constant.getGeoCodeService()

        mapFragment= supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment

        mapFragment.getMapAsync {

            it.uiSettings.isScrollGesturesEnabled =false

            subscriptions.add(

                    mapObservableProvider.mapReadyObservable.subscribe { map ->


                        map.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style))

                        btn_mylocation.setOnClickListener {

                            if (marker != null && currentLatLng != null) {

                                btn_mylocation.visibility = View.GONE
                                map.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 17f))

                            }
                        }

                        initGoogleMap()

                    }

            )
        }

        mapObservableProvider = MapObservableProvider(mapFragment)

        mFusedLocationClient!!.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper())

    }

    private fun buildLocationCallBack() {

        locationCallback = object : LocationCallback() {

            override fun onLocationResult(locationResult: LocationResult) {

                if (marker == null) {
                    subscriptions.add(
                            mapObservableProvider.mapReadyObservable
                                    .subscribe { map ->

                                        btn_mylocation.visibility = View.VISIBLE

                                        lastLocation = locationResult.lastLocation
                                        currentLatLng = LatLng(lastLocation.latitude, lastLocation.longitude)
                                        marker = map.addMarker(MarkerOptions().position(currentLatLng!!).title("your location"))

                                        map.moveCamera(CameraUpdateFactory.newLatLng(currentLatLng))
                                        map.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 17f))

                                    }
                    )
                }
            }
        }
    }

    private fun buildLocationRequest() {
        locationRequest = LocationRequest()
        locationRequest.priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
        locationRequest.smallestDisplacement = 10f
        locationRequest.interval = 5000
        locationRequest.fastestInterval = 3000
    }

    private fun initGoogleMap() {

        subscriptions.add(
                mapObservableProvider.cameraMoveObservable.subscribe { _ ->
                    mapObservableProvider.mapReadyObservable.subscribe {

                        if (marker != null && isFirst) {
                            saveLocation = it.cameraPosition.target
                            marker!!.position = saveLocation
                        }


                        if (it.cameraPosition.zoom > 15f) {
                            isFirst = true
                        }
                    }
                }
        )

        subscriptions.add(
                mapObservableProvider.cameraIdleObservable.subscribe { map ->

                    if (saveLocation != null && isFirst && currentLatLng != null) {

                        layout_info.visibility = View.VISIBLE

                        mapFragment.getMapAsync {

                            it.uiSettings.isScrollGesturesEnabled = true

                        }

                        if (currentLatLng!!.latitude.toFloat() == saveLocation!!.latitude.toFloat()) {

                            btn_mylocation.visibility = View.GONE

                        } else {

                            btn_mylocation.visibility = View.VISIBLE

                        }

                        mServices.getDirections("${kedai_kukus.latitude}, " + "${kedai_kukus.longitude}", "${saveLocation?.latitude}, " + "${saveLocation?.longitude}", GOOGLE_API_KEY)

                                .enqueue(object : Callback<String> {

                                    override fun onResponse(call: Call<String>?, response: Response<String>?) {

                                        val info = DirectionJSONInfo().info(JSONObject(response?.body().toString()))

                                        val durationAll = info.distanceAll.split(" ")

                                        if (durationAll.size == 2) {

                                            val distance = durationAll[0].toFloat()

                                            deliveryCost = when {
                                                distance < 2.5  -> 5f
                                                distance < 5    -> 8f
                                                distance < 7    -> 10f
                                                distance < 10   -> 12f
                                                else            -> 0f
                                            }

                                            if(deliveryCost > 0) {
                                                delivery_cost.text = StringBuilder("Delivery Cost : LE $deliveryCost")
                                                btn_save.setBackgroundColor(Color.parseColor("#F44336"))
                                                btn_save.isEnabled = true
                                            }else{
                                                delivery_cost.text = StringBuilder("Sorry! You location not available")
                                                btn_save.setBackgroundColor(Color.parseColor("#AAAAAA"))
                                                btn_save.isEnabled = false
                                            }

                                        }

                                        btn_save.revertAnimation()

                                    }

                                    override fun onFailure(call: Call<String>?, t: Throwable?) {

                                        deliveryCost = 5f

                                        delivery_cost.text = StringBuilder("Delivery Cost : LE $deliveryCost")
                                        btn_save.setBackgroundColor(Color.parseColor("#4267b2"))
                                        btn_save.isEnabled = true

                                        btn_save.revertAnimation()

                                    }

                                })

                        mServices.getGeoCode("${saveLocation!!.latitude}, ${saveLocation!!.longitude}", GOOGLE_API_KEY).enqueue(object : Callback<String> {

                            override fun onResponse(call: Call<String>?, response: Response<String>?) {

                                val body = response?.body().toString()

                                if(body != "") {

                                    val jsonObject = JSONObject(body)

                                    address = (jsonObject.get("results") as JSONArray).getJSONObject(0).get("formatted_address").toString()

                                    txt_address.text = address
                                }
                            }

                            override fun onFailure(call: Call<String>?, t: Throwable?) {}

                        })


                    }
                }
        )

        btn_save.setOnClickListener {

            val fragmentManager = supportFragmentManager
            val newFragment = DialogAddress()

            val transaction = fragmentManager?.beginTransaction()

            if (transaction != null) {
                transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                transaction.add(android.R.id.content, newFragment).addToBackStack(null).commit()
            }

        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {

        when (requestCode) {

            LOCATION_PERMISSION_REQUEST_CODE -> {

                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    initLocation()

                } else {

                    ActivityCompat.requestPermissions(this, arrayOf(
                            android.Manifest.permission.ACCESS_FINE_LOCATION,
                            android.Manifest.permission.ACCESS_COARSE_LOCATION),
                            LOCATION_PERMISSION_REQUEST_CODE)

                }
            }
        }

    }

    override fun onDataSend(builder: String, floor: String, apertment: String) {

        intent.putExtra("lat", saveLocation!!.latitude.toString())
        intent.putExtra("lng", saveLocation!!.longitude.toString())
        intent.putExtra("street", address)
        intent.putExtra("delivery", deliveryCost)

        intent.putExtra("builder", builder)
        intent.putExtra("floor", floor)
        intent.putExtra("apertment", apertment)

        setResult(Activity.RESULT_OK, intent)

        finish()

    }

    private fun showSnackbar() {

        snackbar.setAction("Turn ON") {
            val myIntent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            startActivity(myIntent)
        }

        snackbar.show()

    }

    override fun onDestroy() {
        subscriptions.unsubscribe()
        super.onDestroy()
    }

}