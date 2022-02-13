package com.kukus.administrator.waiter.tab

import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import com.kukus.library.Constant.Companion.TYPE_ORDER
import java.util.*

class WaiterAdapterTab(fm: FragmentManager, orderId : String = "", isEdit: Boolean = false) : FragmentPagerAdapter(fm) {

    private val mFragmentList: ArrayList<WaiterList> = arrayListOf()
    private val mTabText = arrayOf("Food", "Drink", "Extra")

    init {

        mFragmentList.add(WaiterList.newInstance(TYPE_ORDER.FOOD,  orderId, isEdit))
        mFragmentList.add(WaiterList.newInstance(TYPE_ORDER.DRINK, orderId, isEdit))
        mFragmentList.add(WaiterList.newInstance(TYPE_ORDER.EXTRA, orderId, isEdit))
    }

    override fun getItem(position: Int): WaiterList {
        return mFragmentList[position]
    }

    override fun getCount(): Int {
        return mFragmentList.count()
    }

    inner class SectionsPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

        override fun getItem(position: Int): WaiterList {

            for (i in 0 until mFragmentList.size) {
                if(position == i) return mFragmentList[i]
            }

            return WaiterList()

        }

        override fun getCount(): Int {
            return mFragmentList.count()
        }

        override fun getPageTitle(position: Int): CharSequence? {

            for (i in mTabText.indices) {
                if(position == i) return mTabText[i]
            }

            return null
        }
    }
}