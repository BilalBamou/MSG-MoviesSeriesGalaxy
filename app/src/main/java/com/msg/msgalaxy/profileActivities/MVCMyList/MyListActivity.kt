package com.msg.msgalaxy.profileActivities.MVCMyList

import androidx.appcompat.app.AlertDialog
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.msg.msgalaxy.R
import java.util.Collections

class MyListActivity : AppCompatActivity() {

    private var recyclerViewOfMyList: RecyclerView? = null

    private var firebaseAuth: FirebaseAuth? = null
    private var adapter: AdapterOfMyList? = null

    private var listOfMyList: ArrayList<ModelOfMyList>? = null
    private var myListIsEmpty: LinearLayout? = null

    private var clearMyListBtn: CardView? = null
    private var title: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_list)

        recyclerViewWithAdapterProcess()

        clearMyListProcess()

        //Arrow Btn From MyList to Profile Fragment
        arrowBack()
    }

    private fun recyclerViewWithAdapterProcess() {
        //Initialize the variables
        myListIsEmpty = findViewById(R.id.myListActivity_myListIsEmpty_linearLayoutId)
        title = findViewById(R.id.myListActivity_titleId)
        recyclerViewOfMyList = findViewById(R.id.myListActivity_recyclerViewId)
        var layoutManager: GridLayoutManager = GridLayoutManager(this, 3)
        recyclerViewOfMyList!!.layoutManager = layoutManager
        recyclerViewOfMyList!!.setHasFixedSize(true)
        recyclerViewOfMyList!!.itemAnimator = DefaultItemAnimator()

        listOfMyList = ArrayList()

        getMyListFromFB()
    }

    private fun getMyListFromFB() {
        firebaseAuth = FirebaseAuth.getInstance()
        var firebaseUser: FirebaseUser = firebaseAuth!!.currentUser!!
        var reference =
            FirebaseDatabase.getInstance().reference.child("MyList").child(firebaseUser!!.uid)

        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                listOfMyList!!.clear() // Clear the list to avoid duplicates when onDataChange is called multiple times
                if (snapshot.exists()) {
                    // There are movies in my list
                    for (dataSnapshot: DataSnapshot in snapshot.children) {
                        var nameOfMovieOrSerie: String =
                            dataSnapshot.key!! // Get the name from the key
                        var picture = dataSnapshot.child("picture").getValue(String::class.java)
                        var date = dataSnapshot.child("timestamp").getValue(String::class.java)

                        var modelOfMyList = ModelOfMyList()
                        modelOfMyList.name = nameOfMovieOrSerie
                        modelOfMyList.picture = picture!!
                        modelOfMyList.date = date!!
                        listOfMyList!!.add(modelOfMyList)
                    }
                    // Order all movies or series by timestamp
                    Collections.sort(listOfMyList, object : Comparator<ModelOfMyList?> {
                        override fun compare(o1: ModelOfMyList?, o2: ModelOfMyList?): Int {
                            return o2!!.date.compareTo(o1!!.date)
                        }
                    })

                    adapter = AdapterOfMyList(this@MyListActivity, listOfMyList!!)
                    recyclerViewOfMyList!!.adapter = adapter
                    adapter!!.notifyDataSetChanged()
                    recyclerViewOfMyList!!.visibility = View.VISIBLE // Show RecyclerView
                    title!!.visibility = View.VISIBLE
                    myListIsEmpty!!.visibility = View.GONE // Hide "noCommentYet" view

                    title!!.text = "Movies & Series : ${listOfMyList!!.size}"
                } else {
                    // No Movies Or Series in My List
                    recyclerViewOfMyList!!.visibility = View.GONE // Hide RecyclerView
                    title!!.visibility = View.GONE
                    myListIsEmpty!!.visibility = View.VISIBLE // Show "myListIsEmpty" view
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun clearMyListProcess() {
        //Initialize the variables
        clearMyListBtn = findViewById(R.id.myListActivity_clearMyListId)

        firebaseAuth = FirebaseAuth.getInstance()
        var firebaseUser: FirebaseUser = firebaseAuth!!.currentUser!!
        clearMyListBtn!!.setOnClickListener {
            var reference: DatabaseReference = FirebaseDatabase.getInstance().reference.child("MyList").child(firebaseUser.uid)
            reference.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        var alertDialogBuilder = AlertDialog.Builder(this@MyListActivity)
                            .setCancelable(false)
                            .setTitle("CLEAR MY LIST")
                            .setMessage("Are you sure you want to clear your list ?")
                            .setPositiveButton("CLEAR") { dialogInterface, i ->

                                // Remove all movies at once
                                reference.removeValue()

                                listOfMyList!!.clear()

                                adapter!!.notifyDataSetChanged()

                                Toast.makeText(this@MyListActivity, "Your list has been cleared.", Toast.LENGTH_SHORT).show()

                            }
                            .setNegativeButton("Cancel") { dialogInterface, i ->
                                dialogInterface.cancel()
                            }

                        var alertDialog: AlertDialog = alertDialogBuilder.create()
                        // Change The Color of LOG OUT Btn
                        alertDialog.setOnShowListener { dialog ->
                            var positiveButton = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)
                            positiveButton.setTextColor(Color.parseColor("#FF002E"))
                            val negativeButton = alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE)
                            negativeButton.setTextColor(Color.parseColor("#F0F3F8"))
                        }
                        alertDialog.show()
                    } else {
                        Toast.makeText(this@MyListActivity, "Your list is already empty.", Toast.LENGTH_SHORT).show()
                    }
                }
                override fun onCancelled(error: DatabaseError) {}
            })
        }
    }

    private fun arrowBack() {
        var arrowBack: CardView = findViewById(R.id.myListActivity_arrowBackId) as CardView
        arrowBack.setOnClickListener {
            //For Back from Activity to Fragment !!!!!
            onBackPressed()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }
}