package com.msg.msgalaxy.MVCOfViewAll

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.msg.msgalaxy.R

class ViewAllActivity : AppCompatActivity() {

    private var recyclerViewOfViewAll: RecyclerView? = null
    private var adapter: AdapterOfViewAll? = null
    private var listOfViewAll: ArrayList<ModelOfViewAll>? = null
    private var titleTxtView: TextView? = null
    private var dbRef: DatabaseReference = FirebaseDatabase.getInstance().reference

    companion object {
        var numberOfLists: String? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_all)

        recyclerViewWithAdapterProcess()

        arrowBack()
    }

    private fun recyclerViewWithAdapterProcess() {
        //Initialize the variables
        recyclerViewOfViewAll = findViewById(R.id.viewAllActivity_recyclerViewId)
        titleTxtView = findViewById(R.id.viewAllActivity_title)

        var bundle = intent.extras
        numberOfLists = bundle!!.getString("numberOfLists")
        var title = bundle.getString("Title")

        titleTxtView!!.text = title

        listOfViewAll = ArrayList()

        if (numberOfLists.equals("First")) {
            // What to watch tonight ?
            getMyListFromFB_FirstList()
        }
        else if (numberOfLists.equals("Fourth")) {
            // GOAT Series to Start Now
            getMyListFromFB_FourthList()
        }
        else if (numberOfLists.equals("Fifth")) {
            // Oscar Winning Masterpieces
            getMyListFromFB_FifthList()
        }
    }

    private fun getMyListFromFB_FirstList() {
        var myRef = dbRef.child("What to Watch Tonight")
        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (dataSnapshot: DataSnapshot in snapshot.children) {
                    var id: String = dataSnapshot.key!!  // this is the Id ( 0 , 1 , 2 , 3 ...)

                    var movieName: String = dataSnapshot.child("Name").value.toString()
                    getRightMoviesFromAllMSGData_fromFB(movieName)

                    // Update the RecyclerView here, after fetching all movies
                    updateRecyclerView()
                }
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun getMyListFromFB_FourthList() {
        var myRef = dbRef.child("GOAT Series to Start Now")
        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (dataSnapshot: DataSnapshot in snapshot.children) {
                    var id: String = dataSnapshot.key!!  // this is the Id ( 0 , 1 , 2 , 3 ...)

                    var movieName: String = dataSnapshot.child("Name").value.toString()
                    getRightMoviesFromAllMSGData_fromFB(movieName)

                    // Update the RecyclerView here, after fetching all movies
                    updateRecyclerView()
                }
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun getMyListFromFB_FifthList() {
        var myRef = dbRef.child("Oscar Winning Masterpieces")
        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (dataSnapshot: DataSnapshot in snapshot.children) {
                    var id: String = dataSnapshot.key!!  // this is the Id ( 0 , 1 , 2 , 3 ...)

                    var movieName: String = dataSnapshot.child("Name").value.toString()
                    getRightMoviesFromAllMSGData_fromFB(movieName)
                }
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun getRightMoviesFromAllMSGData_fromFB(movieName: String) {
        var reference: DatabaseReference = dbRef.child("allMsgData")
        listOfViewAll!!.clear()
        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for ( dataSnapshot: DataSnapshot in snapshot.children ) {
                    var id: String = dataSnapshot.key!!
                    var Name: String = dataSnapshot.child("Name").getValue(String::class.java)!!
                    if(movieName.equals(Name) ) {
                        addToModelViewAll(dataSnapshot)
                    }
                    // Update the RecyclerView here, after fetching all movies
                    updateRecyclerView()
                }
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun addToModelViewAll(snapshot: DataSnapshot) {
        var modelOfViewAll: ModelOfViewAll = ModelOfViewAll()

        modelOfViewAll.picture = snapshot.child("Picture").value.toString()
        modelOfViewAll.name = snapshot.child("Name").value.toString()
        modelOfViewAll.year = snapshot.child("Year").value.toString()
        modelOfViewAll.duration = snapshot.child("Duration").value.toString()
        modelOfViewAll.rating = snapshot.child("Rating").value.toString()
        modelOfViewAll.description = snapshot.child("Description").value.toString()
        modelOfViewAll.type = snapshot.child("Type").value.toString()
        modelOfViewAll.trailerUrl = snapshot.child("Trailer").value.toString()

        listOfViewAll!!.add(modelOfViewAll)
    }

    // I Moved this method outside the loop to update the RecyclerView only once
    private fun updateRecyclerView() {
        this.let {
            adapter = AdapterOfViewAll(it, listOfViewAll!!)
            var layoutManager: GridLayoutManager = GridLayoutManager(this, 3)
            recyclerViewOfViewAll!!.layoutManager = layoutManager
            recyclerViewOfViewAll!!.setHasFixedSize(true)
            recyclerViewOfViewAll!!.itemAnimator = DefaultItemAnimator()
            recyclerViewOfViewAll!!.adapter = adapter
            adapter!!.notifyDataSetChanged()
        }
    }

    private fun arrowBack() {
        var arrowBack: CardView = findViewById(R.id.viewAllActivity_arrowBackId)
        arrowBack.setOnClickListener {
            //For Back from Activity to Fragment !!!!!
            onBackPressed()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }
}