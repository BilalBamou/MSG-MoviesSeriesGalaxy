package com.msg.msgalaxy.MVCOfTopRated.forSeries

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener
import com.msg.msgalaxy.R
import com.msg.msgalaxy.SingletonOfFirebase

class TopRatedSeriesFragment : Fragment() {

    private lateinit var view: View
    private var recyclerViewOfSeries: RecyclerView? = null
    private var adapter: AdapterOfTopRatedSeries? = null
    private var topRatedSeriesList: ArrayList<ModelOfTopRatedSeries>? = null

    //Shimmer effect from Facebook ( Loading Effects )
    private var shimmerFrameLayout: ShimmerFrameLayout? = null

    //Singleton Design Pattern ( SingletonOfFirebase Class )
    private var singletonOfFirebase = SingletonOfFirebase.getInstance()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        view = inflater.inflate(R.layout.tablayout_toprated_series_fragment, container , false)

        //Initialize the variables
        recyclerViewOfSeries = view.findViewById(R.id.tablayoutTopRatedSeriesFragment_recyclerViewId)
        shimmerFrameLayout = view.findViewById(R.id.tablayoutTopRatedSeriesFragment_shimmerFrameLayout)

        recyclerViewOfSeries!!.layoutManager = LinearLayoutManager(context)
        recyclerViewOfSeries!!.setHasFixedSize(true)

        // When the data is loading
        shimmerFrameLayout!!.startShimmer()

        getDataFromFirebase()

        return view    }

    private fun getDataFromFirebase() {
        topRatedSeriesList = ArrayList()
        var query: Query = singletonOfFirebase.getData()!!.child("topratedseries")
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                // When the data is loaded
                recyclerViewOfSeries!!.visibility = View.VISIBLE
                shimmerFrameLayout!!.stopShimmer()
                shimmerFrameLayout!!.visibility = View.GONE

                for ( snapshot: DataSnapshot in dataSnapshot.children) {
                    var modelOfTopRatedSeries: ModelOfTopRatedSeries = ModelOfTopRatedSeries()

                    modelOfTopRatedSeries.picture = snapshot.child("Picture").value.toString()
                    modelOfTopRatedSeries.rank = snapshot.child("Rank").value.toString()
                    modelOfTopRatedSeries.name = snapshot.child("Name").value.toString()
                    modelOfTopRatedSeries.year = snapshot.child("Year").value.toString()
                    modelOfTopRatedSeries.duration = snapshot.child("Duration").value.toString()
                    modelOfTopRatedSeries.rating = snapshot.child("Rating").value.toString()
                    modelOfTopRatedSeries.description = snapshot.child("Description").value.toString()
                    modelOfTopRatedSeries.type = snapshot.child("Type").value.toString()
                    modelOfTopRatedSeries.trailerUrl = snapshot.child("Trailer").value.toString()

                    topRatedSeriesList!!.add(modelOfTopRatedSeries)
                }

                adapter = AdapterOfTopRatedSeries(context!!, topRatedSeriesList!!)
                recyclerViewOfSeries!!.adapter = adapter
                adapter!!.notifyDataSetChanged()

            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }
}