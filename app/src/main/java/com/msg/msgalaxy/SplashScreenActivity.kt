package com.msg.msgalaxy

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.msg.msgalaxy.MVCOfFires_dislikes.forFires.ModelOfFires

class SplashScreenActivity : AppCompatActivity() {

    companion object {
        private const val SPLASH_SPEED: Long = 1200
        private const val PREFS_NAME = "MyPrefs"
        private const val LOGIN_COMPLETED_KEY = "loginCompleted"
    }

    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        // Set navigation bar color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.navigationBarColor = Color.parseColor("#191C20")
        }

        mAuth = FirebaseAuth.getInstance()

        Thread {
            Thread.sleep(SPLASH_SPEED)
            runOnUiThread {
                checkLoginStatus()
            }
        }.start()
    }

    private fun checkLoginStatus() {
        val prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val loginCompleted = prefs.getBoolean(LOGIN_COMPLETED_KEY, false)

        if (!loginCompleted) {
            // User is not authenticated, go to LoginActivity
            startActivity(Intent(this@SplashScreenActivity, EntryActivity::class.java))
            finish()
        }
        else {
            // User is logged in, go to MainActivity
            var intent = Intent(this@SplashScreenActivity, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }
}