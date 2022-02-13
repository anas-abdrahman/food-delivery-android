package com.kukus.administrator.dashboard

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.androidhuman.rxfirebase2.database.dataChanges
import com.kukus.administrator.R
import com.kukus.administrator.login.ActivityLogin
import com.kukus.administrator.menu.ActivityMenu
import com.kukus.administrator.order.ActivityOrders
import com.kukus.administrator.orderWaiter.ActivityOrdersWaiter
import com.kukus.administrator.setting.ActivitySetting
import com.kukus.library.Constant.Companion.STATUS
import com.kukus.library.FirebaseUtils.Companion.getDate
import com.kukus.library.FirebaseUtils.Companion.getShip
import com.kukus.library.FirebaseUtils.Companion.getUserAuth
import com.kukus.library.FirebaseUtils.Companion.getWaiter
import com.kukus.library.model.Ship
import com.kukus.library.model.Waiter
import kotlinx.android.synthetic.main.dashboard_cashier.*
import kotlinx.android.synthetic.main.dashboard_cashier.btn_logout
import kotlinx.android.synthetic.main.dashboard_cashier.btn_order_waiter
import kotlinx.android.synthetic.main.dashboard_cashier.button_order
import kotlinx.android.synthetic.main.dashboard_cashier.progressbar
import kotlinx.android.synthetic.main.dashboard_cashier.progressbar_waiter
import kotlinx.android.synthetic.main.dashboard_cashier.txt_order_complete
import kotlinx.android.synthetic.main.dashboard_cashier.txt_order_new
import kotlinx.android.synthetic.main.dashboard_cashier.txt_order_new_waiter
import kotlinx.android.synthetic.main.dashboard_cashier.txt_order_pending
import kotlinx.android.synthetic.main.dashboard_cashier.txt_order_pending_waiter
import org.jetbrains.anko.doAsync

class DashboardCashier : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        setContentView(R.layout.dashboard_cashier)

        //startService(Intent(this, AdminService::class.java))

        onClickListener()

        onDataListener()

    }

    private fun onClickListener() {

        button_order.setOnClickListener {
            startActivity(Intent(this, ActivityOrders::class.java))
        }
        btn_order_waiter.setOnClickListener {
            startActivity(Intent(this, ActivityOrdersWaiter::class.java))
        }
        btn_setting.setOnClickListener {
            startActivity(Intent(this, ActivitySetting::class.java))
        }
        btn_menu.setOnClickListener {
            startActivity(Intent(this, ActivityMenu::class.java))
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

            getShip(getDate).dataChanges().subscribe { child ->

                progressbar.visibility = View.VISIBLE

                var countNewOrder = 0
                var countPendingOrder = 0
                var countCompleteOrder = 0
                var countCancelOrder = 0

                child.children.forEach { data ->

                    val value = data.getValue(Ship::class.java)

                    if (value != null) {

                        if (value.status == STATUS.WAITING) {
                            countNewOrder += 1
                        }

                        if (value.status == STATUS.PENDING) {
                            countPendingOrder += 1
                        }

                        if (value.status == STATUS.DISPATCHED) {
                            countPendingOrder += 1
                        }

                        if (value.status == STATUS.COMPLETE) {
                            countCompleteOrder += 1
                        }

                        if (value.status == STATUS.CANCEL) {
                            countCancelOrder += 1
                        }

                    }

                    txt_order_new.text = ("$countNewOrder new")
                    txt_order_pending.text = ("$countPendingOrder pending")
                    txt_order_complete.text = ("$countCompleteOrder past")

                }

                Handler().postDelayed({ progressbar.visibility = View.GONE }, 1000)

            }

            getWaiter(getDate).dataChanges().subscribe { child ->

                progressbar_waiter.visibility = View.VISIBLE

                var countNewOrder = 0
                var countPendingOrder = 0

                child.children.forEach { data ->

                    val value = data.getValue(Waiter::class.java)

                    if (value != null) {

                        if (value.status == STATUS.WAITING) {
                            countNewOrder += 1
                        }

                        if (value.status == STATUS.COMPLETE) {
                            countPendingOrder += 1
                        }


                    }

                    txt_order_new_waiter.text = StringBuilder("$countNewOrder waiting")
                    txt_order_pending_waiter.text = StringBuilder("$countPendingOrder Complete")

                }

                Handler().postDelayed({ progressbar_waiter.visibility = View.GONE }, 1000)

            }
        }
    }
}
