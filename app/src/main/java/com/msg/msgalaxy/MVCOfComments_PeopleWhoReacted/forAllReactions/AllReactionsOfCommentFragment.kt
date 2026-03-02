package com.msg.msgalaxy.MVCOfComments_PeopleWhoReacted.forAllReactions

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
import com.msg.msgalaxy.MVCOfComments_PeopleWhoReacted.PeopleWhoReactedActivity
import com.msg.msgalaxy.R

class AllReactionsOfCommentFragment : Fragment() {

    private lateinit var view: View
    private var recyclerViewOfAll: RecyclerView? = null
    private var adapter: AdapterOfAll? = null
    private var allList: ArrayList<ModelOfAll>? = null
    private var name: String? = null
    private var commentId: String? = null
    private lateinit var reference: DatabaseReference

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        view = inflater.inflate(R.layout.people_who_reacted_tablayout_items_recyclerview, container, false)

        //Initialize the variables
        recyclerViewOfAll = view.findViewById(R.id.peopleWhoReacted_ItemsRecyclerView_recyclerViewId)

        recyclerViewOfAll!!.layoutManager = LinearLayoutManager(context)
        recyclerViewOfAll!!.setHasFixedSize(true)

        getDataFromFirebase()

        return view
    }
    private fun getDataFromFirebase() {
        allList = ArrayList()

        // Retrieve data
        name = PeopleWhoReactedActivity.name
        commentId = PeopleWhoReactedActivity.commentId

         reference = FirebaseDatabase.getInstance().reference.child("CommentsReactions")
            .child(name!!).child(commentId!!)

        getLikesOfComment()
        getLovesOfComment()
        getLaughsOfComment()
        getAngryOfComment()
    }

    private fun getLikesOfComment() {
        val reference1: DatabaseReference = reference.child("Likes")
        reference1.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    //There are Likes
                    for (dataSnapshot in snapshot.children) {
                        var userId: String? = dataSnapshot.key

                        // Get the Profile picture according to the userId
                        var userReference: DatabaseReference = FirebaseDatabase.getInstance().reference.child("Users").child(userId!!)
                        userReference.addListenerForSingleValueEvent(object :
                            ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                if (snapshot.exists()) {
                                    var profilePic: String = snapshot.child("profilePic").getValue(String::class.java)!!
                                    val username: String = snapshot.child("username").getValue(String::class.java)!!

                                    // Send The Data
                                    var modelOfAll: ModelOfAll = ModelOfAll()
                                    modelOfAll.profilePic = profilePic
                                    modelOfAll.username = username
                                    //for Like icon i will send the number 1
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
                }
                else {
                    // No Likes
                }

            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }
    private fun getLovesOfComment() {
        val reference1: DatabaseReference = reference.child("Loves")
        reference1.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    //There are Loves
                    for (dataSnapshot in snapshot.children) {
                        var userId: String? = dataSnapshot.key

                        // Get the Profile picture according to the userId
                        var userReference: DatabaseReference = FirebaseDatabase.getInstance().reference.child("Users").child(userId!!)
                        userReference.addListenerForSingleValueEvent(object :
                            ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                if (snapshot.exists()) {
                                    var profilePic: String = snapshot.child("profilePic").getValue(String::class.java)!!
                                    val username: String = snapshot.child("username").getValue(String::class.java)!!

                                    // Send The Data
                                    var modelOfAll: ModelOfAll = ModelOfAll()
                                    modelOfAll.profilePic = profilePic
                                    modelOfAll.username = username
                                    //for Love icon i will send the number 2
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
                }
                else {
                    // No Loves
                }

            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }
    private fun getLaughsOfComment() {
        val reference1: DatabaseReference = reference.child("Laughs")
        reference1.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    //There are Laughs
                    for (dataSnapshot in snapshot.children) {
                        var userId: String? = dataSnapshot.key

                        // Get the Profile picture according to the userId
                        var userReference: DatabaseReference = FirebaseDatabase.getInstance().reference.child("Users").child(userId!!)
                        userReference.addListenerForSingleValueEvent(object :
                            ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                if (snapshot.exists()) {
                                    var profilePic: String = snapshot.child("profilePic").getValue(String::class.java)!!
                                    val username: String = snapshot.child("username").getValue(String::class.java)!!

                                    // Send The Data
                                    var modelOfAll: ModelOfAll = ModelOfAll()
                                    modelOfAll.profilePic = profilePic
                                    modelOfAll.username = username
                                    //for Laugh icon i will send the number 3
                                    modelOfAll.iconId = 3
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
                }
                else {
                    // No Laughs
                }

            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }
    private fun getAngryOfComment() {
        val reference1: DatabaseReference = reference.child("Angry")
        reference1.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    //There are Angry reactions
                    for (dataSnapshot in snapshot.children) {
                        var userId: String? = dataSnapshot.key

                        // Get the Profile picture according to the userId
                        var userReference: DatabaseReference = FirebaseDatabase.getInstance().reference.child("Users").child(userId!!)
                        userReference.addListenerForSingleValueEvent(object :
                            ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                if (snapshot.exists()) {
                                    var profilePic: String = snapshot.child("profilePic").getValue(String::class.java)!!
                                    val username: String = snapshot.child("username").getValue(String::class.java)!!

                                    // Send The Data
                                    var modelOfAll: ModelOfAll = ModelOfAll()
                                    modelOfAll.profilePic = profilePic
                                    modelOfAll.username = username
                                    //for Angry icon i will send the number 4
                                    modelOfAll.iconId = 4
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
                }
                else {
                    // No Angry reactions
                }

            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }
}