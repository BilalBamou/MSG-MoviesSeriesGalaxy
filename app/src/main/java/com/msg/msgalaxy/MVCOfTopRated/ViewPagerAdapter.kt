package com.msg.msgalaxy.MVCOfTopRated

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.msg.msgalaxy.MVCOfTopRated.forMovies.TopRatedMoviesFragment
import com.msg.msgalaxy.MVCOfTopRated.forSeries.TopRatedSeriesFragment

class ViewPagerAdapter(var fragmentManager: FragmentManager, var lifecycle: Lifecycle) : FragmentStateAdapter(fragmentManager , lifecycle) {

    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        if(position == 1) {
            //the second TabItem
            return TopRatedSeriesFragment()
        }

        //the first TabItem
        return TopRatedMoviesFragment()
    }
}