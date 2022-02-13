package com.kukus.customer.home.tab

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import com.kukus.library.Constant.Companion.TYPE_ORDER
import java.util.ArrayList

class HomeTab(fm: FragmentManager) : FragmentPagerAdapter(fm) {

    private val mFragmentList: ArrayList<Fragment> = arrayListOf()
    private val mTabText = arrayOf(TYPE_ORDER.FOOD, TYPE_ORDER.DRINK, TYPE_ORDER.EXTRA)

    init {

        mFragmentList.add(HomeList.newInstance(mTabText[0]))
        mFragmentList.add(HomeList.newInstance(mTabText[1]))
        mFragmentList.add(HomeList.newInstance(mTabText[2]))

    }

    override fun getItem(position: Int): Fragment {
        return mFragmentList[position]
    }

    override fun getCount(): Int {
        return mFragmentList.size
    }

    inner class SectionsPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

        override fun getItem(position: Int): Fragment {

            for (i in 0 until mFragmentList.size) {
                if (position == i) return mFragmentList[i]
            }

            return Fragment()

        }

        override fun getCount(): Int {
            return mFragmentList.count()
        }

        override fun getPageTitle(position: Int): CharSequence? {

            for (i in 0 until mTabText.size) {
                if (position == i) return mTabText[i].name
            }

            return null
        }
    }
}