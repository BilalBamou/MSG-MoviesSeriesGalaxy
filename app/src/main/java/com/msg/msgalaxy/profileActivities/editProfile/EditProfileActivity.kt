package com.msg.msgalaxy.profileActivities.editProfile

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.airbnb.lottie.LottieAnimationView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.msg.msgalaxy.MVCOfComments.AdapterOfComments
import com.msg.msgalaxy.MVCOfComments.ModelOfComments
import com.msg.msgalaxy.MainActivity
import com.msg.msgalaxy.R
import com.msg.msgalaxy.fragments.ProfileFragment
import com.msg.msgalaxy.users.LoginActivity
import de.hdodenhof.circleimageview.CircleImageView
import java.util.Collections

open class EditProfileActivity : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth

    private lateinit var profilePicImageView: CircleImageView
    private lateinit var profilePicEditIcon: CardView
    private lateinit var loadingAnimationOfProfilePic: LottieAnimationView
    private lateinit var userProfilePicture: String
    private var newProfilePicUri: Uri? = null

    private lateinit var usernameEditText: TextInputLayout
    private lateinit var userName: String
    private var newUsername: String? = null

    private lateinit var saveBtn: CardView
    private lateinit var saveBtnSaveText: TextView
    private lateinit var loadingAnimationOfSaveBtn: LottieAnimationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        // Initialize the variables
        initializeFun()

        // Getting profilePic and username from ProfileFragment
        getDataFromProfileFragment()

        // get picture from galary
        getPictureFromGalary_onClick()

        // Save Button Process and Edit username and profilePic
        saveButtonProcess()

        // Arrow Btn From EditProfileActivity to Profile Fragment
        arrowBack()
    }

    private fun initializeFun() {
        mAuth = FirebaseAuth.getInstance()

        profilePicImageView = findViewById(R.id.editProfileActivity_profilePic)
        profilePicEditIcon = findViewById(R.id.editProfileActivity_profilePic_editIconId)
        loadingAnimationOfProfilePic = findViewById(R.id.editProfileActivity_profilePic_loadingLottie)

        usernameEditText = findViewById(R.id.editProfileActivity_usernameId)

        saveBtn = findViewById(R.id.editProfileActivity_saveButtonId)
        saveBtnSaveText = findViewById(R.id.editProfileActivity_saveButtonId_textView)
        loadingAnimationOfSaveBtn = findViewById(R.id.editProfileActivity_saveButtonId_loadingLottie)
    }

    private fun getDataFromProfileFragment() {
        var bundle: Bundle? = intent.extras
        userName = bundle!!.getString("username")!!
        userProfilePicture = bundle.getString("profilePic")!!

        setDataToEditProfileActicity()
    }
    private fun setDataToEditProfileActicity() {
        //Set username
        usernameEditText.editText!!.setText(userName)

        //Set pictureProfile
        Glide.with(this).load(userProfilePicture).listener(object : RequestListener<Drawable> {
            override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>, isFirstResource: Boolean): Boolean {
                return false
            }
            override fun onResourceReady(resource: Drawable, model: Any, target: Target<Drawable>?, dataSource: DataSource, isFirstResource: Boolean): Boolean {
                // Image loaded successfully
                loadingAnimationOfProfilePic.visibility = View.GONE
                return false
            }
        }).into(profilePicImageView)
    }

    // these 4 functions about getting pic from galary and set it
    private fun getPictureFromGalary_onClick() {
        profilePicImageView.setOnClickListener {
            getPictureFromGalaryProcess()
        }
        profilePicEditIcon.setOnClickListener {
            getPictureFromGalaryProcess()
        }
    }
    private fun getPictureFromGalaryProcess() {
        // Check for permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted, request it
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 1)
        } else {
            // Permission is granted, proceed with image picker
            ImagePicker.with(this).crop().compress(1024).maxResultSize(1080, 1080).start()
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
        try {
            newProfilePicUri = data!!.data!!
            profilePicImageView.setImageURI(newProfilePicUri)
        }
        catch (e: Exception) { }
    }

    private fun saveButtonProcess() {
        saveBtn.setOnClickListener {
            if(usernameEditText.editText!!.text.isEmpty()) {
                Toast.makeText(this, "Please enter your new username", Toast.LENGTH_SHORT).show()
            }
            else {
                // Hide the keyboard
                var inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.hideSoftInputFromWindow(saveBtn.windowToken, 0)

                saveBtnSaveText.visibility = View.GONE
                loadingAnimationOfSaveBtn.visibility = View.VISIBLE
                editUsernameProcess()
                uploadProfilePic()
            }
        }
    }
    // upload the profilePic to Firebase
    private fun uploadProfilePic() {
        if(newProfilePicUri != null) {
            var storage: FirebaseStorage = FirebaseStorage.getInstance()
            val filePath: StorageReference = storage.reference.child("UserProfilePicture").child(mAuth.currentUser!!.uid).child(newProfilePicUri!!.lastPathSegment!!)

            filePath.putFile(newProfilePicUri!!).addOnSuccessListener {taskSnapshot ->
                filePath.downloadUrl.addOnSuccessListener { uri ->
                    // Get a reference to the user's node in the "Users" child
                    var reference: DatabaseReference = FirebaseDatabase.getInstance().reference.child("Users").child(mAuth.currentUser!!.uid)

                    var userMap:HashMap<String , Any> = HashMap()
                    userMap.put("profilePic" , uri.toString())

                    // Update the profilePic with the new value
                    reference.updateChildren(userMap).addOnCompleteListener {task ->
                        if (task.isSuccessful) {
                            saveBtnSaveText.visibility = View.VISIBLE
                            loadingAnimationOfSaveBtn.visibility = View.GONE

                            val resultIntent = Intent()
                            if(!userName.trim().equals(newUsername!!.trim())) {
                                // !userName.equals(newUsername) - that means the user changed his username
                                resultIntent.putExtra("updatedUsername", newUsername)
                                resultIntent.putExtra("updatedProfilePic", newProfilePicUri.toString())
                                setResult(RESULT_OK, resultIntent)
                                // update profile picture in comments and username
                                setTheUpdatedProfilePicAndUsername_forComments(uri.toString() , newUsername!!)

                                Toast.makeText(this, "You have successfully edited your Profile picture and Username!", Toast.LENGTH_SHORT).show()
                            }
                            else if (userName.trim().equals(newUsername!!.trim())) {
                                resultIntent.putExtra("updatedProfilePic", newProfilePicUri.toString())
                                setResult(RESULT_OK, resultIntent)
                                // update just profile picture in comments ( cuz username is null )
                                setTheUpdatedProfilePicAndUsername_forComments(uri.toString())

                                Toast.makeText(this, "You have successfully edited your Profile picture!", Toast.LENGTH_SHORT).show()
                            }
                            finish()
                        }
                    }
                }
            }
        }
        else {
            saveBtnSaveText.visibility = View.VISIBLE
            loadingAnimationOfSaveBtn.visibility = View.GONE

            val resultIntent = Intent()
            if (newUsername != null) {
                resultIntent.putExtra("updatedUsername", newUsername)
                setResult(RESULT_OK, resultIntent)
                // update just username in comments ( cuz picture is null )
                setTheUpdatedProfilePicAndUsername_forComments(null ,newUsername!!)

                Toast.makeText(this, "You have successfully edited your Username!", Toast.LENGTH_SHORT).show()
            }
            finish()
        }
    }
    private fun editUsernameProcess() {
        newUsername = usernameEditText.editText!!.text.toString().trim()

        mAuth = FirebaseAuth.getInstance()

        // Get a reference to the user's node in the "Users" child
        var reference: DatabaseReference = FirebaseDatabase.getInstance().reference.child("Users").child(mAuth.currentUser!!.uid)

        // Update the username with the new value
        reference.child("username").setValue(newUsername)
    }

    private fun setTheUpdatedProfilePicAndUsername_forComments(profilePic: String? = null , username: String? = null) {
        val userId = mAuth.currentUser!!.uid
        var reference: DatabaseReference = FirebaseDatabase.getInstance().reference.child("Comments")
        reference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    // There are comments
                    for (nameSnapshot: DataSnapshot in snapshot.children) {
                        val name: String = nameSnapshot.key!!

                        // Check if the userId exists under the current name
                        if (nameSnapshot.hasChild(userId)) {
                            for (commentSnapshot: DataSnapshot in nameSnapshot.child(userId).children) {
                                val commentId: String = commentSnapshot.key!!
                                // Update the profilePic directly for the current user's comment
                                // commentSnapshot.child("profilePic").ref.setValue(profilePic)

                                var reference: DatabaseReference =
                                    FirebaseDatabase.getInstance().reference.child("Comments").child(name).child(userId).child(commentId)
                                var userMap: HashMap<String, Any> = HashMap()
                                if (profilePic!=null && username!=null) {
                                    //userMap.put("profilePic", profilePic)
                                    userMap["profilePic"] = profilePic
                                    userMap["username"] = username
                                    reference.updateChildren(userMap)
                                    // Update the profilePic directly for the current user's comment
                                    // commentSnapshot.child("profilePic").ref.setValue(profilePic)
                                }
                                else if (profilePic==null && username!=null) {
                                    // userMap.put("username", username)
                                    userMap["username"] = username
                                    reference.updateChildren(userMap)
                                    // commentSnapshot.child("username").ref.setValue(username)
                                }
                                else if (username==null && profilePic!=null) {
                                    userMap["profilePic"] = profilePic
                                    reference.updateChildren(userMap)
                                }
                            }
                        }
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun arrowBack() {
        var arrowBack: CardView = findViewById(R.id.editProfileActivity_arrowBackId)
        arrowBack.setOnClickListener({
            //For Back from Activity to Fragment !!!!!
            onBackPressed()
        })
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }
}