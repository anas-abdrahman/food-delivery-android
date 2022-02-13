package com.kukus.administrator.delivery

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import com.androidhuman.rxfirebase2.database.dataChanges
import com.kukus.administrator.R
import com.kukus.administrator.delivery.tab.DeliveryTab
import com.kukus.library.Constant.Companion.STATUS
import com.kukus.library.FirebaseUtils.Companion.getDate
import com.kukus.library.FirebaseUtils.Companion.getShip
import com.kukus.library.FirebaseUtils.Companion.getUserId
import com.kukus.library.model.Ship
import kotlinx.android.synthetic.main.dashboard_delivery.*


class ActivityDelivery : AppCompatActivity() {

    var date = getDate
    var isOnline = false

    private lateinit var mAdapter: DeliveryTab

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        isOnline = if(intent.extras != null) {
            intent.extras.getBoolean("online", false)
        }else{
            true
        }

        setContentView(R.layout.dashboard_delivery)

        setupAdapterTab()

        setupBadgetTab()

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION), 1000)
            return
        }

    }

    private fun setupAdapterTab() {

        mAdapter = DeliveryTab(supportFragmentManager)

        for (i in 0 until mAdapter.count) {
            mAdapter.getFragment(i).isOnline = isOnline
        }

        viewPager.offscreenPageLimit = mAdapter.count
        viewPager.adapter = mAdapter.SectionsPagerAdapter(supportFragmentManager)
        viewPager.setCurrentItem(0, false)

        tabs.setupWithViewPager(viewPager)

    }

    @SuppressLint("CheckResult")
    private fun setupBadgetTab() {

        getShip(getDate).dataChanges().subscribe { dataRef ->

            var countActiveOrder = 0
            var countUpcomingOrder = 0
            var countPastOrder = 0

            tabs.setBadgeText(0, null)
            tabs.setBadgeText(1, null)
            tabs.setBadgeText(2, null)

            dataRef.children.forEach {

                val value = it.getValue(Ship::class.java)

                if (value != null) {

                    if ((value.status == STATUS.PENDING) && value.deliveryId == "" && isOnline) {
                        countActiveOrder += 1
                    }

                    if ((value.status == STATUS.PENDING || value.status == STATUS.DISPATCHED) && value.deliveryId == getUserId) {
                        countUpcomingOrder += 1
                    }

                    if (value.status == STATUS.COMPLETE && value.deliveryId == getUserId) {
                        countPastOrder += 1

                    }

                }

            }


            if (countActiveOrder > 0) tabs.setBadgeText(0, (countActiveOrder).toString())
            if (countUpcomingOrder > 0) tabs.setBadgeText(1, (countUpcomingOrder).toString())
            if (countPastOrder > 0) tabs.setBadgeText(2, (countPastOrder).toString())


        }
    }

/*
    val c = Calendar.getInstance()
    var mYear = c.get(Calendar.YEAR)
    var mMonth = c.get(Calendar.MONTH)
    var mDay = c.get(Calendar.DAY_OF_MONTH)

    private fun getDatePicker() {


        DatePickerDialog(this, DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->

            var month = (monthOfYear + 1).toString()
            var day = (dayOfMonth).toString()

            if (month.toInt() < 10) {

                month = "0" + month
            }

            if (day.toInt() < 10) {

                day = "0" + day
            }

            date = ("$year$month$day")

            for (i in 0 until mAdapter.count) {
                mAdapter.getFragment(i).setupAdapterList(date)
            }

            mYear = year
            mMonth = monthOfYear
            mDay = dayOfMonth

        }, mYear, mMonth, mDay).show()

    }*/
}
