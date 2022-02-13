package com.kukus.administrator.orderWaiter.tab

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import com.kukus.library.Constant.Companion.STATUS


import java.util.ArrayList

class OrderWaiterAdapterTab(fm: FragmentManager) : FragmentPagerAdapter(fm) {

    private val mFragmentList: ArrayList<Fragment> = arrayListOf()
    private val mTabText = arrayOf(STATUS.ORDER, STATUS.COMPLETE, STATUS.CANCEL)

    init {

        mFragmentList.add(OrderWaiterDash.newInstance(mTabText[0]))
        mFragmentList.add(OrderWaiterList.newInstance(mTabText[1]))
        mFragmentList.add(OrderWaiterList.newInstance(mTabText[2]))

    }

    override fun getItem(position: Int): Fragment {
        return mFragmentList[position]
    }

    override fun getCount(): Int {
        return mFragmentList.size
    }

    fun getFragment(position : Int) : OrderWaiterList {

        return mFragmentList[position] as OrderWaiterList

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

            for (i in mTabText.indices) {
                if(position == i) return mTabText[i].name
            }

            return null
        }
    }
}