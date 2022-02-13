package com.kukus.administrator.order.tab

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import com.kukus.library.Constant.Companion.STATUS


import java.util.ArrayList

class OrderAdapterTab(fm: FragmentManager) : FragmentPagerAdapter(fm) {

    private val mFragmentList: ArrayList<Fragment> = arrayListOf()
    private val mTabText = arrayOf(STATUS.WAITING, STATUS.PENDING, STATUS.COMPLETE, STATUS.CANCEL)

    init {

        mFragmentList.add(OrderList.newInstance(mTabText[0]))
        mFragmentList.add(OrderList.newInstance(mTabText[1]))
        mFragmentList.add(OrderList.newInstance(mTabText[2]))
        mFragmentList.add(OrderList.newInstance(mTabText[3]))

    }

    override fun getItem(position: Int): Fragment {
        return mFragmentList[position]
    }

    override fun getCount(): Int {
        return mFragmentList.size
    }

    fun getFragment(position : Int) : OrderList {

        return mFragmentList[position] as OrderList

    }

    inner class SectionsPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

        override fun getItem(position: Int): Fragment {

            for (i in 0 until mFragmentList.size) {
                if(position == i) return mFragmentList[i]
            }

            return Fragment()

        }

        override fun getCount(): Int {
            return mFragmentList.count()
        }

        override fun getPageTitle(position: Int): CharSequence? {

            for (i in 0 until mTabText.size) {
                if(position == i) return mTabText[i].name
            }

            return null
        }
    }
}