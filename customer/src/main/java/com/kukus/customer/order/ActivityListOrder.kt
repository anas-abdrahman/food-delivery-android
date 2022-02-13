package com.kukus.customer.order

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.kukus.customer.R
import com.kukus.customer.order.tab.OrderAdapterTab
import kotlinx.android.synthetic.main.activity_order.*

class ActivityListOrder : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_order)
        setupAdapterTab()

    }

    private fun setupAdapterTab() {

        val mAdapter = OrderAdapterTab(supportFragmentManager)

        viewpager.offscreenPageLimit = mAdapter.count
        viewpager.adapter = mAdapter.SectionsPagerAdapter(supportFragmentManager)
        viewpager.setCurrentItem(0, false)

        tabs.setupWithViewPager(viewpager)
    }

}
