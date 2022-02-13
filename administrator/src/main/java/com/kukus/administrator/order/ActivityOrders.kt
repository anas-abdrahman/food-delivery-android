package com.kukus.administrator.order

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.kukus.administrator.R
import com.kukus.administrator.order.tab.OrderAdapterTab
import kotlinx.android.synthetic.main.fragment_order_tab.*
import android.app.DatePickerDialog
import com.androidhuman.rxfirebase2.database.dataChanges
import com.kukus.library.model.Ship
import com.kukus.library.Constant.Companion.STATUS
import com.kukus.library.FirebaseUtils.Companion.getDate
import com.kukus.library.FirebaseUtils.Companion.getShip
import java.util.*


class ActivityOrders : AppCompatActivity() {

    private lateinit var mAdapter: OrderAdapterTab

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.fragment_order_tab)

        setupAdapterTab()

        setupBadgeTab()

    }

    private fun setupAdapterTab() {

        mAdapter = OrderAdapterTab(supportFragmentManager)

        viewpager.adapter = mAdapter
        viewpager.offscreenPageLimit = mAdapter.count
        viewpager.adapter = mAdapter.SectionsPagerAdapter(supportFragmentManager)
        viewpager.setCurrentItem(0, false)

        tabs.setupWithViewPager(viewpager)

    }

    @SuppressLint("CheckResult")
    private fun setupBadgeTab() {

        getShip(getDate).dataChanges().subscribe { dataRef ->

            var count_new_order = 0
            var count_pending_order = 0
            var count_complete_order = 0
            var count_cancel_order = 0

            tabs.setBadgeText(0, null)
            tabs.setBadgeText(1, null)
            tabs.setBadgeText(2, null)
            tabs.setBadgeText(3, null)

            dataRef.children.forEach {

                val value = it.getValue(Ship::class.java)

                if (value != null) {

                    if (value.status == STATUS.WAITING) {
                        count_new_order += 1
                    }

                    if (value.status == STATUS.PENDING) {
                        count_pending_order += 1
                    }

                    if (value.status == STATUS.DISPATCHED) {
                        count_pending_order += 1
                    }

                    if (value.status == STATUS.COMPLETE) {
                        count_complete_order += 1
                    }

                    if (value.status == STATUS.CANCEL) {
                        count_cancel_order += 1
                    }

                }

            }

            if (count_new_order > 0) tabs.setBadgeText(0, (count_new_order).toString())
            if (count_pending_order > 0) tabs.setBadgeText(1, (count_pending_order).toString())
            if (count_complete_order > 0) tabs.setBadgeText(2, (count_complete_order).toString())
            if (count_cancel_order > 0) tabs.setBadgeText(3, (count_cancel_order).toString())

        }
    }

    private val calendar: Calendar = Calendar.getInstance()
    var mYear = calendar.get(Calendar.YEAR)
    var mMonth = calendar.get(Calendar.MONTH)
    var mDay = calendar.get(Calendar.DAY_OF_MONTH)

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

            val date = ("$year$month$day")

            for (i in 0 until mAdapter.count) {
                mAdapter.getFragment(i).setDataList(date)
            }


            mYear = year
            mMonth = monthOfYear
            mDay = dayOfMonth

        }, mYear, mMonth, mDay).show()

    }

}
