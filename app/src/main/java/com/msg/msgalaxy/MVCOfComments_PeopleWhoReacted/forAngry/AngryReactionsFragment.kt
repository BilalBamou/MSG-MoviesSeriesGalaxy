package com.msg.msgalaxy.MVCOfComments_PeopleWhoReacted.forAngry

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

class AngryReactionsFragment: Fragment() {

    private lateinit var view: View
    private var recyclerView: RecyclerView? = null
    private var adapter: AdapterOfAngry? = null
    private var list: ArrayList<ModelOfAngry>? = null
    private var name: String? = null
    private var commentId: String? = null
    private lateinit var reference: DatabaseReference

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        view = inflater.inflate(R.layout.people_who_reacted_tablayout_items_recyclerview, container, false)

        //Initialize the variables
        recyclerView = view.findViewById(R.id.peopleWhoReacted_ItemsRecyclerView_recyclerViewId)

        recyclerView!!.layoutManager = LinearLayoutManager(context)
        recyclerView!!.setHasFixedSize(true)

        getDataFromFirebase()

        return view
    }

    private fun getDataFromFirebase() {
        list = ArrayList()

        // Retrieve data
        name = PeopleWhoReactedActivity.name
        commentId = PeopleWhoReactedActivity.commentId

        reference = FirebaseDatabase.getInstance().reference.child("CommentsReactions")
            .child(name!!).child(commentId!!)

        getAngryOfComment()
    }

    private fun getAngryOfComment() {
        val reference1: DatabaseReference = reference.child("Angry")
        reference1.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    //There are Angry reacts
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
                                    var modelOfAngry: ModelOfAngry = ModelOfAngry()
                                    modelOfAngry.profilePic = profilePic
                                    modelOfAngry.username = username
                                    list!!.add(modelOfAngry)

                                    // Notify adapter after adding data
                                    adapter!!.notifyDataSetChanged()
                                }
                            }
                            override fun onCancelled(error: DatabaseError) {}
                        })
                    }
                    context?.let {
                        adapter = AdapterOfAngry(it, list!!)
                        recyclerView!!.adapter = adapter
                        adapter!!.notifyDataSetChanged()
                    }
                }
                else {
                    // No Angry reacts
                }
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }


}