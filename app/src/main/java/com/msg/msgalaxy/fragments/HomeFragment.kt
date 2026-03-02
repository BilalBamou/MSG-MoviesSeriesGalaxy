package com.msg.msgalaxy.fragments

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener
import com.msg.msgalaxy.MVCOfFifthList_homeFragment.AdapterOfFifthList
import com.msg.msgalaxy.MVCOfFifthList_homeFragment.ModelOfFifthList
import com.msg.msgalaxy.MVCOfFirstList_homeFragment.AdapterOfFirstList
import com.msg.msgalaxy.MVCOfFirstList_homeFragment.ModelOfFirstList
import com.msg.msgalaxy.MVCOfFourthList_homeFragment.AdapterOfFourthList
import com.msg.msgalaxy.MVCOfFourthList_homeFragment.ModelOfFourthList
import com.msg.msgalaxy.MVCOfSecondList_homeFragment.AdapterOfSecondList
import com.msg.msgalaxy.MVCOfSecondList_homeFragment.ModelOfSecondList
import com.msg.msgalaxy.MVCOfThirdList_homeFragment.AdapterOfThirdList
import com.msg.msgalaxy.MVCOfThirdList_homeFragment.ModelOfThirdList
import com.msg.msgalaxy.MVCOfTopMSG_homeFragment.AdapterOfTopMSG
import com.msg.msgalaxy.MVCOfTopMSG_homeFragment.ModelOfTopMSG
import com.msg.msgalaxy.MVCOfViewAll.ViewAllActivity
import com.msg.msgalaxy.R
import com.msg.msgalaxy.SingletonOfFirebase
import me.relex.circleindicator.CircleIndicator

class HomeFragment : Fragment() {

    private lateinit var view: View
    private lateinit var loadingOfData: RelativeLayout
    private lateinit var noInternet: RelativeLayout
    private lateinit var retryButton: CardView

    private lateinit var firebaseAuth: FirebaseAuth

    private lateinit var topMSGList: ArrayList<ModelOfTopMSG>
    private lateinit var playTrailerButton: CardView
    private lateinit var adapterOfTopMSG: AdapterOfTopMSG
    private lateinit var viewPager: ViewPager
    private lateinit var circleIndicator: CircleIndicator

    //The First List Of Home Fragment
    private lateinit var recyclerViewOfFirstList: RecyclerView
    private lateinit var adapterOfFirstList: AdapterOfFirstList
    private lateinit var firstListList: ArrayList<ModelOfFirstList>
    private lateinit var viewAll_firstList: CardView

    //Singleton Design Pattern ( SingletonOfFirebase Class )
    private var singletonOfFirebase: SingletonOfFirebase = SingletonOfFirebase.getInstance()

    //The Second List Of Home Fragment
    private lateinit var recyclerViewOfSecondList: RecyclerView
    private lateinit var adapterOfSecondList: AdapterOfSecondList
    private lateinit var secondListList: ArrayList<ModelOfSecondList>

    //The Third List Of Home Fragment
    private lateinit var recyclerViewOfThirdList: RecyclerView
    private lateinit var adapterOfThirdList: AdapterOfThirdList
    private lateinit var thirdListList: ArrayList<ModelOfThirdList>

    //The Fourth List Of Home Fragment
    private lateinit var recyclerViewOfFourthList: RecyclerView
    private lateinit var adapterOfFourthList: AdapterOfFourthList
    private lateinit var fourthListList: ArrayList<ModelOfFourthList>
    private lateinit var viewAll_fourthList: CardView

    //The Fifth List Of Home Fragment
    private lateinit var recyclerViewOfFifthList: RecyclerView
    private lateinit var adapterOfFifthList: AdapterOfFifthList
    private lateinit var fifthListList: ArrayList<ModelOfFifthList>
    private lateinit var viewAll_fifthList: CardView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            //Transparent status bar
//            activity?.window?.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
//        }

        view = inflater.inflate(R.layout.home_fragment , container , false)

        firebaseAuth = FirebaseAuth.getInstance()

        // About view pager
        viewPager = view.findViewById(R.id.homeFragment_viewPagerId)

