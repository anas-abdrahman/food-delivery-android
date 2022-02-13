package com.kukus.administrator.delivery

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.os.Looper
import android.support.v4.app.ActivityCompat
import android.support.v4.app.FragmentTransaction
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Toast
import com.androidhuman.rxfirebase2.database.data
import com.androidhuman.rxfirebase2.database.rxUpdateChildren
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.kukus.administrator.R
import com.kukus.administrator.dialog.DialogMessageDelivery
import com.kukus.administrator.dialog.DialogPaymentSuccess
import com.kukus.library.remote.DirectionJSONInfo
import com.kukus.customer.halper.remote.DirectionJSONParser
import com.kukus.library.Constant.Companion.GOOGLE_API_KEY
import com.kukus.library.Constant.Companion.STATUS
import com.kukus.library.Constant.Companion.bitmapDescriptorFromVector
import com.kukus.library.Constant.Companion.getGeoCodeService
import com.kukus.library.FirebaseUtils.Companion.getDate
import com.kukus.library.FirebaseUtils.Companion.getShip
import com.kukus.library.FirebaseUtils.Companion.getStaff
import com.kukus.library.FirebaseUtils.Companion.getUser
import com.kukus.library.FirebaseUtils.Companion.getUserId
import com.kukus.library.github.mapHelper.MarkerAnimation
import com.kukus.library.model.User
import com.kukus.library.remote.IGeoCoordinates
import com.sdoward.rxgooglemap.MapObservableProvider
import kotlinx.android.synthetic.main.activity_delivery_tracking.*
import org.jetbrains.anko.toast
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import rx.Subscription


class ActivityTracking : AppCompatActivity() , OnMapReadyCallback {

    private var locationCallback        : LocationCallback? = null
    private lateinit var locationRequest         : LocationRequest
    private lateinit var manager                 : LocationManager
    private lateinit var customerLocation: LatLng
    private lateinit var mServices: IGeoCoordinates

    private var oldLocation: LatLng? = null
    private var newLocation: LatLng? = null

    private var mFusedLocationClient             : FusedLocationProviderClient? = null
    private var marker: Marker? = null
    private var orderID = ""
    private var date = ""
    private var userId = ""
    private var status = STATUS.WAITING

    companion object {

        lateinit var mapObservableProvider: MapObservableProvider
        private var polySubscriptions: Subscription? = null
        private var polyline: Polyline? = null

    }

