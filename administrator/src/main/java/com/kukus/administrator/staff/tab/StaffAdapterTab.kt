package com.kukus.administrator.staff.tab

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import com.kukus.library.Constant.Companion.USER


import java.util.ArrayList

class StaffAdapterTab(fm: FragmentManager) : FragmentPagerAdapter(fm) {

    private val mTextList: ArrayList<Fragment> = arrayListOf()
    private val mTabText = arrayOf(USER.ADMIN.name, USER.CASHIER.name,  USER.DELIVERY.name, USER.WAITER.name)

    init {

        mTextList.add(StaffList.newInstance(USER.ADMIN))
        mTextList.add(StaffList.newInstance(USER.CASHIER))
        mTextList.add(StaffList.newInstance(USER.DELIVERY))
        mTextList.add(StaffList.newInstance(USER.WAITER))

    }

    override fun getItem(position: Int): Fragment {
        return mTextList[position]
    }

    override fun getCount(): Int {
        return mTextList.size
    }

    inner class SectionsPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

        override fun getItem(position: Int): Fragment {

            for (i in 0 until mTextList.size) {
                if(position == i) return mTextList[i]
            }

            return Fragment()

        }

        override fun getCount(): Int {
            return mTextList.count()
        }

        override fun getPageTitle(position: Int): CharSequence? {

            for (i in 0 until mTabText.size) {
                if(position == i) return mTabText[i]
            }

            return null
        }
    }
}