package com.msg.msgalaxy

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import com.airbnb.lottie.LottieAnimationView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.msg.msgalaxy.users.LoginActivity
import de.hdodenhof.circleimageview.CircleImageView

class SettingsActivity : AppCompatActivity() {

    private lateinit var toolbar: Toolbar

    private var suggestionsBtn: CardView? = null
    private var reportIssueBtn: CardView? = null
    private var logOutBtn: CardView? = null
    private lateinit var deleteAccount: CardView

    private var mAuth: FirebaseAuth? = null
    private lateinit var alertDialogBuilder : AlertDialog.Builder

    private lateinit var userName: String
    private lateinit var userProfilePicture: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        // Set navigation bar color
        window.statusBarColor = ContextCompat.getColor(this , R.color.primaryColorOfApp)

        mAuth = FirebaseAuth.getInstance()

        // Initialize variables
        toolbar = findViewById(R.id.settingsActivity_toolBarId)

        // Set arrow back button to Toolbar
        toolbar.setNavigationIcon(R.drawable.arrow_back)
        toolbar.setNavigationOnClickListener {
            finish()
        }

        val bundle: Bundle? = intent.extras
        userName = bundle!!.getString("username")!!
        userProfilePicture = bundle.getString("profilePic")!!

        suggestionsForOurAppFunc()
        reportIssueWithOurAppFunc()
        logOutProcess()
        deleteAccountProcess()
    }

    private fun suggestionsForOurAppFunc() {
        // Suggestions For Our Application Function
        suggestionsBtn = findViewById(R.id.profileFragment_suggestionsBtnId)
        suggestionsBtn!!.setOnClickListener {

            val recipientEmail = "msg.msgalaxy@gmail.com"
            val emailSubject = "MSG - Suggestions"
            val emailMessage = "Hello,\n\nI have some suggestions for your application:\n\n"

            val intent = Intent(Intent.ACTION_SENDTO)
            intent.data = Uri.parse("mailto:")
            intent.setPackage("com.google.android.gm")
            // Add the email
            intent.putExtra(Intent.EXTRA_EMAIL, arrayOf(recipientEmail))
            // Add the email subject
            intent.putExtra(Intent.EXTRA_SUBJECT, emailSubject)
            // Add the email body text
            intent.putExtra(Intent.EXTRA_TEXT, emailMessage)

            try {
                startActivity(intent)
            } catch (ex: ActivityNotFoundException) {
                // Handle the case where no email app is installed on the device.
            }
        }
    }

    private fun reportIssueWithOurAppFunc() {
        reportIssueBtn = findViewById(R.id.profileFragment_reportIssueBtnId)
        reportIssueBtn!!.setOnClickListener {
            val recipientEmail = "msg.msgalaxy@gmail.com"
            val emailSubject = "MSG - Reporting Issue"
            val emailMessage = "Hello,\n\nI encountered the following issue with your application:\n\n"

            val intent = Intent(Intent.ACTION_SENDTO)
            intent.data = Uri.parse("mailto:")
            intent.setPackage("com.google.android.gm")
            // Add the email
            intent.putExtra(Intent.EXTRA_EMAIL, arrayOf(recipientEmail))
            // Add the email subject
            intent.putExtra(Intent.EXTRA_SUBJECT, emailSubject)
            // Add the email body text
            intent.putExtra(Intent.EXTRA_TEXT, emailMessage)

            try {
                startActivity(intent)
            } catch (ex: ActivityNotFoundException) {
                // Handle the case where no email app is installed on the device.
            }
        }
    }

    private fun logOutProcess() {
        // Initialize the variables
        logOutBtn = findViewById(R.id.profileFragment_logOutBtnId)
        logOutBtn!!.setOnClickListener {

            alertDialogBuilder  = AlertDialog.Builder(this)
                .setCancelable(false)
                .setTitle("LOG OUT")
                .setMessage("Are you sure you want to log out ?")
                .setPositiveButton("LOG OUT") { dialogInterface, i ->

                    mAuth!!.signOut()

                    var intent = Intent(this, LoginActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    startActivity(intent)
                    finish()
                    Toast.makeText(this, "You have successfully logged out.", Toast.LENGTH_SHORT).show()
                }
                .setNegativeButton("Cancel") { dialogInterface, i ->
                    dialogInterface.cancel()
                }

            var alertDialog : AlertDialog = alertDialogBuilder.create()

            //Change The Color of LOG OUT Btn
            alertDialog.setOnShowListener {
                var positiveButton: Button = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)
                positiveButton.setTextColor(Color.parseColor("#FF002E"))

                var negativeButton: Button = alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE)
                negativeButton.setTextColor(Color.parseColor("#F0F3F8"))
            }
            alertDialog.show()

            // for when the user log out he can again signup but he can't enter to mainAc until login
            clearLoginFlag()
        }
    }

    private fun clearLoginFlag() {
        val prefs: SharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = prefs.edit()
        editor.remove("loginCompleted")
        editor.apply()
    }

    private fun deleteAccountProcess() {
        deleteAccount = findViewById(R.id.profileFragment_deleteAccBtnId)
        deleteAccount.setOnClickListener {
            var builder: AlertDialog.Builder = AlertDialog.Builder(this)
            var view: View = layoutInflater.inflate(R.layout.custom_alert_dialog_of_delete_account , null)

            view.background = ContextCompat.getDrawable(this , R.drawable.background_of_alert_dialog)

            // Initialize the variables
            var profilePic: CircleImageView = view.findViewById(R.id.customAlertDialogOf_deleteAccount_profilePicId)
            var username: TextView = view.findViewById(R.id.customAlertDialogOf_deleteAccount_usernameId)
            var deleteEditText : TextInputLayout = view.findViewById(R.id.customAlertDialogOf_deleteAccount_textInputLayoutId)
            var checkBox1: CheckBox = view.findViewById(R.id.checkBox_firstId)
            var checkBox2: CheckBox = view.findViewById(R.id.checkBox_secondId)
            var deleteAccBtn: CardView = view.findViewById(R.id.customAlertDialogOf_deleteAccount_deleteAccButtonId)
            var cancelBtn: CardView = view.findViewById(R.id.customAlertDialogOf_deleteAccount_cancelButtonId)
            var deleteAccBtn_deleteTxt: TextView = view.findViewById(R.id.customAlertDialogOf_deleteAccount_deleteAccButtonId_textView)
            var deleteAccBtn_loadingAnimation: LottieAnimationView = view.findViewById(R.id.customAlertDialogOf_deleteAccount_deleteAccButtonId_loadingLottie)

            // Set the data to views
            username.text = userName
            Glide.with(this).load(userProfilePicture).apply(RequestOptions.fitCenterTransform()).into(profilePic)

            builder.setView(view)
            var dialog: AlertDialog = builder.create()
            // Set the background of the entire dialog to transparent
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.show()

            // for deleting Account
            deleteAccBtn.setOnClickListener {
                var deleteWord: String = deleteEditText.editText!!.text.toString().trim()

                if(deleteWord.isEmpty() || !deleteWord.equals("delete")) {
                    Toast.makeText(this, "Please type ' delete ' in the box to continue.", Toast.LENGTH_SHORT).show()
                }
                else if (!checkBox1.isChecked || !checkBox2.isChecked){
                    Toast.makeText(this, "Please check both checkboxes to continue.", Toast.LENGTH_SHORT).show()
                }
                else {
                    // Here we will delete account
                    // Hide the keyboard
                    var inputMethodManager: InputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    inputMethodManager.hideSoftInputFromWindow(deleteAccBtn.getWindowToken(), 0)

                    deleteAllDataOfUserProcess(dialog , deleteAccBtn_deleteTxt , deleteAccBtn_loadingAnimation)
                }
            }
            // for Canceling
            cancelBtn.setOnClickListener {
                dialog.dismiss()
            }

        }
    }

    private fun deleteAllDataOfUserProcess(dialog: AlertDialog , deleteAccBtn_deleteTxt: TextView , deleteAccBtn_loadingAnimation: LottieAnimationView) {
        // Set visibilities
        deleteAccBtn_deleteTxt.visibility = View.GONE
        deleteAccBtn_loadingAnimation.visibility = View.VISIBLE

        // for deleting email ... from authentication in Firebase
        mAuth!!.currentUser!!.delete().addOnCompleteListener {task->
            if (task.isSuccessful) {
                // Set visibilities
                deleteAccBtn_deleteTxt.visibility = View.VISIBLE
                deleteAccBtn_loadingAnimation.visibility = View.GONE

                Toast.makeText(this, "You have successfully deleted your account!", Toast.LENGTH_SHORT).show()
                var intent = Intent(this , EntryActivity::class.java)
                startActivity(intent)
                finish()
            }
        }

        // delete all other things
        var userReference: DatabaseReference = FirebaseDatabase.getInstance().reference.child("Users")
        userReference.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    snapshot.child(mAuth!!.currentUser!!.uid).ref.removeValue()
                }
            }
            override fun onCancelled(error: DatabaseError) {}
        })
        deleteFiresDislikesHalvesOfCurrentUser()
        deleteListOfCurrentUser()
        deleteCommentsOfCurrentUser()
        deleteRepliesOfCurrentUser()
        deleteCommentsReactionsOfCurrentUser()
        deleteProfilePicFromStorage()

    }

    private fun deleteFiresDislikesHalvesOfCurrentUser() {
        var referenceFires = FirebaseDatabase.getInstance().reference.child("Fires")
        referenceFires.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (dataSnapshot: DataSnapshot in snapshot.children) {
                        dataSnapshot.child(mAuth!!.currentUser!!.uid).ref.removeValue()
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {}
        })
        var referenceDislikes = FirebaseDatabase.getInstance().reference.child("Dislikes")
        referenceDislikes.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (dataSnapshot: DataSnapshot in snapshot.children) {
                        dataSnapshot.child(mAuth!!.currentUser!!.uid).ref.removeValue()
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {}
        })
        var referenceHalves = FirebaseDatabase.getInstance().reference.child("Halves")
        referenceHalves.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (dataSnapshot: DataSnapshot in snapshot.children) {
                        dataSnapshot.child(mAuth!!.currentUser!!.uid).ref.removeValue()
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun deleteListOfCurrentUser() {
        var referenceList = FirebaseDatabase.getInstance().reference.child("MyList")
        referenceList.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    snapshot.child(mAuth!!.currentUser!!.uid).ref.removeValue()
                }
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun deleteCommentsOfCurrentUser() {
        var referenceComments = FirebaseDatabase.getInstance().reference.child("Comments")
        referenceComments.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (dataSnapshot: DataSnapshot in snapshot.children) {
                        dataSnapshot.child(mAuth!!.currentUser!!.uid).ref.removeValue()
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun deleteRepliesOfCurrentUser() {
        var referenceReplies = FirebaseDatabase.getInstance().reference.child("CommentReplies")
        referenceReplies.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (nameSnapshot: DataSnapshot in snapshot.children) { // Names
                        for (commentSnapshot: DataSnapshot in nameSnapshot.children) { // Main CommentId
                            for (replySnapshot: DataSnapshot in commentSnapshot.children) { // Reply CommentId
                                replySnapshot.child(mAuth!!.currentUser!!.uid).ref.removeValue()
                            }
                        }
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun deleteProfilePicFromStorage() {
        // Reference to the Firebase Storage
        var storageReference: StorageReference = FirebaseStorage.getInstance().reference.child("UserProfilePicture").child(mAuth!!.currentUser!!.uid)

        // List all items (files) in the "UserProfilePicture" folder
        storageReference.listAll()
            .addOnSuccessListener { result ->
                // Iterate through each item and delete it
                result.items.forEach { fileReference ->
                    fileReference.delete()
                }
            }
    }

    private fun deleteCommentsReactionsOfCurrentUser() {
        var reference = FirebaseDatabase.getInstance().reference.child("CommentsReactions")
        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (dataSnapshot: DataSnapshot in snapshot.children) { // Names
                        for (commentSnapshot: DataSnapshot in dataSnapshot.children) { // Comments
                            for(likesSnapshot: DataSnapshot in commentSnapshot.children) { // Likes or Loves or Laughs or Angry
                                likesSnapshot.child(mAuth!!.currentUser!!.uid).ref.removeValue()
                            }
                        }
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }
}