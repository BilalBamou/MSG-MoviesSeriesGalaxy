package com.msg.msgalaxy.MVCOfFires_dislikes.forFires

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.msg.msgalaxy.MVCOfFires_dislikes.PeopleWhoReactedActivityForMS
import com.msg.msgalaxy.R

class FiresFragment : Fragment() {

    private lateinit var view: View
    private var recyclerViewOfFires: RecyclerView? = null
    private var adapter: AdapterOfFires? = null
    private var firesList: ArrayList<ModelOfFires>? = null
    private var name: String? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        view = inflater.inflate(R.layout.tablayout_fires_fragment, container, false)

        //Initialize the variables
        recyclerViewOfFires = view.findViewById(R.id.tablayoutFiresFragment_recyclerViewId)

        recyclerViewOfFires!!.layoutManager = LinearLayoutManager(context)
        recyclerViewOfFires!!.setHasFixedSize(true)

        getDataFromFirebase()

        return view
    }

    private fun getDataFromFirebase() {
        firesList = ArrayList()

        // Retrieve data
        name = PeopleWhoReactedActivityForMS.name

        var reference: DatabaseReference = FirebaseDatabase.getInstance().reference.child("Fires").child(name!!)
        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    //There are Fires
                    for (dataSnapshot in snapshot.children) {

                        var userId: String? = dataSnapshot.key


                        // Get the Profile picture according to the userId
                        var userReference: DatabaseReference = FirebaseDatabase.getInstance().reference.child("Users").child(userId!!)
                        userReference.addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                if (snapshot.exists()) {
                                    var profilePic: String = snapshot.child("profilePic").getValue(String::class.java)!!
                                    var username: String = snapshot.child("username").getValue(String::class.java)!!
                                    // Send The Data
                                    var modelOfFires: ModelOfFires = ModelOfFires()
                                    modelOfFires.profilePic = profilePic
                                    modelOfFires.username = username
                                    firesList!!.add(modelOfFires)

                                    // Notify adapter after adding data
                                    adapter!!.notifyDataSetChanged()
                                }
                            }
                            override fun onCancelled(error: DatabaseError) {/* Handle error */ }
                        })
                    }
                    context?.let {
                        adapter = AdapterOfFires(it, firesList!!)
                        recyclerViewOfFires!!.adapter = adapter
                        adapter!!.notifyDataSetChanged()
                    }
                    // recyclerViewOfComments.setVisibility(View.VISIBLE);  // Show RecyclerView
                    // noCommentsYet.setVisibility(View.GONE);  // Hide "noCommentYet" view
                } else {
                    // No Dislikes
                    // recyclerViewOfComments.setVisibility(View.GONE);  // Hide RecyclerView
                    // noCommentsYet.setVisibility(View.VISIBLE);  // Show "noCommentYet" view
                }
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }
}