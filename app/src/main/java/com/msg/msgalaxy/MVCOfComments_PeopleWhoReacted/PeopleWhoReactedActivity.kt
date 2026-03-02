package com.msg.msgalaxy.MVCOfComments_PeopleWhoReacted

import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.msg.msgalaxy.R

class PeopleWhoReactedActivity : AppCompatActivity() {

    companion object {
        var name: String? = null
        var commentId: String? = null
    }
    private var totalCount: Long = 0L
    private var likesCount: Long = 0L
    private var lovesCount: Long = 0L
    private var laughsCount: Long = 0L
    private var angryCount: Long = 0L

    private lateinit var arrowBack: CardView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_people_who_reacted)

        //Initialize The variables
        val tabLayout: TabLayout? = findViewById(R.id.peopleWhoReactedActivity_tablayoutId)
        val viewPager2: ViewPager2? = findViewById(R.id.peopleWhoReactedActivity_viewPagerId)
        arrowBack = findViewById(R.id.peopleWhoReactedActivity_arrowBackId)

        // Set up the bundle with data
        var bundle: Bundle = intent!!.extras!!

        name = bundle.getString("name")
        commentId = bundle.getString("commentId")
        totalCount = bundle.getLong("totalCount")
        likesCount = bundle.getLong("likesCount")
        lovesCount = bundle.getLong("lovesCount")
        laughsCount = bundle.getLong("laughsCount")
        angryCount = bundle.getLong("angryCount")

        tabLayoutWithViewPager2(tabLayout!! , viewPager2!!)

        arrowBackProcess()
    }

    private fun tabLayoutWithViewPager2(tabLayout: TabLayout, viewPager2: ViewPager2) {
        var fragmentManager: FragmentManager = this.supportFragmentManager
        var viewPagerAdapter: ViewPagerAdapter = ViewPagerAdapter(fragmentManager , this.lifecycle)

        viewPager2.adapter = viewPagerAdapter

        tabLayout.addTab(tabLayout.newTab().setText( "All $totalCount" ))

        tabLayout.addTab(tabLayout.newTab().setText( "$likesCount" ))
        tabLayout.getTabAt(1)!!.setIcon(R.drawable.like_comments_reaction)

        tabLayout.addTab(tabLayout.newTab().setText( "$lovesCount" ))
        tabLayout.getTabAt(2)!!.setIcon(R.drawable.love_comments_reaction)

        tabLayout.addTab(tabLayout.newTab().setText( "$laughsCount" ))
        tabLayout.getTabAt(3)!!.setIcon(R.drawable.laugh_comments_reaction)

        tabLayout.addTab(tabLayout.newTab().setText( "$angryCount" ))
        tabLayout.getTabAt(4)!!.setIcon(R.drawable.angry_comments_reaction)

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                viewPager2.currentItem = tab!!.position
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })

        viewPager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                tabLayout.selectTab(tabLayout.getTabAt(position))
            }
        })
    }

    private fun arrowBackProcess() {
        arrowBack.setOnClickListener {
            onBackPressed()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }
}