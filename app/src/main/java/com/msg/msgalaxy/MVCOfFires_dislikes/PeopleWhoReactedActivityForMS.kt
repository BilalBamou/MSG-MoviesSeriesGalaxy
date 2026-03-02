package com.msg.msgalaxy.MVCOfFires_dislikes

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.cardview.widget.CardView
import androidx.fragment.app.FragmentManager
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.msg.msgalaxy.R

class PeopleWhoReactedActivityForMS : AppCompatActivity() {

    companion object {
        var name: String? = null
    }

    private lateinit var arrowBack: CardView
    private var numberOfFires: String? = null
    private var numberOfHalves: String? = null
    private var numberOfDislikes: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_people_who_reacted_for_ms)

        //Initialize The variables
        val tabLayout: TabLayout? = findViewById(R.id.peopleWhoReactedActivity_forMS_tablayoutId)
        val viewPager2: ViewPager2? = findViewById(R.id.peopleWhoReactedActivity_forMS_viewPagerId)
        arrowBack = findViewById(R.id.peopleWhoReactedActivity_forMS_arrowBackId)

        // Set up the bundle with data
        var bundle: Bundle = intent!!.extras!!

        name = bundle.getString("name")
        numberOfFires = bundle.getString("numberOfFires")
        numberOfHalves = bundle.getString("numberOfHalves")
        numberOfDislikes = bundle.getString("numberOfDislikes")

        tabLayoutWithViewPager2(tabLayout!! , viewPager2!!)

        arrowBackProcess()
    }

    private fun tabLayoutWithViewPager2(tabLayout: TabLayout, viewPager2: ViewPager2) {
        var fragmentManager: FragmentManager = this.supportFragmentManager
        var viewPagerAdapter: ViewPagerAdapter = ViewPagerAdapter(fragmentManager, this.lifecycle)

        viewPager2.adapter = viewPagerAdapter

        tabLayout.addTab(tabLayout.newTab().setText( "All ${( numberOfFires!!.toInt() + numberOfHalves!!.toInt() + numberOfDislikes!!.toInt() )}"))
        tabLayout.addTab(tabLayout.newTab().setText(numberOfFires))
        tabLayout.addTab(tabLayout.newTab().setText(numberOfHalves))
        tabLayout.addTab(tabLayout.newTab().setText(numberOfDislikes))

        tabLayout.getTabAt(1)!!.setIcon(R.drawable.selected_fire_icon)
        tabLayout.getTabAt(2)!!.setIcon(R.drawable.selected_half_icon)
        tabLayout.getTabAt(3)!!.setIcon(R.drawable.selected_dislike_icon)

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