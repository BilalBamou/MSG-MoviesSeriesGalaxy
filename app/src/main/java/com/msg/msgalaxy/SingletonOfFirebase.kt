package com.msg.msgalaxy

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class SingletonOfFirebase() {

    private var myReference: DatabaseReference? = null
    companion object {
        private var instance: SingletonOfFirebase? = null

        // Method to get the singleton instance
        fun getInstance() : SingletonOfFirebase {
            if(instance == null) {
                instance = SingletonOfFirebase()
            }
            return instance!!
        }
    }

    fun getData() : DatabaseReference? {
        myReference = FirebaseDatabase.getInstance().reference
        return myReference

        // The same
        // return FirebaseDatabase.getInstance().reference.also { myReference = it }
    }


}