package com.msg.msgalaxy.users

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.airbnb.lottie.LottieAnimationView
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.msg.msgalaxy.R
import de.hdodenhof.circleimageview.CircleImageView

class SignupActivity : AppCompatActivity() {

    private var signupButton: CardView? = null

    //About Load Pic From Galary and Upload it to Firebase
    private var profilePic: CircleImageView? = null
    private var uriOfProfilePic: Uri? = null

    private var usernameEditText: TextInputLayout? = null
    private var emailEditText: TextInputLayout? = null
    private var passwordEditText: TextInputLayout? = null

    private var loadingAnimation: LottieAnimationView? = null
    private var signupTextFromSignupBtn: TextView? = null
    private var loginButton: TextView? = null

    //Firebase
    private var mAuth: FirebaseAuth? = null
    private var reference: DatabaseReference? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        mAuth = FirebaseAuth.getInstance()

        signupButton = findViewById(R.id.signupActivity_signupButtonId)
        signupButton!!.setOnClickListener {
            // Signup Function
            signupProcess()
        }

        loginButton = findViewById(R.id.signupActivity_loginButtonId)
        loginButton!!.setOnClickListener {
            var intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        //Add picture to Profile
        addProfilePicProcess()
    }

    private fun addProfilePicProcess() {
        profilePic = findViewById(R.id.signupActivity_addProfilePicId)
        profilePic!!.setOnClickListener {
            // Check for permission
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                // Permission is not granted, request it
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 1)
            } else {
                // Permission is granted, proceed with image picker
                ImagePicker.with(this).crop().compress(1024).maxResultSize(1080, 1080).start()
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == 1) {
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, proceed with image picker
                ImagePicker.with(this).crop().compress(1024).maxResultSize(1080, 1080).start()
            } else {
                // Permission denied
                Toast.makeText(this, "Permission denied. Cannot access gallery.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        uriOfProfilePic = data!!.data
        profilePic!!.setImageURI(uriOfProfilePic)
    }

    private fun signupProcess() {
        //Initialize the variables
        usernameEditText = findViewById(R.id.signupActivity_usernameId)
        emailEditText = findViewById(R.id.signupActivity_emailId)
        passwordEditText = findViewById(R.id.signupActivity_passwordId)
        loadingAnimation = findViewById(R.id.signupActivity_signupButtonId_loadingLottie)
        signupTextFromSignupBtn = findViewById(R.id.signupActivity_signupButtonId_textView)

        var username = usernameEditText!!.editText!!.text.toString().trim()
        var email = emailEditText!!.editText!!.text.toString().trim()
        var password = passwordEditText!!.editText!!.text.toString().trim()

        if (uriOfProfilePic == null) {
            Toast.makeText(this, "Please select your profile picture!", Toast.LENGTH_SHORT).show()
        }
        else if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
            if (username.isEmpty()) {
                Toast.makeText(this, "Please enter your username", Toast.LENGTH_SHORT).show()
            }
            else if (email.isEmpty()) {
                Toast.makeText(this, "Please enter your email", Toast.LENGTH_SHORT).show()
            }
            else if (password.isEmpty()) {
                Toast.makeText(this, "Please enter your password", Toast.LENGTH_SHORT).show()
            }
        }
        else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Please enter a valid email", Toast.LENGTH_SHORT).show()
        }
        else if (password.length < 6) {
            Toast.makeText(this, "Password must be at least 6 characters ", Toast.LENGTH_SHORT).show()
        }
        else {
            loadingAnimation!!.visibility = View.VISIBLE
            signupTextFromSignupBtn!!.visibility = View.GONE

            // Hide the keyboard
            var inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(signupButton!!.windowToken, 0)

            mAuth!!.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this , { task ->
                if (task.isSuccessful()) {
                    var firebaseUser: FirebaseUser? = mAuth!!.currentUser
                    val userId: String = firebaseUser!!.uid

                    uploadProfilePicWithUserDetails(userId , username , email)

                } else {
                    if (task.exception is FirebaseAuthUserCollisionException) {
                        // email already exists in the database
                        Toast.makeText(this, "Email already exists. Please use a different email.", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "Sign-up failed. Please try again.", Toast.LENGTH_SHORT).show()
                    }
                    loadingAnimation!!.visibility = View.GONE
                    signupTextFromSignupBtn!!.visibility = View.VISIBLE

                }
            } )
        }
    }

    private fun uploadProfilePicWithUserDetails(userId: String, username: String, email: String) {
        var storage: FirebaseStorage = FirebaseStorage.getInstance()
        val filePath: StorageReference = storage.reference.child("UserProfilePicture").child(userId).child(uriOfProfilePic!!.lastPathSegment!!)

        filePath.putFile(uriOfProfilePic!!).addOnSuccessListener {taskSnapshot ->
            filePath.downloadUrl.addOnSuccessListener { uri ->

                reference = FirebaseDatabase.getInstance().reference.child("Users").child(userId)

                var data:HashMap<String , Any> = HashMap()
                data.put("id", userId)
                data.put("profilePic" , uri.toString())
                data.put("username", username.trim())
                data.put("email", email)

                reference!!.setValue(data).addOnCompleteListener {task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "Congratulations! You have successfully signed up.", Toast.LENGTH_SHORT).show()
                        loadingAnimation!!.visibility = View.GONE
                        signupTextFromSignupBtn!!.visibility = View.VISIBLE

                        var intent = Intent(this, LoginActivity::class.java)
                        /* this line is important for ( if the user does not log in he cant enter to main activity [ cuz if if i delete this line
                        the user direcly when he sign up and close the app and open it again he will go to main activity direclty without log in]) */
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        finish()
                    }
                }

            }
        }
    }
}