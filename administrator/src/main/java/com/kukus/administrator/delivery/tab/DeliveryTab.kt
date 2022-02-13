package com.kukus.administrator.delivery.tab

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import com.kukus.library.Constant


import java.util.ArrayList

class DeliveryTab(fm: FragmentManager) : FragmentPagerAdapter(fm) {

    private val mTextList: ArrayList<Fragment> = arrayListOf()
    private val mTabText = arrayOf(
            Constant.Companion.TRIP.ACTIVE,
            Constant.Companion.TRIP.UPCOMING,
            Constant.Companion.TRIP.PAST
    )

    init {

        mTextList.add(DeliveryList.newInstance(mTabText[0]))
        mTextList.add(DeliveryList.newInstance(mTabText[1]))
        mTextList.add(DeliveryList.newInstance(mTabText[2]))

    }

    override fun getItem(position: Int): Fragment {
        return mTextList[position]
    }

    override fun getCount(): Int {
        return mTextList.size
    }

    fun getFragment(position : Int) : DeliveryList {

        return mTextList[position] as DeliveryList

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
                if(position == i) return mTabText[i].name
            }

            return null
        }
    }
}