        loadingOfData = view.findViewById(R.id.homeFragment_lottieLoadingAnimation_relativeLayoutId)
        noInternet = view.findViewById(R.id.homeFragment_noInternet_relativeLayoutId)
        retryButton = view.findViewById(R.id.homeFragment_retryButtonId)

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
                networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR))
    }

    private fun internetTestProcess() {
        // Check for internet connectivity before initiating network calls
        if (!isNetworkAvailable(requireContext())) {
            loadingOfData.visibility = View.GONE
            // Show the "No Internet" animation immediately
            noInternet.visibility = View.VISIBLE
            return // Exit the function here to prevent potential network errors
        }
        // Show the LoadingAnimation while loading data
        loadingOfData.visibility = View.VISIBLE
        noInternet.visibility = View.GONE // Hide the noInternet layout

        // Attempt to get data
        loadDataFromFirebase()
    }

    private fun onRetryButtonClick() {
        // Check for internet connectivity before initiating network calls
        if (!isNetworkAvailable(requireContext())) {
            // If still no internet
            loadingOfData.visibility = View.GONE
            noInternet.visibility = View.VISIBLE
            return
        }
        // Show the LoadingAnimation while loading data
        loadingOfData.visibility = View.VISIBLE
        noInternet.visibility = View.GONE // Hide the noInternet layout

        // Attempt to get data again
        loadDataFromFirebase()
    }

    private fun loadDataFromFirebase() {
        getDataFromFireBase_TopMSG()
        firstListProcess()
        secondListProcess()
        thirdListProcess()
        fourthListProcess()
        fifthListProcess()

    }

    // For Fifth List ---------------------------------------------------------------------------------
    private fun fifthListProcess() {
        //Initialize the variables
        recyclerViewOfFifthList = view.findViewById(R.id.homeFragment_fifthList_RecyclerViewId)
        viewAll_fifthList = view.findViewById(R.id.homeFragment_fifthList_viewAllId)

        viewAll_fifthList.setOnClickListener {
            var intent: Intent = Intent(requireContext() , ViewAllActivity::class.java)
            intent.putExtra("numberOfLists" , "Fifth")
            intent.putExtra("Title" , "Oscar Winning Masterpieces")
            startActivity(intent)
        }

        fifthListList = ArrayList()

        var myRef = singletonOfFirebase.getData()!!.child("Oscar Winning Masterpieces")
        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (dataSnapshot: DataSnapshot in snapshot.children) {
                    var id: String = dataSnapshot.key!!  // this is the Id ( 0 , 1 , 2 , 3 ...)

                    var movieName: String = dataSnapshot.child("Name").value.toString()
                    getRightMoviesFromAllMSGData_fromFB_FifthList(movieName)

                    if (id.equals("9")) {
                        break
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }
    private fun getRightMoviesFromAllMSGData_fromFB_FifthList(movieName: String) {
        var reference: DatabaseReference = singletonOfFirebase.getData()!!.child("allMsgData")
        fifthListList.clear()
        reference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for ( dataSnapshot: DataSnapshot in snapshot.children ) {
                    var id: String = dataSnapshot.key!!
                    var Name: String = dataSnapshot.child("Name").value.toString()
                    if(movieName.equals(Name) ) {
                        var modelOfFifthList: ModelOfFifthList = ModelOfFifthList()

                        modelOfFifthList.picture = dataSnapshot.child("Picture").value.toString()
                        modelOfFifthList.name = Name
                        modelOfFifthList.year = dataSnapshot.child("Year").value.toString()
                        modelOfFifthList.duration = dataSnapshot.child("Duration").value.toString()
                        modelOfFifthList.rating = dataSnapshot.child("Rating").value.toString()
                        modelOfFifthList.description = dataSnapshot.child("Description").value.toString()
                        modelOfFifthList.type = dataSnapshot.child("Type").value.toString()
                        modelOfFifthList.trailerUrl = dataSnapshot.child("Trailer").value.toString()

                        fifthListList.add(modelOfFifthList)
                    }
                    // Update the RecyclerView here, after fetching all movies
                    updateRecyclerView_FifthList()
                }
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }
    // I Moved this method outside the loop to update the RecyclerView only once
    private fun updateRecyclerView_FifthList() {
        context?.let {
            adapterOfFifthList = AdapterOfFifthList(it, fifthListList)
            recyclerViewOfFifthList.layoutManager = LinearLayoutManager(context , LinearLayoutManager.HORIZONTAL , false)
            recyclerViewOfFifthList.setHasFixedSize(true)
            recyclerViewOfFifthList.adapter = adapterOfFifthList
            adapterOfFifthList.notifyDataSetChanged()
        }
    }

    // For Fourth List ---------------------------------------------------------------------------------
    private fun fourthListProcess() {
        //Initialize the variables
        recyclerViewOfFourthList = view.findViewById(R.id.homeFragment_fourthList_RecyclerViewId)

        viewAll_fourthList = view.findViewById(R.id.homeFragment_fourthList_viewAllId)

        viewAll_fourthList.setOnClickListener {
            var intent: Intent = Intent(requireContext() , ViewAllActivity::class.java)
            intent.putExtra("numberOfLists" , "Fourth")
            intent.putExtra("Title" , "GOAT Series to Start Now")
            startActivity(intent)
        }

        fourthListList = ArrayList()

        var myRef = singletonOfFirebase.getData()!!.child("GOAT Series to Start Now")
        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (dataSnapshot: DataSnapshot in snapshot.children) {
                    var id: String = dataSnapshot.key!!  // this is the Id ( 0 , 1 , 2 , 3 ...)

                    var movieName: String = dataSnapshot.child("Name").value.toString()
                    getRightMoviesFromAllMSGData_fromFB_FourthList(movieName)

                    if (id.equals("9")) {
                        break
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }
    private fun getRightMoviesFromAllMSGData_fromFB_FourthList(movieName: String) {
        var reference: DatabaseReference = singletonOfFirebase.getData()!!.child("allMsgData")
        fourthListList.clear()
        reference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for ( dataSnapshot: DataSnapshot in snapshot.children ) {
                    var id: String = dataSnapshot.key!!
                    var Name: String = dataSnapshot.child("Name").value.toString()
                    if(movieName.equals(Name) ) {
                        var modelFourthList: ModelOfFourthList = ModelOfFourthList()

                        modelFourthList.picture = dataSnapshot.child("Picture").value.toString()
                        modelFourthList.name = Name
                        modelFourthList.year = dataSnapshot.child("Year").value.toString()
                        modelFourthList.duration = dataSnapshot.child("Duration").value.toString()
                        modelFourthList.rating = dataSnapshot.child("Rating").value.toString()
                        modelFourthList.description = dataSnapshot.child("Description").value.toString()
                        modelFourthList.type = dataSnapshot.child("Type").value.toString()
                        modelFourthList.trailerUrl = dataSnapshot.child("Trailer").value.toString()

                        fourthListList.add(modelFourthList)
                    }
                    // Update the RecyclerView here, after fetching all movies
                    updateRecyclerView_FourthList()
                }
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }
    private fun updateRecyclerView_FourthList() {
        context?.let {
            adapterOfFourthList = AdapterOfFourthList(it, fourthListList)
            recyclerViewOfFourthList.layoutManager = LinearLayoutManager(context , LinearLayoutManager.HORIZONTAL , false)
            recyclerViewOfFourthList.setHasFixedSize(true)
            recyclerViewOfFourthList.adapter = adapterOfFourthList
            adapterOfFourthList.notifyDataSetChanged()
        }
    }

    private fun thirdListProcess() {
        //Initialize the variables
        recyclerViewOfThirdList = view.findViewById(R.id.homeFragment_thirdList_RecyclerViewId)

        thirdListList = ArrayList()

        var query: Query = singletonOfFirebase.getData()!!.child("Best Masterpiece Makers")
        thirdListList.clear()
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (snapshot in dataSnapshot.children) {
                    var modelOfThirdList = ModelOfThirdList()

                    modelOfThirdList.idOfDirector = snapshot.key!!
                    modelOfThirdList.directorName = snapshot.child("DirectorName").value.toString()
                    modelOfThirdList.directorPicture = snapshot.child("DirectorPicture").value.toString()
                    modelOfThirdList.whoIsHe = snapshot.child("WhoIsHe").value.toString()
                    modelOfThirdList.directorAwards = snapshot.child("DirectorAwards").value.toString()
                    modelOfThirdList.birthDate = snapshot.child("BirthDate").value.toString()
                    modelOfThirdList.nationality = snapshot.child("Nationality").value.toString()

                    thirdListList.add(modelOfThirdList)
                }

                context?.let {
                    adapterOfThirdList = AdapterOfThirdList(it, thirdListList)
                    recyclerViewOfThirdList.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                    recyclerViewOfThirdList.setHasFixedSize(true)
                    recyclerViewOfThirdList.adapter = adapterOfThirdList
                    adapterOfThirdList.notifyDataSetChanged()
                }
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    // For Second List ---------------------------------------------------------------------------------
    private fun secondListProcess() {
        //Initialize the variables
        recyclerViewOfSecondList = view.findViewById(R.id.homeFragment_secondList_RecyclerViewId)

        secondListList = ArrayList()

        var myRef = singletonOfFirebase.getData()!!.child("9 Most Popular Movies Right Now")
        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (dataSnapshot: DataSnapshot in snapshot.children) {
                    var id: String = dataSnapshot.key!!  // this is the Id ( 0 , 1 , 2 , 3 ...)

                    var movieName: String = dataSnapshot.child("Name").value.toString()
                    var movieRank: String = dataSnapshot.child("Rank").value.toString()

                    getRightMoviesFromAllMSGData_fromFB_SecondList(movieName , movieRank)
                }
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }
    private fun getRightMoviesFromAllMSGData_fromFB_SecondList(movieName: String , movieRank: String) {
        var reference: DatabaseReference = singletonOfFirebase.getData()!!.child("allMsgData")
        secondListList.clear()
        reference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for ( dataSnapshot: DataSnapshot in snapshot.children ) {
                    var id: String = dataSnapshot.key!!
                    var Name: String = dataSnapshot.child("Name").value.toString()
                    if(movieName.equals(Name) ) {
                        var modelOfSecondList: ModelOfSecondList = ModelOfSecondList()

                        modelOfSecondList.picture = dataSnapshot.child("Picture").value.toString()
                        modelOfSecondList.name = Name
                        modelOfSecondList.rank = movieRank
                        modelOfSecondList.year = dataSnapshot.child("Year").value.toString()
                        modelOfSecondList.duration = dataSnapshot.child("Duration").value.toString()
                        modelOfSecondList.rating = dataSnapshot.child("Rating").value.toString()
                        modelOfSecondList.description = dataSnapshot.child("Description").value.toString()
                        modelOfSecondList.type = dataSnapshot.child("Type").value.toString()
                        modelOfSecondList.trailerUrl = dataSnapshot.child("Trailer").value.toString()

                        secondListList.add(modelOfSecondList)
                    }
                    // Update the RecyclerView here, after fetching all movies
                    updateRecyclerView_SecondList()
                }
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }
    private fun updateRecyclerView_SecondList() {
        context?.let {
            adapterOfSecondList = AdapterOfSecondList(it, secondListList)
            recyclerViewOfSecondList.layoutManager = LinearLayoutManager(context , LinearLayoutManager.HORIZONTAL , false)
            recyclerViewOfSecondList.setHasFixedSize(true)
            recyclerViewOfSecondList.adapter = adapterOfSecondList
            adapterOfSecondList.notifyDataSetChanged()
        }
    }

    // For First List ---------------------------------------------------------------------------------
    private fun firstListProcess() {
        //Initialize the variables
        recyclerViewOfFirstList = view.findViewById(R.id.homeFragment_firstList_RecyclerViewId)
        viewAll_firstList = view.findViewById(R.id.homeFragment_firstList_viewAllId)

        viewAll_firstList.setOnClickListener {
            var intent: Intent = Intent(requireContext() , ViewAllActivity::class.java)
            intent.putExtra("numberOfLists" , "First")
            intent.putExtra("Title" , "What to watch tonight ?")
            startActivity(intent)
        }

        firstListList = ArrayList()

        var myRef = singletonOfFirebase.getData()!!.child("What to Watch Tonight")
        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (dataSnapshot: DataSnapshot in snapshot.children) {
                    var id: String = dataSnapshot.key!!  // this is the Id ( 0 , 1 , 2 , 3 ...)

                    var movieName: String = dataSnapshot.child("Name").value.toString()
                    getRightMoviesFromAllMSGData_fromFB_FirstList(movieName)

                    if (id.equals("9")) {
                        break
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }
    private fun getRightMoviesFromAllMSGData_fromFB_FirstList(movieName: String) {
        var reference: DatabaseReference = singletonOfFirebase.getData()!!.child("allMsgData")
        firstListList.clear()
        reference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for ( dataSnapshot: DataSnapshot in snapshot.children ) {
                    var id: String = dataSnapshot.key!!
                    var Name: String = dataSnapshot.child("Name").value.toString()
                    if(movieName.equals(Name) ) {
                        var modelFirstList: ModelOfFirstList = ModelOfFirstList()

                        modelFirstList.picture = dataSnapshot.child("Picture").value.toString()
                        modelFirstList.name = Name
                        modelFirstList.year = dataSnapshot.child("Year").value.toString()
                        modelFirstList.duration = dataSnapshot.child("Duration").value.toString()
                        modelFirstList.rating = dataSnapshot.child("Rating").value.toString()
                        modelFirstList.description = dataSnapshot.child("Description").value.toString()
                        modelFirstList.type = dataSnapshot.child("Type").value.toString()
                        modelFirstList.trailerUrl = dataSnapshot.child("Trailer").value.toString()

                        firstListList.add(modelFirstList)
                    }
                    // Update the RecyclerView here, after fetching all movies
                    updateRecyclerView_FirstList()
                }
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }
    private fun updateRecyclerView_FirstList() {
        context?.let {
            adapterOfFirstList = AdapterOfFirstList(it, firstListList)
            recyclerViewOfFirstList.layoutManager = LinearLayoutManager(context , LinearLayoutManager.HORIZONTAL , false)
            recyclerViewOfFirstList.setHasFixedSize(true)
            recyclerViewOfFirstList.adapter = adapterOfFirstList
            adapterOfFirstList.notifyDataSetChanged()
        }
    }

    // For Top MSG ---------------------------------------------------------------------------------
    private fun getDataFromFireBase_TopMSG() {
        topMSGList = ArrayList()
        //singletonOfFirebase.getData() - this from the Singleton Class
        var query: Query = singletonOfFirebase.getData()!!.child("home_topMSG")
        topMSGList.clear()
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (dataSnapshot in snapshot.children) {
                    var modelOfTopMSG: ModelOfTopMSG = ModelOfTopMSG()

                    modelOfTopMSG.picture = dataSnapshot.child("Picture").value.toString()
                    modelOfTopMSG.name = dataSnapshot.child("Name").value.toString()
                    modelOfTopMSG.year = dataSnapshot.child("Year").value.toString()
                    modelOfTopMSG.duration = dataSnapshot.child("Duration").value.toString()
                    modelOfTopMSG.rating = dataSnapshot.child("Rating").value.toString()
                    modelOfTopMSG.description = dataSnapshot.child("Description").value.toString()
                    modelOfTopMSG.type = dataSnapshot.child("Type").value.toString()
                    modelOfTopMSG.trailerUrl = dataSnapshot.child("Trailer").value.toString()

                    topMSGList.add(modelOfTopMSG)

                    // Hide the LoadingAnimation when data is loaded
                    loadingOfData.visibility = View.GONE
                }
                context?.let {
                    adapterOfTopMSG = AdapterOfTopMSG(it , topMSGList)
                    viewPager.adapter = adapterOfTopMSG
                    viewPager.setPadding(0 , 0 , 0 , 0)
                    adapterOfTopMSG.notifyDataSetChanged()

                    //The Indicator
                    circleIndicator = view.findViewById(R.id.homeFragment_indicatorId)
                    circleIndicator.setViewPager(viewPager)
                }
            }
            override fun onCancelled(error: DatabaseError) {}
        })

        // For watching
        playTrailerButton = view.findViewById(R.id.homeFragment_playTrailerButtonId)
        // Set a general click listener for the playTrailerButton
        playTrailerButton.setOnClickListener {
            // Get the position of the clicked item
            var position: Int = viewPager.currentItem
            playTrailerProcess(position)
        }
    }

    private fun playTrailerProcess(position: Int) {
        // Handle the click for the item at the captured position
        var trailerUrl: String = topMSGList.get(position).trailerUrl
        //Must remove the the first link (http://www.youtube.com/watch?v=) to get just the Id and For that we used Substring
        var url: String = ""
        if (trailerUrl.contains("https://www.youtube.com/")) {
            // Extract the video ID using substring
            var startIndex: Int = "https://www.youtube.com/watch?v=".length
            url = trailerUrl.substring(startIndex)
        }

        var intent: Intent = Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + url))
        // If the YouTube app is not installed, open the link in a web browser
        if (intent.resolveActivity(requireContext().packageManager) == null) {
            intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/watch?v=" + url))
        }
        startActivity(intent)
    }

}