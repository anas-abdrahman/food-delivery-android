package com.kukus.administrator.orderWaiter

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.kukus.administrator.R
import kotlinx.android.synthetic.main.fragment_order_tab.*
import com.androidhuman.rxfirebase2.database.dataChanges
import com.kukus.administrator.orderWaiter.tab.OrderWaiterAdapterTab
import com.kukus.library.Constant.Companion.STATUS
import com.kukus.library.FirebaseUtils.Companion.getDate
import com.kukus.library.FirebaseUtils.Companion.getWaiter
import com.kukus.library.model.Waiter


class ActivityOrdersWaiter : AppCompatActivity() {

    private lateinit var mAdapter: OrderWaiterAdapterTab

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.fragment_order_tab)

        setupAdapterTab()

        //setupBadgeTab()

    }

    private fun setupAdapterTab() {

        mAdapter = OrderWaiterAdapterTab(supportFragmentManager)
        viewpager.offscreenPageLimit = mAdapter.count
        viewpager.adapter = mAdapter.SectionsPagerAdapter(supportFragmentManager)
        viewpager.setCurrentItem(0, false)

        tabs.setupWithViewPager(viewpager)

    }

    @SuppressLint("CheckResult")
    private fun setupBadgeTab() {

        getWaiter(getDate).dataChanges().subscribe { dataRef ->

            var count_new_order = 0
            var count_placed_order = 0
            var count_complete_order = 0
            var count_cancel_order = 0

            tabs.setBadgeText(0, null)
            tabs.setBadgeText(1, null)
            tabs.setBadgeText(2, null)

            dataRef.children.forEach {

                val value = it.getValue(Waiter::class.java)

                if (value != null) {

                    if (value.status == STATUS.WAITING) {
                        count_new_order += 1

                    }

                    if (value.status == STATUS.PENDING) {
                        count_placed_order += 1

                    }

                    if (value.status == STATUS.COMPLETE) {
                        count_complete_order += 1

                    }

                    if (value.status == STATUS.DISPATCHED) {
                        count_placed_order += 1

                    }

                    if (value.status == STATUS.CANCEL) {
                        count_cancel_order += 1

                    }

                }

            }


            if (count_placed_order > 0) tabs.setBadgeText(0, (count_placed_order).toString())
            if (count_complete_order > 0) tabs.setBadgeText(1, (count_complete_order).toString())
            if (count_cancel_order > 0) tabs.setBadgeText(2, (count_cancel_order).toString())


        }
    }

}
