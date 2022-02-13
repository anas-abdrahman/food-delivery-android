package com.kukus.administrator.staff

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.kukus.administrator.R
import com.kukus.administrator.staff.tab.StaffAdapterTab
import kotlinx.android.synthetic.main.activity_staff.*

class ActivityStaff : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_staff)
        setupAdapterTab()
    }

    private fun setupAdapterTab() {

        val mAdapter = StaffAdapterTab(supportFragmentManager)

        viewpager.adapter = mAdapter
        viewpager.offscreenPageLimit = mAdapter.count
        viewpager.adapter = mAdapter.SectionsPagerAdapter(supportFragmentManager)
        viewpager.setCurrentItem(0, false)

        tabs.setupWithViewPager(viewpager)


    }
}
