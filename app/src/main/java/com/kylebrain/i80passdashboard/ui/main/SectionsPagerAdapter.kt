package com.kylebrain.i80passdashboard.ui.main

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.kylebrain.i80passdashboard.R

private val TAB_TITLES = arrayOf(
        R.string.camera_tab_name,
        R.string.twitter_tab_name,
        R.string.maps_tab_name,
        R.string.weather_tab_name
)

private val TAB_INSTANCES = arrayOf(
    CameraFragment.newInstance(0),
    TwitterFragment.newInstance("test1", "test2"),
    TwitterFragment.newInstance("test1", "test2"),
    TwitterFragment.newInstance("test1", "test2")
)

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
class SectionsPagerAdapter(private val context: Context, fm: FragmentManager)
    : FragmentPagerAdapter(fm) {

    override fun getItem(position: Int): Fragment {
        // getItem is called to instantiate the fragment for the given page.
        return TAB_INSTANCES[position]
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return context.resources.getString(TAB_TITLES[position])
    }

    override fun getCount(): Int {
        // Show 4 total pages.
        return 4
    }
}