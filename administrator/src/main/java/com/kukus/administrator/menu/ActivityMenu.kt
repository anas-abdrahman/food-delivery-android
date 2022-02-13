package com.kukus.administrator.menu

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.kukus.administrator.R
import com.kukus.administrator.menu.tab.MenuAdapterTab
import kotlinx.android.synthetic.main.activity_menu.*

class ActivityMenu : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        setupAdapterTab()
    }

    private fun setupAdapterTab() {

        val mAdapter = MenuAdapterTab(supportFragmentManager)

        viewpager.adapter = mAdapter
        viewpager.offscreenPageLimit = mAdapter.count
        viewpager.adapter = mAdapter.SectionsPagerAdapter(supportFragmentManager)
        viewpager.setCurrentItem(0, false)

        tabs.setupWithViewPager(viewpager)

    }
}
