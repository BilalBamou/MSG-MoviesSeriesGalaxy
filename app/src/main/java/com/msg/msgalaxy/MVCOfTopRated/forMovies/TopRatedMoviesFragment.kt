package com.msg.msgalaxy.MVCOfTopRated.forMovies

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

class TopRatedMoviesFragment : Fragment() {

    private lateinit var view: View
    private var recyclerViewOfMovies: RecyclerView? = null
    private var adapter: AdapterOfTopRatedMovies? = null
    private var topRatedMoviesList: ArrayList<ModelOfTopRatedMovies>? = null

    //Shimmer effect from Facebook ( Loading Effects )
    private var shimmerFrameLayout: ShimmerFrameLayout? = null

    //Singleton Design Pattern ( SingletonOfFirebase Class )
    private var singletonOfFirebase = SingletonOfFirebase.getInstance()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        view = inflater.inflate(R.layout.tablayout_toprated_movies_fragment, container , false)

        //Initialize the variables
        recyclerViewOfMovies = view.findViewById(R.id.tablayoutTopRatedMoviesFragment_recyclerViewId)
        shimmerFrameLayout = view.findViewById(R.id.tablayoutTopRatedMoviesFragment_shimmerFrameLayout)

        recyclerViewOfMovies!!.layoutManager = LinearLayoutManager(context)
        recyclerViewOfMovies!!.setHasFixedSize(true)

        // When the data is loading
        shimmerFrameLayout!!.startShimmer()

        getDataFromFirebase()

        return view
    }

    private fun getDataFromFirebase() {

        topRatedMoviesList = ArrayList()
        var query: Query = singletonOfFirebase.getData()!!.child("topratedmovies")
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                // When the data is loaded
                recyclerViewOfMovies!!.visibility = View.VISIBLE
                shimmerFrameLayout!!.stopShimmer()
                shimmerFrameLayout!!.visibility = View.GONE

                for ( snapshot: DataSnapshot in dataSnapshot.children) {
                    var modelOfTopRatedMovies: ModelOfTopRatedMovies = ModelOfTopRatedMovies()

                    modelOfTopRatedMovies.picture = snapshot.child("Picture").value.toString()
                    modelOfTopRatedMovies.rank = snapshot.child("Rank").value.toString()
                    modelOfTopRatedMovies.name = snapshot.child("Name").value.toString()
                    modelOfTopRatedMovies.year = snapshot.child("Year").value.toString()
                    modelOfTopRatedMovies.duration = snapshot.child("Duration").value.toString()
                    modelOfTopRatedMovies.rating = snapshot.child("Rating").value.toString()
                    modelOfTopRatedMovies.description = snapshot.child("Description").value.toString()
                    modelOfTopRatedMovies.type = snapshot.child("Type").value.toString()
                    modelOfTopRatedMovies.trailerUrl = snapshot.child("Trailer").value.toString()

                    topRatedMoviesList!!.add(modelOfTopRatedMovies)
                }

                adapter = AdapterOfTopRatedMovies(context!!, topRatedMoviesList!!)
                recyclerViewOfMovies!!.adapter = adapter
                adapter!!.notifyDataSetChanged()

            }

            override fun onCancelled(error: DatabaseError) {

            }
        })

    }

}