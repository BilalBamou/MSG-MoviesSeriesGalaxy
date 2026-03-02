package com.msg.msgalaxy

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.msg.msgalaxy.fragments.HomeFragment
import com.msg.msgalaxy.fragments.ProfileFragment
import com.msg.msgalaxy.fragments.SearchFragment
import com.msg.msgalaxy.fragments.TopRatedFragment
import com.msg.msgalaxy.users.LoginActivity
import me.ibrahimsn.lib.NiceBottomBar
import me.ibrahimsn.lib.OnItemSelectedListener

class MainActivity : AppCompatActivity() {

    private var bottomNavigationView : NiceBottomBar? = null
    private var backButtonPressedTime: Long = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        bottomNavigationView = findViewById(R.id.bottomNavigationId)

        //Default Fragment is (HOME fragment)
        supportFragmentManager.beginTransaction().replace(R.id.fragment_containerId , HomeFragment()).commit()

        bottomNavigationView?.setOnItemSelectedListener(object : OnItemSelectedListener {
            override fun onItemSelect(pos: Int) {
                var selectedFragment: Fragment? = null
                when(pos) {
                    0 -> selectedFragment = HomeFragment()
                    1 -> selectedFragment = SearchFragment()
                    2 -> selectedFragment = TopRatedFragment()
                    3 -> selectedFragment = ProfileFragment()
                }
                supportFragmentManager.beginTransaction().replace(R.id.fragment_containerId , selectedFragment!!).commit()
            }
        })
    }

    override fun onBackPressed() {
        var currentTime: Long = System.currentTimeMillis()
        if (backButtonPressedTime + 2000 > currentTime) {
            // If the back button is pressed again within 2 seconds, exit the app
            super.onBackPressed()
        }
        else {
            // Show a toast or a message to inform the user to press again to exit
            Toast.makeText(this, "Press back again to close the app", Toast.LENGTH_SHORT).show()
        }
        backButtonPressedTime = currentTime;
    }
}