    @SuppressLint("CheckResult")
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_delivery_tracking)

        orderID       = intent.getStringExtra("order")
        date          = intent.getStringExtra("date")
        userId          = intent.getStringExtra("userId")
        status          = intent.getSerializableExtra("status") as STATUS
        customerLocation = LatLng(intent.getStringExtra("lat").toDouble(), intent.getStringExtra("lng").toDouble())

        getUser(userId).data().subscribe{data->

            val user = data.getValue(User::class.java)

            if(user != null){

                btn_call.setOnClickListener {

                    val callIntent = Intent(Intent.ACTION_CALL)
                    callIntent.data = Uri.parse("tel:${user.mobile}")

                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CALL_PHONE), 1001)
                        return@setOnClickListener
                    }
                    startActivity(callIntent)

                }
            }

        }


        initMap()



    }

    private fun initMap(){

        initLocation ()
        initClickListener()

        if(status == STATUS.DISPATCHED){

            btn_navigator.visibility = View.VISIBLE
            btn_pending.visibility = View.VISIBLE
            btn_message.visibility = View.VISIBLE

            buildLocationRequest()

            buildLocationCallBack()

            mapObservableProvider.mapReadyObservable.subscribe { map ->

                if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION), 1000)
                    return@subscribe
                }

                mFusedLocationClient!!.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper())

                mFusedLocationClient!!.lastLocation.addOnSuccessListener(this) { location ->

                    if(location != null) {

                        val latLng = LatLng(location.latitude, location.longitude)

                        map.moveCamera(CameraUpdateFactory.newLatLng(latLng))
                        map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17f))
                    }
                }
            }

            linearLayout7.visibility = View.GONE
            linearLayout8.visibility = View.VISIBLE

        }


    }

    private fun initLocation (){

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        mapObservableProvider = MapObservableProvider(mapFragment)
        mFusedLocationClient    = LocationServices.getFusedLocationProviderClient(this)
        mServices               = getGeoCodeService()
        manager                 = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        registerReceiver(gpsReceiver, IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION))

    }

    private fun initClickListener() {

        val ref = getShip(getDate).child(orderID)

        btn_accept.setOnClickListener {

            val update= java.util.HashMap<String, Any>()
            update["status"]        = STATUS.DISPATCHED

            progress.visibility = View.VISIBLE

            ref.updateChildren(update).addOnCompleteListener {


                progress.visibility = View.GONE

                Toast.makeText(this, "Start Delivery", Toast.LENGTH_LONG).show()

                buildLocationRequest()

                buildLocationCallBack()

                mapObservableProvider.mapReadyObservable.subscribe { map ->

                    if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION), 1)
                        return@subscribe
                    }

                    mFusedLocationClient!!.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper())

                    mFusedLocationClient!!.lastLocation.addOnSuccessListener(this) { location ->

                        if(location != null) {

                            val latLng = LatLng(location.latitude, location.longitude)

                            map.moveCamera(CameraUpdateFactory.newLatLng(latLng))
                            map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17f))

                            btn_pending.visibility = View.VISIBLE
                            btn_message.visibility = View.VISIBLE
                            btn_navigator.visibility = View.VISIBLE

                        }
                    }
                }

            }

            linearLayout7.visibility = View.GONE
            linearLayout8.visibility = View.VISIBLE

        }

        btn_cancel.setOnClickListener {

            val update= java.util.HashMap<String, Any>()
            update["status"]        = STATUS.CANCEL

            ref.updateChildren(update).addOnCompleteListener {

                Toast.makeText(this, "Cancel Delivery", Toast.LENGTH_LONG).show()
                finish()

            }

            linearLayout7.visibility = View.GONE

        }

        btn_Delivered.setOnClickListener {

            val fragmentManager = supportFragmentManager
            val newFragment = DialogPaymentSuccess()

            val bundle = Bundle()

            bundle.putString("id", orderID)
            bundle.putString("date", date)
            bundle.putString("userId", userId)

            newFragment.arguments = bundle

            val transaction = fragmentManager?.beginTransaction()

            if(transaction != null) {
                transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                transaction.add(android.R.id.content, newFragment).addToBackStack(null).commit()

            }

        }

        btn_pending.setOnClickListener {

            val ships = getShip(date).child(orderID)

            val update = mapOf("status" to STATUS.PENDING)

            ships.rxUpdateChildren(update).subscribe {
                toast("pending order!")
                finish()
            }

        }

        btn_navigator.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/maps/search/?api=1&query=${customerLocation.latitude}, ${customerLocation.longitude}"))
            startActivity(intent)
        }

        btn_message.setOnClickListener {

            val fragmentManager = supportFragmentManager
            val newFragment = DialogMessageDelivery()
            val bundle = Bundle()

            bundle.putString("id", orderID)
            bundle.putString("userId", userId)

            newFragment.arguments = bundle

            if ( newFragment.dialog != null ) newFragment.dialog.setCanceledOnTouchOutside(true)
            val transaction = fragmentManager?.beginTransaction()

            if(transaction != null) {
                transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                transaction.add(android.R.id.content, newFragment).addToBackStack(null).commit()

            }

        }

    }


    private var gpsReceiver = object : BroadcastReceiver(){

        override fun onReceive(context: Context?, intent: Intent?) {

            if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {

                buildLocationCallBack()

                Toast.makeText(context, "Service ON", Toast.LENGTH_SHORT).show()

            }else{

                Toast.makeText(context, "Service OFF", Toast.LENGTH_SHORT).show()

            }
        }
    }

    private fun buildLocationRequest() {
        locationRequest                         = LocationRequest()
        locationRequest.priority                = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.smallestDisplacement    = 10f
        locationRequest.interval                = 5000
        locationRequest.fastestInterval         = 3000
    }

    private fun buildLocationCallBack(){

        locationCallback = object : LocationCallback() {

            override fun onLocationResult(locationResult: LocationResult) {

                val location = locationResult.lastLocation

                mapObservableProvider.mapReadyObservable
                        .subscribe { map ->

                            setCurrentLocation(location, map)

                        }
            }
        }
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
                .icon(bitmapDescriptorFromVector(this@ActivityTracking, R.drawable.ic_pin)))

                map.moveCamera(CameraUpdateFactory.newLatLng(customerLocation))
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(customerLocation, 17f))

            }
    }



    fun setCurrentLocation(location: Location, map: GoogleMap){

        val latLng        = LatLng(location.latitude, location.longitude)

        map.moveCamera(CameraUpdateFactory.newLatLng(latLng))

        if(marker == null) {

            marker = map.addMarker(MarkerOptions()
                    .position(latLng)
                    .title("Your location")
                    .icon(bitmapDescriptorFromVector(this@ActivityTracking, R.drawable.ic_navigation)))

        } else {

            MarkerAnimation.animateMarkerTo(marker, latLng)

        }

        newLocation = latLng

        if(oldLocation != null && newLocation != null ) {

            val bearing = MarkerAnimation.bearingBetweenLocations(oldLocation!!, newLocation!!).toFloat()
            MarkerAnimation.rotateMarker(marker, bearing)
            map.animateCamera(CameraUpdateFactory.newCameraPosition(
                    CameraPosition.builder(map.cameraPosition).bearing(location.bearing).build()
            ))

        }

        oldLocation = latLng

        getStaff(getUserId).child("location").setValue(location)

        updateLocation(latLng.latitude.toString(), latLng.longitude.toString())

    }

    private fun updateLocation(lat: String, lng: String) {

        mServices.getDirections(
                "$lat, $lng",
                "${customerLocation.latitude}, ${customerLocation.longitude}", GOOGLE_API_KEY)
                .enqueue(object : Callback<String> {

                    override fun onResponse(call: Call<String>?, response: Response<String>?) {

                        ParserTask().execute(response!!.body().toString())

                        val info = DirectionJSONInfo().info(JSONObject(response.body().toString()))

                        val duration = info.durationAll.split(" ")

                        if (duration.size == 2) {

                            txt_distance.text = info.distanceAll
                            txt_duration.text = duration[0]
                            txt_duration_unit.text = duration[1]

                        } else {

                            txt_distance.text = info.distanceAll
                            txt_duration.text = info.durationAll

                        }

                    }

                    override fun onFailure(call: Call<String>?, t: Throwable?) {}

                })
    }

    class ParserTask : AsyncTask<String, Int, List<List<HashMap<String, String>>>>() {

        override fun doInBackground(vararg params: String?): List<List<HashMap<String, String>>> {
            return DirectionJSONParser().parse(JSONObject(params[0]))
        }

        override fun onPostExecute(result: List<List<HashMap<String, String>>>?) {

            super.onPostExecute(result)

            try {

                val points = arrayListOf<LatLng>()
                val lineOption = PolylineOptions()

                for (i in 0 until result!!.size) {
                    val path = result[i]

                    for (j in 0 until path.size) {

                        val point = path[j]
                        val lat = point["lat"]!!.toDouble()
                        val lng = point["lng"]!!.toDouble()
                        val position = LatLng(lat, lng)

                        points.add(position)
                    }

                    lineOption.color(Color.parseColor("#F44336"))
                    lineOption.addAll(points)
                    lineOption.geodesic(true)
                    lineOption.width(5f)

                }

                if (polyline != null) {
                    polyline!!.remove()
                }

                polySubscriptions = mapObservableProvider.mapReadyObservable
                        .subscribe {
                            polyline = it.addPolyline(lineOption)
                        }

            } catch (e: Exception) {

                e.printStackTrace()

            }
        }
    }

    override fun onDestroy() {

        if (polySubscriptions != null) polySubscriptions!!.unsubscribe()
        if(locationCallback != null) mFusedLocationClient!!.removeLocationUpdates(locationCallback)

        unregisterReceiver(gpsReceiver)



        super.onDestroy()
    }

}