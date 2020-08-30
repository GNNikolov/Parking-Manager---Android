package com.joron.parkingmanager.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.joron.parkingmanager.fragment.MainFragment
import com.joron.parkingmanager.fragment.ParkingStayFragment

/**
 * Created by Joro on 23/08/2020
 */
class ViewPagerAdapter(context: FragmentActivity) : FragmentStateAdapter(context) {
    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return if (position == 0)
            MainFragment()
        else
            ParkingStayFragment()
    }
}