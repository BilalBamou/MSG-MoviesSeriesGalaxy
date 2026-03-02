package com.msg.msgalaxy

import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.msg.msgalaxy.users.LoginActivity
import com.msg.msgalaxy.users.SignupActivity

class EntryActivity : AppCompatActivity() {

    private var loginButton: CardView? = null
    private var signupButton: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // Transparent status bar
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)

            // Set navigation bar color to black
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                window.navigationBarColor = Color.parseColor("#000000")
            }
        }

        setContentView(R.layout.activity_entry)

        loginButton = findViewById(R.id.entryActivity_loginButtonId)
        signupButton = findViewById(R.id.entryActivity_createAccountButtonId)

        loginButton?.setOnClickListener {
            val intent = Intent(this@EntryActivity, LoginActivity::class.java)
            startActivity(intent)
        }

        signupButton?.setOnClickListener {
            var intent = Intent(this@EntryActivity , SignupActivity::class.java)
            startActivity(intent)
        }
    }
}