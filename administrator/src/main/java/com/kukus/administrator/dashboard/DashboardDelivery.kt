package com.kukus.administrator.dashboard

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import com.androidhuman.rxfirebase2.database.data
import com.androidhuman.rxfirebase2.database.dataChanges
import com.androidhuman.rxfirebase2.database.rxUpdateChildren
import com.kukus.administrator.R
import com.kukus.administrator.delivery.ActivityDelivery
import com.kukus.administrator.history.ActivityHistory
import com.kukus.administrator.login.ActivityLogin
import com.kukus.administrator.service.DeliveryOrderService
import com.kukus.administrator.wallet.ActivityWallet
import com.kukus.library.Constant.Companion.STATUS
import com.kukus.library.FirebaseUtils.Companion.getDate
import com.kukus.library.FirebaseUtils.Companion.getShip
import com.kukus.library.FirebaseUtils.Companion.getStaff
import com.kukus.library.FirebaseUtils.Companion.getUserAuth
import com.kukus.library.FirebaseUtils.Companion.getUserId
import com.kukus.library.model.Ship
import com.kukus.library.model.Staff
import kotlinx.android.synthetic.main.dashboard_deliver.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.toast

class DashboardDelivery : ActivityDashboard() {

    var isOnline = false

    lateinit var service : Intent

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        service = Intent(this, DeliveryOrderService::class.java)
        service.putExtra("service_delivery", "service.DeliveryOrderService")

        setContentView(R.layout.dashboard_deliver)

        onClickListener()

        onDataListener()

    }


    override fun onDestroy() {
        super.onDestroy()
        stopService(service)
    }

    private fun onClickListener() {

        work.setOnCheckedChangeListener { compoundButton, b ->

            val update = mapOf("online" to compoundButton.isChecked)

            getStaff(getUserId).rxUpdateChildren(update).subscribe {

                if (compoundButton.isChecked) {

                    toast("Online")
                    startService(service)

                } else {

                    toast("Offline")
                    stopService(service)
                }

                onDataListener()
            }

        }

        btn_delivery.setOnClickListener {

            val intentOrder = Intent(this, ActivityDelivery::class.java)

            intentOrder.putExtra("online", isOnline)

            startActivity(intentOrder)
        }

        btn_wallet.setOnClickListener {
            startActivity(Intent(this, ActivityWallet::class.java))
        }

        btn_history.setOnClickListener {
            startActivity(Intent(this, ActivityHistory::class.java))
        }

        btn_logout.setOnClickListener {

            startActivity(Intent(this, ActivityLogin::class.java))

            mSharedPreferences.edit().putString("user_rule", "").apply()

            getUserAuth.signOut()

            stopService(service)

            finish()

        }
    }

    private fun onDataListener() {

        doAsync {

            getStaff(getUserId).data().subscribe {data ->

                if (data.exists()) {

                    val user = data.getValue(Staff::class.java)

                    if (user != null) {

                        isOnline = user.online

                        work.isChecked = isOnline

                        if (work.isChecked) {

                            toast("Online")
                            startService(service)

                        } else {

                            toast("Offline")
                            stopService(service)
                        }

                    }
                }
            }

            getShip(getDate).dataChanges().subscribe { data ->

                progressbar.visibility = View.VISIBLE

                var earn = 0f

                var countNewOrder = 0
                var countPlacedOrder = 0
                var countDeliveredOrder = 0

                data.children.forEach { mData ->

                    val value = mData.getValue(Ship::class.java)

                    if (value != null) {

                        if (value.status == STATUS.PENDING && value.deliveryId == "" && isOnline) {
                            countNewOrder += 1
                        }

                        if (value.status == STATUS.PENDING && value.deliveryId == getUserId) {
                            countPlacedOrder += 1
                        }

                        if (value.status == STATUS.DISPATCHED && value.deliveryId == getUserId) {
                            countPlacedOrder += 1
                        }

                        if (value.status == STATUS.COMPLETE && value.deliveryId == getUserId) {
                            countDeliveredOrder += 1
                            earn += value.delivery
                        }

                    }

                    txt_order_new.text = StringBuilder("$countNewOrder new")
                    txt_order_pending.text = StringBuilder("$countPlacedOrder pending")
                    txt_order_past.text = StringBuilder("$countDeliveredOrder past")

                    wallet.text = ("Earn : LE $earn")
                    trip.text = ("Trip : $countDeliveredOrder")

                }

                Handler().postDelayed({ progressbar.visibility = View.GONE }, 1000)

            }
        }
    }
}
