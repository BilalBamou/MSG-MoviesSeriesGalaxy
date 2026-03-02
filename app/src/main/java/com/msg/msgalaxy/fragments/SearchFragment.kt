package com.msg.msgalaxy.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.google.android.material.search.SearchView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener
import com.msg.msgalaxy.MVCOfSearch.AdapterOfSearch
import com.msg.msgalaxy.MVCOfSearch.ModelOfSearch
import com.msg.msgalaxy.R
import com.msg.msgalaxy.SingletonOfFirebase

class SearchFragment: Fragment() {

    private lateinit var view: View

    private lateinit var profileView: RelativeLayout
    private lateinit var loadingData: RelativeLayout
    private lateinit var noInternet: RelativeLayout
    private lateinit var retryButton: CardView

    //Search Variables
    private lateinit var recyclerViewOfSearch: RecyclerView
    private lateinit var adapterOfSearch: AdapterOfSearch
    private lateinit var noDataFoundView: LinearLayout
    private lateinit var searchView: SearchView
    private lateinit var searchList: ArrayList<ModelOfSearch>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //Clear the Transparent of status bar
            activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        }

        view = inflater.inflate(R.layout.search_fragment , container , false)

        profileView = view.findViewById(R.id.searchFragment_viewOfProfileRelativeLayoutId)
        loadingData = view.findViewById(R.id.searchFragment_lottieLoadingAnimation_relativeLayoutId)

        noInternet = view.findViewById(R.id.searchFragment_noInternet_relativeLayoutId)
        retryButton = view.findViewById(R.id.searchFragment_retryButtonId)


        internetTestProcess()

        retryButton.setOnClickListener {
            onRetryButtonClick()
        }

        //SearchProcess Function
        searchProcess()

        //To hide the keyboard
        hideKeyboard()

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
            profileView.visibility = View.GONE
            loadingData.visibility = View.GONE
            noInternet.visibility = View.VISIBLE
            return
        }
        loadingData.visibility = View.VISIBLE
        noInternet.visibility = View.GONE

        // Attempt to get data again
        getDataFromFirebase()
    }

    private fun onRetryButtonClick() {
        if (!isNetworkAvailable(requireContext())) {
            // If still no internet
            profileView.visibility = View.GONE
            loadingData.visibility = View.GONE
            noInternet.visibility = View.VISIBLE
            return
        }
        loadingData.visibility = View.VISIBLE
        noInternet.visibility = View.GONE

        // Attempt to get data again
        getDataFromFirebase()
    }

    private fun searchProcess() {
        //Initialize the variables
        recyclerViewOfSearch = view.findViewById(R.id.searchFragment_searchList_RecyclerViewId)
        noDataFoundView = view.findViewById(R.id.searchFragment_noDataFound_linearLayoutId)

        recyclerViewOfSearch.layoutManager = LinearLayoutManager(context)
        recyclerViewOfSearch.setHasFixedSize(true)

        searchView = view.findViewById(R.id.searchFragment_searchViewId)
        searchView.editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Update the visibility based on whether text is present
                recyclerViewOfSearch.visibility = if(s!!.length > 0) View.VISIBLE else View.GONE
                filterList(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        getDataFromFirebase()
    }

    private fun filterList(text: String) {
        var filteredList: ArrayList<ModelOfSearch> = ArrayList()
        if (adapterOfSearch != null) {
            for (movieOrSerie: ModelOfSearch in searchList) {
                if(movieOrSerie.name.lowercase().trim().contains(text.lowercase())) {
                    filteredList.add(movieOrSerie)
                }
            }
            if (filteredList.isEmpty()) {
                noDataFoundView.visibility = View.VISIBLE
                adapterOfSearch.setFilteredList(filteredList)
            }
            else {
                noDataFoundView.visibility = View.GONE
                adapterOfSearch.setFilteredList(filteredList)
            }
        }
    }

    private fun getDataFromFirebase() {

        //Set visibility to loading data Animation
        loadingData.visibility = View.VISIBLE
        profileView.visibility = View.GONE

        searchList = ArrayList()

        val query: Query = FirebaseDatabase.getInstance().reference.child("allMsgData")
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val movieNamesSet = HashSet<String>()

                for (snapshot: DataSnapshot in dataSnapshot.children) {
                    val movieName = snapshot.child("Name").value.toString()

                    // Check if the movie name has already been added
                    if (!movieNamesSet.contains(movieName)) {
                        val modelOfSearch: ModelOfSearch = ModelOfSearch()

                        modelOfSearch.picture = snapshot.child("Picture").value.toString()
                        modelOfSearch.name = movieName
                        modelOfSearch.year = snapshot.child("Year").value.toString()
                        modelOfSearch.duration = snapshot.child("Duration").value.toString()
                        modelOfSearch.rating = snapshot.child("Rating").value.toString()
                        modelOfSearch.description = snapshot.child("Description").value.toString()
                        modelOfSearch.type = snapshot.child("Type").value.toString()
                        modelOfSearch.trailerUrl = snapshot.child("Trailer").value.toString()

                        searchList.add(modelOfSearch)
                        movieNamesSet.add(movieName)
                    }
                }

                // Filter out duplicates directly in the searchList
                searchList = searchList.distinctBy { it.name }.toMutableList() as ArrayList<ModelOfSearch>

                // Check if the context is not null before creating the adapter
                context?.let {
                    adapterOfSearch = AdapterOfSearch(it, searchList)
                    recyclerViewOfSearch.adapter = adapterOfSearch
                    adapterOfSearch.notifyDataSetChanged()
                }

                profileView.visibility = View.VISIBLE
                loadingData.visibility = View.GONE
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun hideKeyboard() {
        recyclerViewOfSearch.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                var imm: InputMethodManager = context!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(view.windowToken , 0)
                return false
            }
        })
    }

}