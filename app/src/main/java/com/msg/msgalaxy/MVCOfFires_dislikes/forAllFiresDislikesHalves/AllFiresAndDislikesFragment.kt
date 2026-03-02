package com.msg.msgalaxy.MVCOfFires_dislikes.forAllFiresDislikesHalves

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

class AllFiresAndDislikesFragment : Fragment() {

    private lateinit var view: View
    private var recyclerViewOfAll: RecyclerView? = null
    private var adapter: AdapterOfAll? = null
    private var allList: ArrayList<ModelOfAll>? = null
    private var name: String? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        view = inflater.inflate(R.layout.tablayout_fires_fragment, container, false)

        //Initialize the variables
        recyclerViewOfAll = view.findViewById(R.id.tablayoutFiresFragment_recyclerViewId)

        recyclerViewOfAll!!.layoutManager = LinearLayoutManager(context)
        recyclerViewOfAll!!.setHasFixedSize(true)

        getDataFromFirebase()

        return view
    }

    private fun getDataFromFirebase() {
        allList = ArrayList()

        // Retrieve data
        name = PeopleWhoReactedActivityForMS.name

        getFires(name)
        getHalves(name)
        getDislikes(name)
    }

    private fun getFires(name: String?) {
        val reference1: DatabaseReference = FirebaseDatabase.getInstance().reference.child("Fires").child(name!!)
        reference1.addValueEventListener(object : ValueEventListener {
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
                                    val username: String = snapshot.child("username").getValue(String::class.java)!!

                                    // Send The Data
                                    var modelOfAll: ModelOfAll = ModelOfAll()
                                    modelOfAll.profilePic = profilePic
                                    modelOfAll.username = username
                                    //for Fire icon i will send the number 1
                                    modelOfAll.iconId = 1
                                    allList!!.add(modelOfAll)

                                    // Notify adapter after adding data
                                    adapter!!.notifyDataSetChanged()
                                }
                            }
                            override fun onCancelled(error: DatabaseError) {}
                        })
                    }
                    context?.let {
                        adapter = AdapterOfAll(it, allList!!)
                        recyclerViewOfAll!!.adapter = adapter
                        adapter!!.notifyDataSetChanged()
                    }
                    // recyclerViewOfComments.setVisibility(View.VISIBLE);  // Show RecyclerView
                    // noCommentsYet.setVisibility(View.GONE);  // Hide "noCommentYet" view
                }
                else {
                    // No Dislikes
                    // recyclerViewOfComments.setVisibility(View.GONE);  // Hide RecyclerView
                    // noCommentsYet.setVisibility(View.VISIBLE);  // Show "noCommentYet" view
                }

            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun getHalves(name: String?) {
        val reference1: DatabaseReference = FirebaseDatabase.getInstance().reference.child("Halves").child(name!!)
        reference1.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                if (snapshot.exists()) {
                    //There are Halves
                    for (dataSnapshot in snapshot.children) {
                        var userId: String? = dataSnapshot.key


                        // Get the Profile picture according to the userId
                        var userReference: DatabaseReference = FirebaseDatabase.getInstance().reference.child("Users").child(userId!!)
                        userReference.addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                if (snapshot.exists()) {
                                    var profilePic: String = snapshot.child("profilePic").getValue(String::class.java)!!
                                    val username: String = snapshot.child("username").getValue(String::class.java)!!

                                    // Send The Data
                                    var modelOfAll: ModelOfAll = ModelOfAll()
                                    modelOfAll.profilePic = profilePic
                                    modelOfAll.username = username
                                    //for Half icon i will send the number 2
                                    modelOfAll.iconId = 2
                                    allList!!.add(modelOfAll)

                                    // Notify adapter after adding data
                                    adapter!!.notifyDataSetChanged()
                                }
                            }
                            override fun onCancelled(error: DatabaseError) {}
                        })
                    }
                    context?.let {
                        adapter = AdapterOfAll(it, allList!!)
                        recyclerViewOfAll!!.adapter = adapter
                        adapter!!.notifyDataSetChanged()
                    }
                    // recyclerViewOfComments.setVisibility(View.VISIBLE);  // Show RecyclerView
                    // noCommentsYet.setVisibility(View.GONE);  // Hide "noCommentYet" view
                }
                else {
                    // No Dislikes
                    // recyclerViewOfComments.setVisibility(View.GONE);  // Hide RecyclerView
                    // noCommentsYet.setVisibility(View.VISIBLE);  // Show "noCommentYet" view
                }

            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun getDislikes(name: String?) {
        var reference: DatabaseReference = FirebaseDatabase.getInstance().reference.child("Dislikes").child(name!!)
        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    //There are Dislikes
                    for (dataSnapshot in snapshot.children) {

                        var userId: String? = dataSnapshot.key


                        // Get the Profile picture according to the userId
                        var userReference: DatabaseReference = FirebaseDatabase.getInstance().reference.child("Users").child(userId!!)
                        userReference.addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                if (snapshot.exists()) {
                                    var profilePic: String = snapshot.child("profilePic").getValue(String::class.java)!!
                                    val username: String = snapshot.child("username").getValue(String::class.java)!!

                                    // Send The Data
                                    var modelOfAll: ModelOfAll = ModelOfAll()

                                    modelOfAll.profilePic = profilePic
                                    modelOfAll.username = username
                                    //for Dislike icon i will send the number 3
                                    modelOfAll.iconId = 3
                                    allList!!.add(modelOfAll)

                                    // Notify adapter after adding data
                                    adapter!!.notifyDataSetChanged()
                                }
                            }
                            override fun onCancelled(error: DatabaseError) {/* Handle error */}
                        })
                    }
                    context?.let {
                        adapter = AdapterOfAll(it, allList!!)
                        recyclerViewOfAll!!.adapter = adapter
                        adapter!!.notifyDataSetChanged()
                    }
                    // recyclerViewOfComments.setVisibility(View.VISIBLE);  // Show RecyclerView
                    //  noCommentsYet.setVisibility(View.GONE);  // Hide "noCommentYet" view
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