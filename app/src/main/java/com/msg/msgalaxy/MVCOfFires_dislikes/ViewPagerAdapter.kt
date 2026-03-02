package com.msg.msgalaxy.MVCOfFires_dislikes

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.msg.msgalaxy.MVCOfFires_dislikes.forAllFiresDislikesHalves.AllFiresAndDislikesFragment
import com.msg.msgalaxy.MVCOfFires_dislikes.forDislikes.DislikesFragment
import com.msg.msgalaxy.MVCOfFires_dislikes.forFires.FiresFragment
import com.msg.msgalaxy.MVCOfFires_dislikes.forHalves.HalvesFragment

class ViewPagerAdapter : FragmentStateAdapter {

    constructor(fragmentManager: FragmentManager , lifecycle: Lifecycle) : super(fragmentManager , lifecycle) {}

    override fun createFragment(position: Int): Fragment {
        if (position == 1) {
            return FiresFragment()
        }
        if(position == 2) {
            return HalvesFragment()
        }
        if (position == 3) {
            return DislikesFragment()
        }
        return AllFiresAndDislikesFragment()
    }

    override fun getItemCount(): Int = 4
}