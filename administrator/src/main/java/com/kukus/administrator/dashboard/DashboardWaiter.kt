package com.kukus.administrator.dashboard

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.androidhuman.rxfirebase2.database.dataChanges
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.kukus.administrator.R
import com.kukus.administrator.login.ActivityLogin
import com.kukus.administrator.service.DeliveryOrderService
import com.kukus.administrator.waiter.ActivityWaiter
import com.kukus.library.FirebaseUtils.Companion.getDate
import com.kukus.library.FirebaseUtils.Companion.getUserAuth
import com.kukus.library.FirebaseUtils.Companion.getWaiter
import com.kukus.library.Constant.Companion.STATUS
import com.kukus.library.model.Waiter
import kotlinx.android.synthetic.main.dashboard_waiter.*
import org.jetbrains.anko.doAsync

class DashboardWaiter : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        setContentView(R.layout.dashboard_waiter)

        startService(Intent(this, DeliveryOrderService::class.java))

        onClickListener()

        onDataListener()

    }

    override fun onDestroy() {

        stopService(Intent(this, DeliveryOrderService::class.java))
        super.onDestroy()

    }

    private fun onClickListener() {

        btn_waiter.setOnClickListener {
            startActivity(Intent(this, ActivityWaiter::class.java))
        }

        btn_logout.setOnClickListener {

            getUserAuth.signOut()

            startActivity(Intent(this, ActivityLogin::class.java))

            val mSharedPreferences = getSharedPreferences("user-preference", Context.MODE_PRIVATE)
            mSharedPreferences.edit().putString("user_rule", "").apply()

            finish()

        }
    }

    private fun onDataListener() {

        doAsync {

            getWaiter(getDate).dataChanges().subscribe { child ->

                progressbar_waiter.visibility = View.VISIBLE

                var countNewOrder = 0
                var countPendingOrder = 0
                var countCancelOrder = 0

                child.children.forEach { data ->

                    val value = data.getValue(Waiter::class.java)

                    if (value != null) {

                        if (value.status == STATUS.WAITING) {
                            countNewOrder += 1
                        }

                        if (value.status == STATUS.PENDING) {
                            countPendingOrder += 1
                        }

                        if (value.status == STATUS.CANCEL) {
                            countCancelOrder += 1
                        }

                    }

                    txt_order_new_waiter.text = StringBuilder("$countNewOrder waiting")
                    txt_order_pending_waiter.text = StringBuilder("$countPendingOrder place")

                }

                Handler().postDelayed({ progressbar_waiter.visibility = View.GONE }, 1000)


            }
        }
    }
}
