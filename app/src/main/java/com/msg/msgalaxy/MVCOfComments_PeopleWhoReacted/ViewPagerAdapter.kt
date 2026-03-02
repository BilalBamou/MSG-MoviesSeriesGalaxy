package com.msg.msgalaxy.MVCOfComments_PeopleWhoReacted

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.msg.msgalaxy.MVCOfComments_PeopleWhoReacted.forAllReactions.AllReactionsOfCommentFragment
import com.msg.msgalaxy.MVCOfComments_PeopleWhoReacted.forAngry.AngryReactionsFragment
import com.msg.msgalaxy.MVCOfComments_PeopleWhoReacted.forLaughs.LaughReactionsFragment
import com.msg.msgalaxy.MVCOfComments_PeopleWhoReacted.forLikes.LikeReactionsFragment
import com.msg.msgalaxy.MVCOfComments_PeopleWhoReacted.forLoves.LoveReactionsFragment

class ViewPagerAdapter : FragmentStateAdapter {

    constructor(fragmentManager: FragmentManager, lifecycle: Lifecycle) : super(fragmentManager , lifecycle) {}

    override fun createFragment(position: Int): Fragment {
        if (position == 1) {
            return LikeReactionsFragment()
        }
        if (position == 2) {
            return LoveReactionsFragment()
        }
        if(position == 3) {
            return LaughReactionsFragment()
        }
        if (position == 4) {
            return AngryReactionsFragment()
        }
        return AllReactionsOfCommentFragment()
    }

    override fun getItemCount(): Int = 5
}