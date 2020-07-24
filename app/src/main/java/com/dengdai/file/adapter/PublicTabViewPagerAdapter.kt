package com.dengdai.file.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

/**
 * 选项卡tab
 */
@Suppress("DEPRECATION")
class PublicTabViewPagerAdapter(fm: FragmentManager?, //fragment列表
                                private val mFragments: List<Fragment>, //tab名的列表
                                private val mTitles: List<String>) : FragmentPagerAdapter(fm!!) {

    override fun getItem(i: Int): Fragment {
        return mFragments[i]
    }

    override fun getCount(): Int {
        return mFragments.size
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return mTitles[position % mTitles.size]
    }

}