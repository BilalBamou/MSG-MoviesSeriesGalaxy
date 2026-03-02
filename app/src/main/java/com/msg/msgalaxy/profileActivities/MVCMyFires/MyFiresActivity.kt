package com.msg.msgalaxy.profileActivities.MVCMyFires

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
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
import com.msg.msgalaxy.profileActivities.MVCMyList.AdapterOfMyList
import com.msg.msgalaxy.profileActivities.MVCMyList.ModelOfMyList
import java.util.Collections

class MyFiresActivity : AppCompatActivity() {

    private var recyclerViewOfMyFires: RecyclerView? = null

    private var firebaseAuth: FirebaseAuth? = null
    private var adapter: AdapterOfMyFires? = null

    private var listOfMyFires: ArrayList<ModelOfMyFires>? = null
    private var myFiresIsEmpty: LinearLayout? = null

    private var clearMyFiresBtn: CardView? = null
    private var title: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_fires)

        recyclerViewWithAdapterProcess()

        clearMyFiresProcess()

        //Arrow Btn From MyList to Profile Fragment
        arrowBack()
    }

    private fun recyclerViewWithAdapterProcess() {
        //Initialize the variables
        myFiresIsEmpty = findViewById(R.id.myFiresActivity_myFiresIsEmpty_linearLayoutId)
        title = findViewById(R.id.myFiresActivity_titleId)
        recyclerViewOfMyFires = findViewById(R.id.myFiresActivity_recyclerViewId)
        var layoutManager: GridLayoutManager = GridLayoutManager(this, 3)
        recyclerViewOfMyFires!!.layoutManager = layoutManager
        recyclerViewOfMyFires!!.setHasFixedSize(true)
        recyclerViewOfMyFires!!.itemAnimator = DefaultItemAnimator()

        listOfMyFires = ArrayList()

        getMyFiresFromFB()
    }

    private fun getMyFiresFromFB() {
        firebaseAuth = FirebaseAuth.getInstance()
        var firebaseUser: FirebaseUser = firebaseAuth!!.currentUser!!
        var reference = FirebaseDatabase.getInstance().reference.child("Fires")

        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                listOfMyFires!!.clear() // Clear the list to avoid duplicates when onDataChange is called multiple times
                if (snapshot.exists()) {
                    // There are movies in Fires Column who did some user Fire to them
                    for (dataSnapshot: DataSnapshot in snapshot.children) {
                        var nameOfMovieOrSerie: String = dataSnapshot.key!! // Get the name from the key

                        for(userSnapshot: DataSnapshot in dataSnapshot.children) {
                            var userId: String = userSnapshot.key!! // Get the userId from the key
                            if(userId.equals(firebaseUser.uid)) {

                                var picture = dataSnapshot.child(firebaseUser.uid).child("picture").getValue(String::class.java)
                                var date = dataSnapshot.child(firebaseUser.uid).child("timestamp").getValue(String::class.java)

                                var modelOfMyFires = ModelOfMyFires()
                                modelOfMyFires.name = nameOfMovieOrSerie
                                modelOfMyFires.picture = picture!!
                                modelOfMyFires.date = date!!
                                listOfMyFires!!.add(modelOfMyFires)

                                // Order all movies or series by timestamp
                                Collections.sort(listOfMyFires, object : Comparator<ModelOfMyFires?> {
                                    override fun compare(o1: ModelOfMyFires?, o2: ModelOfMyFires?): Int {
                                        return o2!!.date.compareTo(o1!!.date)
                                    }
                                })

                                adapter = AdapterOfMyFires(this@MyFiresActivity, listOfMyFires!!)
                                recyclerViewOfMyFires!!.adapter = adapter
                                adapter!!.notifyDataSetChanged()
                                recyclerViewOfMyFires!!.visibility = View.VISIBLE // Show RecyclerView
                                title!!.visibility = View.VISIBLE
                                myFiresIsEmpty!!.visibility = View.GONE // Hide

                                title!!.text = "Movies & Series : ${listOfMyFires!!.size}"
                            }
                           if(listOfMyFires!!.isEmpty()) {
                                // No Movies Or Series did this user Fire to them
                                recyclerViewOfMyFires!!.visibility = View.GONE // Hide RecyclerView
                                title!!.visibility = View.GONE
                                myFiresIsEmpty!!.visibility = View.VISIBLE // Show "myListIsEmpty" view
                            }
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun clearMyFiresProcess() {
        //Initialize the variables
        clearMyFiresBtn = findViewById(R.id.myFiresActivity_clearMyFiresId)

        firebaseAuth = FirebaseAuth.getInstance()
        var firebaseUser: FirebaseUser = firebaseAuth!!.currentUser!!

        clearMyFiresBtn!!.setOnClickListener {
            var reference: DatabaseReference = FirebaseDatabase.getInstance().reference.child("Fires")
            reference.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (! listOfMyFires!!.isEmpty()) {
                        var alertDialogBuilder = AlertDialog.Builder(this@MyFiresActivity)
                            .setCancelable(false)
                            .setTitle("CLEAR MY FIRES")
                            .setMessage("Are you sure you want to clear your fires ?")
                            .setPositiveButton("CLEAR") { dialogInterface, i ->

                                // Remove the user's entry in each movie
                                for (movieSnapshot: DataSnapshot in snapshot.children) {
                                    movieSnapshot.child(firebaseUser.uid).ref.removeValue()
                                }

                                listOfMyFires!!.clear()

                                adapter!!.notifyDataSetChanged()

                                Toast.makeText(this@MyFiresActivity, "Your fires has been cleared.", Toast.LENGTH_SHORT).show()

                                recyclerViewOfMyFires!!.visibility = View.GONE // Hide RecyclerView
                                title!!.visibility = View.GONE
                                myFiresIsEmpty!!.visibility = View.VISIBLE // Show "myListIsEmpty" view
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

                    }
                    else {
                        Toast.makeText(this@MyFiresActivity, "Your fires is already empty.", Toast.LENGTH_SHORT).show()
                    }
                }
                override fun onCancelled(error: DatabaseError) {}
            })
        }
    }

    private fun arrowBack() {
        var arrowBack: CardView = findViewById(R.id.myFiresActivity_arrowBackId) as CardView
        arrowBack.setOnClickListener {
            //For Back from Activity to Fragment !!!!!
            onBackPressed()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }
}