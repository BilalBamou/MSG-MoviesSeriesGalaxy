package com.msg.msgalaxy.fragments

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.airbnb.lottie.LottieAnimationView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.msg.msgalaxy.R
import com.msg.msgalaxy.SettingsActivity
import com.msg.msgalaxy.profileActivities.MVCMyFires.MyFiresActivity
import com.msg.msgalaxy.profileActivities.MVCMyList.MyListActivity
import com.msg.msgalaxy.profileActivities.editProfile.EditProfileActivity
import de.hdodenhof.circleimageview.CircleImageView

class ProfileFragment: Fragment() {

    private lateinit var view: View
    private lateinit var profileView: RelativeLayout
    private lateinit var loadingData: RelativeLayout
    private lateinit var loadingAnimation: LottieAnimationView
    private lateinit var noInternet: RelativeLayout
    private lateinit var retryButton: CardView

    private lateinit var userProfilePicture: String
    private lateinit var userName: String
    companion object {
        private const val EDIT_PROFILE_REQUEST_CODE = 1001
    }

    private var mAuth: FirebaseAuth? = null
    private var usernameTextView: TextView? = null
    private var profilePicImageView: CircleImageView? = null

    private var myListBtn: CardView? = null
    private var myFiresBtn: CardView? = null
    private lateinit var editProfileBtn: CardView
    private lateinit var settingsBtn: CardView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //Clear the Transparent of status bar
            activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        }

        view = inflater.inflate(R.layout.profile_fragment, container , false)
        profileView = view.findViewById(R.id.profileFragment_viewOfProfileRelativeLayoutId)
        loadingData = view.findViewById(R.id.profileFragment_lottieLoadingAnimation_relativeLayoutId)
        loadingAnimation = view.findViewById(R.id.profileFragment_loadingLottie)

        noInternet = view.findViewById(R.id.profileFragment_noInternet_relativeLayoutId)
        retryButton = view.findViewById(R.id.profileFragment_retryButtonId)

        mAuth = FirebaseAuth.getInstance()

        internetTestProcess()

        retryButton.setOnClickListener {
            onRetryButtonClick()
        }

        // The Total of my list items
        totalOfItems_inMyListProcess()

        myListProcess()

        myFiresProcess()

        editProfileProcess()

        appSettingsProcess()

        return view
    }

    fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = connectivityManager.activeNetwork
        val networkCapabilities = connectivityManager.getNetworkCapabilities(activeNetwork)
        return networkCapabilities != null && (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) )
    }

    private fun internetTestProcess() {
        if (!isNetworkAvailable(requireContext())) {
            profileView.visibility = View.GONE
            loadingData.visibility = View.GONE
            noInternet.visibility = View.VISIBLE
            return
        }
        loadingData.visibility = View.VISIBLE
        noInternet.visibility = View.GONE

        // Attempt to get data again
        //get The Username and The ProfilePic from Firebase
        getUsername_pPic()
    }

    private fun onRetryButtonClick() {
        if (!isNetworkAvailable(requireContext())) {
            // If still no internet
            profileView.visibility = View.GONE
            loadingData.visibility = View.GONE
            noInternet.visibility = View.VISIBLE
            return
        }
        loadingData.visibility = View.VISIBLE
        noInternet.visibility = View.GONE

        // Attempt to get data again
        //get The Username and The ProfilePic from Firebase
        getUsername_pPic()
    }

    private fun totalOfItems_inMyListProcess() {
        var totalItemsTxtView: TextView = view.findViewById(R.id.profileFragment_myListBtn_totalItemsId)
        var totalItems = ""

        var reference: DatabaseReference = FirebaseDatabase.getInstance().reference.child("MyList").child(mAuth!!.currentUser!!.uid)
        reference.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                totalItems = snapshot.childrenCount.toString()
                if (totalItems.equals("0")) {
                    totalItemsTxtView.visibility = View.GONE
                }
                else {
                    totalItemsTxtView.text = totalItems
                }
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun appSettingsProcess() {
        settingsBtn = view.findViewById(R.id.profileFragment_settingsBtnId)
        settingsBtn.setOnClickListener {
            val intent = Intent(requireContext() , SettingsActivity::class.java)
            intent.putExtra("username" , userName)
            intent.putExtra("profilePic" , userProfilePicture )
            startActivity(intent)
        }
    }

    private fun getUsername_pPic() {

        //Initialize the variables
        usernameTextView = view.findViewById(R.id.profileFragment_usernameId)
        profilePicImageView = view.findViewById(R.id.profileFragment_profilePicId)

        //Set visibility to loading data Animation
        loadingData.visibility = View.VISIBLE
        profileView.visibility = View.GONE

        var reference: DatabaseReference = FirebaseDatabase.getInstance().reference.child("Users").child(mAuth!!.currentUser!!.uid)

        // Use ValueEventListener to get the value of the "username" child
        reference.child("username").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // Get the value from the dataSnapshot
                userName = snapshot.getValue(String::class.java)!!
                usernameTextView!!.text = userName
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })

        reference.child("profilePic").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // Get the value from the dataSnapshot
                userProfilePicture = snapshot.getValue(String::class.java)!!

                context?.let {
                    Glide.with(it).load(userProfilePicture).listener(object :
                        RequestListener<Drawable> {
                        override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>, isFirstResource: Boolean): Boolean {
                            return false
                        }
                        override fun onResourceReady(resource: Drawable, model: Any, target: Target<Drawable>?, dataSource: DataSource, isFirstResource: Boolean): Boolean {
                            // Image loaded successfully
                            profileView.visibility = View.VISIBLE
                            loadingData.visibility = View.GONE
                            loadingAnimation.visibility = View.GONE

                            return false
                        }
                    }).into(profilePicImageView!!)
                }

            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    private fun myListProcess() {
        myListBtn = view.findViewById(R.id.profileFragment_myListBtnId)
        myListBtn!!.setOnClickListener {
            var intent = Intent(activity , MyListActivity::class.java)
            startActivity(intent)
        }
    }

    private fun myFiresProcess() {
        myFiresBtn = view.findViewById(R.id.profileFragment_myFiresBtnId)
        myFiresBtn!!.setOnClickListener {
            var intent = Intent(activity , MyFiresActivity::class.java)
            startActivity(intent)
        }
    }

    private fun editProfileProcess() {
        editProfileBtn = view.findViewById(R.id.profileFragment_editProfileBtnId)
        editProfileBtn.setOnClickListener {
            var intent = Intent(requireContext() , EditProfileActivity::class.java)
            intent.putExtra("username" , userName)
            intent.putExtra("profilePic" , userProfilePicture )
            startActivityForResult(intent, EDIT_PROFILE_REQUEST_CODE)
        }
    }

    // this function for the username that it will come from EditProfile
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == EDIT_PROFILE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            // Check if the result includes the updated username and profilePic
            try {
                val updatedUsername = data?.getStringExtra("updatedUsername")
                val updatedProfilePic = data?.getStringExtra("updatedProfilePic")

                if (updatedUsername != null) {
                    // Update the UI with the new username
                    userName = updatedUsername
                    usernameTextView!!.text = userName
                    userName = updatedUsername
                }
                if (updatedProfilePic != null) {
                    // Update the UI with the new profilePic
                    loadingAnimation.visibility = View.VISIBLE
                    context?.let {
                        Glide.with(it).load(updatedProfilePic).listener(object : RequestListener<Drawable> {
                            override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>, isFirstResource: Boolean): Boolean {
                                return false
                            }
                            override fun onResourceReady(resource: Drawable, model: Any, target: Target<Drawable>?, dataSource: DataSource, isFirstResource: Boolean): Boolean {
                                // Image loaded successfully
                                loadingAnimation.visibility = View.GONE
                                return false
                            }
                        }).into(profilePicImageView!!)
                    }
                    userProfilePicture = updatedProfilePic
                }
            }
            catch (e: Exception) {}
        }
    }
}