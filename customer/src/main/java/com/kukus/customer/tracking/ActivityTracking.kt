package com.kukus.customer.tracking

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.ActivityCompat
import android.support.v4.app.FragmentTransaction
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.androidhuman.rxfirebase2.database.data
import com.androidhuman.rxfirebase2.database.dataChanges
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.firebase.database.DatabaseReference
import com.kukus.customer.R
import com.kukus.customer.halper.remote.DirectionJSONParser
import com.kukus.library.Constant
import com.kukus.library.Constant.Companion.GOOGLE_API_KEY
import com.kukus.library.Constant.Companion.bitmapDescriptorFromVector
import com.kukus.library.FirebaseUtils.Companion.getShip
import com.kukus.library.FirebaseUtils.Companion.getStaff
import com.kukus.library.github.mapHelper.MarkerAnimation
import com.kukus.library.model.Ship
import com.kukus.library.model.Staff
import com.kukus.library.remote.DirectionJSONInfo
import com.kukus.library.remote.IGeoCoordinates
import com.sdoward.rxgooglemap.MapObservableProvider
import kotlinx.android.synthetic.main.activity_tracking.*
import org.jetbrains.anko.toast
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import rx.Subscription
import rx.subscriptions.Subscriptions

class ActivityTracking : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var currentLocation: LatLng
    private var subscriptions = Subscriptions.from()!!
    private var markers: Marker? = null
    private lateinit var mServices: IGeoCoordinates

    var deliveryId = ""
    var id = ""
    var date = ""

    companion object {

        lateinit var mapObservableProvider: MapObservableProvider
        private var polySubscriptions: Subscription? = null
        private var polyline: Polyline? = null

    }

    lateinit var deliveryRef: DatabaseReference
    @SuppressLint("CheckResult")
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_tracking)

        currentLocation     = LatLng(intent.getStringExtra("lat").toDouble(), intent.getStringExtra("lng").toDouble())
        deliveryId          = intent.getStringExtra("deliveryId")
        id                  = intent.getStringExtra("id")
        date                = intent.getStringExtra("date")

        toast(id)

        if (intent.getBooleanExtra("isMessage", false)) {
            dialogMessage()
        }

        deliveryRef = getStaff(deliveryId)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment

        (supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment).getMapAsync(this)

        mapObservableProvider = MapObservableProvider(mapFragment)

        mServices = Constant.getGeoCodeService()


        imageView6.setOnClickListener {
            dialogMessage()
        }

        getShip(date).child(id).dataChanges().subscribe({

            if (it.exists()) {
                val ship = it.getValue(Ship::class.java)

                if (ship != null) {

                    if (ship.status == Constant.Companion.STATUS.COMPLETE) {

                        layout_delivered.visibility = View.VISIBLE
                        Handler().postDelayed({ finish() }, 5000)

                    } else {
                        layout_delivered.visibility = View.GONE
                    }

                }
            }
        }) {

        }

        imageView5.setOnClickListener {
            val callIntent = Intent(Intent.ACTION_CALL)
            callIntent.data = Uri.parse("tel:+201009453970")


            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CALL_PHONE), 1)
                return@setOnClickListener
            }
            startActivity(callIntent)
        }


    }


    private fun dialogMessage() {

        val fragmentManager = supportFragmentManager
        val newFragment = DialogMessage()
        val bundle = Bundle()
        bundle.putString("id", id)

        newFragment.arguments = bundle

        if (newFragment.dialog != null) newFragment.dialog.setCanceledOnTouchOutside(true);
        val transaction = fragmentManager?.beginTransaction()

        if (transaction != null) {
            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            transaction.add(android.R.id.content, newFragment).addToBackStack(null).commit()
        }
    }

    @SuppressLint("CheckResult")
    override fun onMapReady(googleMap: GoogleMap) {

        subscriptions.add(
                mapObservableProvider.mapReadyObservable
                        .subscribe {

                            it.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style))
                            it.uiSettings.isZoomControlsEnabled = true

                            it.addMarker(MarkerOptions()
                                    .position(currentLocation)
                                    .title("order location")
                                    .icon(bitmapDescriptorFromVector(this@ActivityTracking, R.drawable.ic_pin)))
                        }

        )

        deliveryRef.child("location").dataChanges().subscribe({

            if (it.exists()) {

                var lat = ""
                var lng = ""
                var bearing = ""

                for (d in it.children) {
                    if (d.key == "latitude") lat = d.value.toString()
                    if (d.key == "longitude") lng = d.value.toString()
                    if (d.key == "bearing") bearing = d.value.toString()
                }

                if (lat != "" && lng != "") {
                    if (isFirst) {

                        updateLocation(lat.toDouble(), lng.toDouble(), bearing.toDouble(), true, false)

                    } else {

                        updateLocation(lat.toDouble(), lng.toDouble(), bearing.toDouble())

                    }


                }

            }
        }) {}

        //updateLocation((30.039405), (31.3334358), 0.0)

    }

    var isFirst = false

    private var oldLocation: LatLng? = null
    private var newLocation: LatLng? = null

    private fun setNewLocation(lat: Double, lng: Double, locationBearing: Double, isMove: Boolean, isZoom: Boolean) {

        val currentLatLng = LatLng(lat, lng)

        if (markers != null) {
            markers!!.position = currentLatLng
        }

        mapObservableProvider.mapReadyObservable
                .subscribe {
                    if (isMove) it.moveCamera(CameraUpdateFactory.newLatLng(currentLatLng))
                    if (isZoom) {
                        it.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 17f))
                        isFirst = true
                    }
                    if (markers == null) {


                        markers = it.addMarker(MarkerOptions().position(currentLatLng).title("Delivery location").icon(bitmapDescriptorFromVector(this@ActivityTracking, R.drawable.ic_scooter_delivery)).flat(true))


                    } else {

                        MarkerAnimation.animateMarkerTo(markers, currentLatLng)

                    }

                    newLocation = currentLatLng

                    if (oldLocation != null && newLocation != null) {

                        val bearing = MarkerAnimation.bearingBetweenLocations(oldLocation!!, newLocation!!).toFloat()
                        MarkerAnimation.rotateMarker(markers, bearing)
                        it.animateCamera(CameraUpdateFactory.newCameraPosition(
                                CameraPosition.builder(it.cameraPosition).bearing(locationBearing.toFloat()).build()
                        ))

                    }

                    oldLocation = currentLatLng


                }

    }

    fun updateLocation(lat: Double, lng: Double, bearing: Double, isMove: Boolean = true, isZoom: Boolean = true) {

        setNewLocation(lat, lng, bearing, isMove, isZoom)

        mServices.getDirections(
                "$lat, $lng",
                "${currentLocation.latitude}, ${currentLocation.longitude}", GOOGLE_API_KEY)
                .enqueue(object : Callback<String> {

                    override fun onResponse(call: Call<String>?, response: Response<String>?) {

                        ParserTask().execute(response!!.body().toString())

                        val info = DirectionJSONInfo().info(JSONObject(response.body().toString()))

                        val duration = info.durationAll.split(" ")

                        if (duration.size == 2) {

                            txt_distance.text = info.distanceAll
                            txt_duration.text = StringBuilder(duration[0] + " " + duration[1])

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

                    lineOption.color(Color.RED)
                    lineOption.addAll(points)
                    lineOption.geodesic(true)
                    lineOption.width(12f)

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
        subscriptions.unsubscribe()

        super.onDestroy()
    }

}
