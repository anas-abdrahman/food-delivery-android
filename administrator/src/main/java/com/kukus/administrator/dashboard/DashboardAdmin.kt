package com.kukus.administrator.dashboard

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import com.androidhuman.rxfirebase2.database.data
import com.androidhuman.rxfirebase2.database.dataChanges
import com.androidhuman.rxfirebase2.database.rxUpdateChildren
import com.kukus.administrator.R
import com.kukus.administrator.delivery.ActivityDelivery
import com.kukus.administrator.login.ActivityLogin
import com.kukus.administrator.menu.ActivityMenu
import com.kukus.administrator.message.ActivityMessage
import com.kukus.administrator.news.ActivityNews
import com.kukus.administrator.order.ActivityOrders
import com.kukus.administrator.orderWaiter.ActivityOrdersWaiter
import com.kukus.administrator.promo.ActivityPromo
import com.kukus.administrator.service.AdminService
import com.kukus.administrator.setting.ActivitySetting
import com.kukus.administrator.staff.ActivityStaff
import com.kukus.administrator.statistic.ActivityStatistic
import com.kukus.administrator.statistic.ActivityStatisticNew
import com.kukus.administrator.user.ActivityUserList
import com.kukus.administrator.voucher.ActivityVoucher
import com.kukus.administrator.waiter.ActivityWaiter
import com.kukus.library.Constant.Companion.STATUS
import com.kukus.library.FirebaseUtils
import com.kukus.library.FirebaseUtils.Companion.getContactRef
import com.kukus.library.FirebaseUtils.Companion.getDate
import com.kukus.library.FirebaseUtils.Companion.getMenu
import com.kukus.library.FirebaseUtils.Companion.getMenuRef
import com.kukus.library.FirebaseUtils.Companion.getShip
import com.kukus.library.FirebaseUtils.Companion.getTableRef
import com.kukus.library.FirebaseUtils.Companion.getUserAuth
import com.kukus.library.FirebaseUtils.Companion.getWaiter
import com.kukus.library.model.*
import kotlinx.android.synthetic.main.dashboard_admin.*
import org.jetbrains.anko.toast

class DashboardAdmin : ActivityDashboard() {

    lateinit var service : Intent


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        service = Intent(this, AdminService::class.java)
        service.putExtra("service_admin", "service.AdminService")

        setContentView(R.layout.dashboard_admin)


        startService(service)

        onClickListener()

        onDataListener()

    }

    override fun onDestroy() {
        super.onDestroy()
        stopService(service)
    }

    private fun onClickListener() {

        button_order.setOnClickListener {
            startActivity(Intent(this, ActivityOrders::class.java))
        }
        btn_order_waiter.setOnClickListener {
            startActivity(Intent(this, ActivityOrdersWaiter::class.java))
        }
        btn_delivery.setOnClickListener {
            startActivity(Intent(this, ActivityDelivery::class.java))
        }
        btn_staff.setOnClickListener {
            startActivity(Intent(this, ActivityStaff::class.java))
        }
        btn_promo.setOnClickListener {
            startActivity(Intent(this, ActivityPromo::class.java))
        }
        btn_voucher.setOnClickListener {
            startActivity(Intent(this, ActivityVoucher::class.java))
        }
        btn_menu.setOnClickListener {
            startActivity(Intent(this, ActivityMenu::class.java))
        }
        btn_waiter.setOnClickListener {
            startActivity(Intent(this, ActivityWaiter::class.java))
        }
        btn_statistic.setOnClickListener {
            startActivity(Intent(this, ActivityStatisticNew::class.java))
        }
        btn_user.setOnClickListener {
            startActivity(Intent(this, ActivityUserList::class.java))
        }
        btn_news.setOnClickListener {
            startActivity(Intent(this, ActivityNews::class.java))
        }
        btn_setting.setOnClickListener {
            startActivity(Intent(this, ActivitySetting::class.java))
        }
        btn_message.setOnClickListener {
            startActivity(Intent(this, ActivityMessage::class.java))
        }
        btn_logout.setOnClickListener {

            startActivity(Intent(this, ActivityLogin::class.java))

            mSharedPreferences.edit().putString("user_rule", "").apply()

            getUserAuth.signOut()

            stopService(service)

            finish()

        }
        btn_reset.setOnClickListener {

            getMenuRef.data().subscribe { child->

                child.children.forEach { data ->

                    if(data.exists()){

                        val menus = data.getValue(Menu::class.java)

                        if(menus != null)
                        {

                            val update = mapOf("currentLimit" to 0)

                            getMenu(menus.id).updateChildren(update)

                        }
                    }
                }

                getTableRef.setValue(Table())

                toast("Reset Successfully!")
            }

        }
    }

    @SuppressLint("CheckResult")
    private fun onDataListener() {

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

                txt_order_new.text = StringBuilder("$countNewOrder new")
                txt_order_pending.text = StringBuilder("$countPendingOrder pending")
                txt_order_complete.text = StringBuilder("$countCompleteOrder past")

            }

            Handler().postDelayed({ progressbar.visibility = View.GONE }, 1000)

        }

        getWaiter(getDate).dataChanges().subscribe {child ->

                progressbar_waiter.visibility = View.VISIBLE

                var countNewOrder = 0
                var countPendingOrder = 0
                var countCompleteOrder = 0
                var countCancelOrder = 0

            child.children.forEach {data ->

                    val value = data.getValue(Waiter::class.java)

                    if (value != null) {

                        if (value.status == STATUS.WAITING) {
                            countNewOrder += 1
                        }

                        if (value.status == STATUS.PENDING) {
                            countPendingOrder += 1
                        }

                        if (value.status == STATUS.COMPLETE) {
                            countCompleteOrder += 1
                        }

                        if (value.status == STATUS.CANCEL) {
                            countCancelOrder += 1
                        }

                    }

                    txt_order_new_waiter.text = StringBuilder("$countNewOrder new")
                    txt_order_pending_waiter.text = StringBuilder("$countPendingOrder pending")
                    txt_order_complete_waiter.text = StringBuilder("$countCompleteOrder past")

                }

                Handler().postDelayed({ progressbar_waiter.visibility = View.GONE }, 1000)

        }

        getContactRef.dataChanges().subscribe {

            var count = 0

            it?.children?.forEach { data ->

                data?.children?.forEach { dataValue ->

                    val contact = dataValue.getValue(Message::class.java)

                    if (contact != null)
                    {

                        if (!contact.read && contact.user_to == "admin") {
                            count += 1
                        }

                    }
                }
            }

            badge_notification.text = count.toString()

        }

    }
}
