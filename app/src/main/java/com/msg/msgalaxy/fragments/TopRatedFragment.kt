package com.msg.msgalaxy.fragments

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.RelativeLayout
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.viewpager2.widget.ViewPager2
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.google.android.material.tabs.TabLayout
import com.msg.msgalaxy.MVCOfTopRated.ViewPagerAdapter
import com.msg.msgalaxy.R

class TopRatedFragment: Fragment() {

    private lateinit var view: View
    private lateinit var noInternet: RelativeLayout
    private lateinit var retryButton: CardView

    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager2: ViewPager2
    private lateinit var viewPagerAdapter: ViewPagerAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //Clear the Transparent of status bar
            activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        }

        view = inflater.inflate(R.layout.toprated_fragment , container , false)
        noInternet = view.findViewById(R.id.topRatedFragment_noInternet_relativeLayoutId)
        retryButton = view.findViewById(R.id.topRatedFragment_retryButtonId)

        //Initialize The variables
        tabLayout = view.findViewById(R.id.topRatedFragment_tablayoutId)
        viewPager2 = view.findViewById(R.id.topRatedFragment_viewPagerId)

        internetTestProcess()

        retryButton.setOnClickListener {
            onRetryButtonClick()
        }

        return view
    }

    fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = connectivityManager.activeNetwork
        val networkCapabilities = connectivityManager.getNetworkCapabilities(activeNetwork)
        return networkCapabilities != null && (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) )
    }

    private fun internetTestProcess() {
        if (!isNetworkAvailable(requireContext())) {
            noInternet.visibility = View.VISIBLE
            return
        }
        noInternet.visibility = View.GONE

        setTabLayoutWithViewPager()
    }

    private fun onRetryButtonClick() {
        if (!isNetworkAvailable(requireContext())) {
            // If still no internet
            noInternet.visibility = View.VISIBLE
            return
        }
        noInternet.visibility = View.GONE

        setTabLayoutWithViewPager()
    }

    private fun setTabLayoutWithViewPager() {
        var fragmentManager: FragmentManager = activity?.supportFragmentManager!!
        viewPagerAdapter = ViewPagerAdapter(fragmentManager , activity?.lifecycle!!)

        viewPager2.adapter = viewPagerAdapter

        //Set the titles and the icons to the TabLayout
        // If i want them in one line ( the icon and the title ) i should add this line ( app:tabInlineLabel="true" ) in XML
        tabLayout.addTab(tabLayout.newTab().setText("Movies"))
        tabLayout.getTabAt(0)!!.setIcon(R.drawable.tablayout_movies_icon)
        tabLayout.addTab(tabLayout.newTab().setText("Series"))
        tabLayout.getTabAt(1)!!.setIcon(R.drawable.tablayout_series_icon)

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                viewPager2.currentItem = tab!!.position
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })

        viewPager2.registerOnPageChangeCallback(object : OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                tabLayout.selectTab(tabLayout.getTabAt(position))
            }
        })
    }

}