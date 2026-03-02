package com.msg.msgalaxy

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.GestureDetector
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.airbnb.lottie.LottieAnimationView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.tabs.TabLayout
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.msg.msgalaxy.MVCOfComments.AdapterOfComments
import com.msg.msgalaxy.MVCOfComments.ModelOfComments
import com.msg.msgalaxy.MVCOfFires_dislikes.PeopleWhoReactedActivityForMS
import com.msg.msgalaxy.MVCOfFires_dislikes.ViewPagerAdapter
import com.msg.msgalaxy.profileActivities.MVCMyList.ModelOfMyList
import com.msg.msgalaxy.profileActivities.MVCMyList.MyListActivity
import java.text.SimpleDateFormat
import java.util.Collections
import java.util.Date
import java.util.Locale

class AboutMovieOrSerieActivity : AppCompatActivity() {

    private lateinit var backgroundBtn: CardView

    private lateinit var moviePicture: ImageView
    private lateinit var loadingAnimation: LottieAnimationView

    private lateinit var moviePicture_forPoster: ImageView
    private lateinit var loadingAnimation_forPoster: LottieAnimationView

    private lateinit var movieName: TextView
    private lateinit var movieType: TextView
    private lateinit var movieYear: TextView
    private lateinit var movieDuration: TextView

    private lateinit var playTrailerBtn: CardView
    private lateinit var myListBtn: LinearLayout
    private lateinit var addToMyListIcon: ImageView
    private lateinit var firebaseAuth: FirebaseAuth

    private lateinit var name: String
    private lateinit var picture: String
    private lateinit var year: String
    private lateinit var duration: String
    private lateinit var rating: String
    private lateinit var description: String
    private lateinit var type: String
    private lateinit var trailerUrl: String

    private lateinit var fireButton: ImageView
    private lateinit var halfButton: ImageView
    private lateinit var dislikeButton: ImageView

    private lateinit var fireBtnLinearLayout: LinearLayout
    private lateinit var halfBtnLinearLayout: LinearLayout
    private lateinit var dislikeBtnLinearLayout: LinearLayout

    private lateinit var numberOfFireEditText: TextView
    private lateinit var numberOfHalfEditText: TextView
    private lateinit var numberOfDislikeEditText: TextView

    private lateinit var numberOfFires: String
    private lateinit var numberOfHalves: String
    private lateinit var numberOfDislikes: String

    private lateinit var movieRating: TextView
    private lateinit var movieDescription: TextView

    //About Comments
    private lateinit var commentEditText: TextInputLayout
    private lateinit var sendCommentBtn: CardView
    private lateinit var sendCommentBtnIcon: ImageView
    private lateinit var recyclerViewOfComments: RecyclerView
    private lateinit var adapterOfComments: AdapterOfComments
    private lateinit var commentsList: ArrayList<ModelOfComments>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // Transparent status bar
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        }

        setContentView(R.layout.activity_about_movie_or_serie)

        firebaseAuth = FirebaseAuth.getInstance()

        //get the data from the other activities
        getDataFromActivitiesAndSetItHere()

        addToMyListProcess()

        //For showing Fires And Halves And Dislikes when the user clicks on Button
        showFires_Halves_DislikesProcess()

        fireProcess()

        dislikeProcess()

        halfProcess()

        //Play the Trailer Button and the func will take the Url as String
        playTrailerProcess(trailerUrl)

        //Do Fire when you click two times on the Picture of Movie Or Serie
        fireByClickingTwoTimeOnPicture()

        //About Comments
        commentsProcess()
    }


    private fun getDataFromActivitiesAndSetItHere() {
        //Initialize the variables
        backgroundBtn = findViewById(R.id.aboutMovieOrSerieActivity_cardViewId_background)

        moviePicture = findViewById(R.id.aboutMovieOrSerieActivity_pictureId)
        loadingAnimation = findViewById(R.id.aboutMovieOrSerieActivity_loadingLottie)
        moviePicture_forPoster = findViewById(R.id.aboutMovieOrSerieActivity_posterPic_imageViewId)
        loadingAnimation_forPoster = findViewById(R.id.aboutMovieOrSerieActivity_posterPic_loadingLottie)

        movieName = findViewById(R.id.aboutMovieOrSerieActivity_nameId)
        movieYear = findViewById(R.id.aboutMovieOrSerieActivity_yearId)
        movieDuration = findViewById(R.id.aboutMovieOrSerieActivity_durationId)
        movieRating = findViewById(R.id.aboutMovieOrSerieActivity_ratingId)
        movieDescription = findViewById(R.id.aboutMovieOrSerieActivity_descriptionId)
        movieType = findViewById(R.id.aboutMovieOrSerieActivity_typeId)

        var bundle: Bundle = intent.extras!!

        if(bundle != null) {
            picture = bundle.getString("Picture")!!
            name = bundle.getString("Name")!!
            year = bundle.getString("Year")!!
            duration = bundle.getString("Duration")!!
            rating = bundle.getString("Rating")!!
            description = bundle.getString("Description")!!
            type = bundle.getString("Type")!!
            trailerUrl = bundle.getString("Trailer")!!

            //Setting the data into the activity for Background
            Glide.with(this).load(picture).apply(RequestOptions.fitCenterTransform())
                .listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>, isFirstResource: Boolean): Boolean {
                        loadingAnimation.visibility = View.GONE
                        return false
                    }

                    override fun onResourceReady(resource: Drawable, model: Any, target: Target<Drawable>?, dataSource: DataSource, isFirstResource: Boolean): Boolean {
                        // Image loaded successfully
                        loadingAnimation.visibility = View.GONE
                        return false
                    }
                }).into(moviePicture)

            //Setting the data into the activity for Poster
            Glide.with(this).load(picture).apply(RequestOptions.fitCenterTransform())
                .listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>, isFirstResource: Boolean): Boolean {
                        loadingAnimation_forPoster.visibility = View.GONE
                        return false
                    }

                    override fun onResourceReady(resource: Drawable, model: Any, target: Target<Drawable>?, dataSource: DataSource, isFirstResource: Boolean): Boolean {
                        // Image loaded successfully
                        loadingAnimation_forPoster.visibility = View.GONE
                        return false
                    }
                }).into(moviePicture_forPoster)

            movieName.text = name
            movieYear.text = year
            movieDuration.text = duration
            movieRating.text = rating
            movieDescription.text = description
            movieType.text = type
        }
    }

    private fun showFires_Halves_DislikesProcess() {
        val showFDBtn: CardView = findViewById(R.id.aboutMovieOrSerieActivity_cardView_showFiresAndDislikesId) as CardView

        showFDBtn.setOnClickListener {
            var intent = Intent(this , PeopleWhoReactedActivityForMS::class.java)
            intent.putExtra("name" , name)
            intent.putExtra("numberOfFires" , numberOfFires)
            intent.putExtra("numberOfHalves" , numberOfHalves)
            intent.putExtra("numberOfDislikes" , numberOfDislikes)
            startActivity(intent)
        }
    }

    private fun fireProcess() {
        fireBtnLinearLayout = findViewById(R.id.aboutMovieOrSerieActivity_fireButtonId_linearLayoutId)
        fireButton = findViewById(R.id.aboutMovieOrSerieActivity_fireButtonId)
        numberOfFireEditText = findViewById(R.id.aboutMovieOrSerieActivity_numberOfFireId)

        isFire(name)
        numberOfFire(name)

        fireBtnLinearLayout.setOnClickListener {
            if (fireButton.tag.equals("unselectedFire")) {
                var userReference: DatabaseReference = FirebaseDatabase.getInstance().reference.child("Users").child(firebaseAuth.uid!!)
                userReference.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {

                            // Get the current timestamp
                            var timestamp: Long = System.currentTimeMillis()
                            // Convert the timestamp to a readable date string
                            var date: String? = SimpleDateFormat("yyyy-dd-MM  HH:mm:ss", Locale.getDefault()).format(Date(timestamp))

                            val fireData: HashMap<String , Any> = HashMap()
                            fireData.put("picture" , picture)
                            fireData.put("timestamp", date!!) // Or i can do : fireData["timestamp"] = date

                            // Store data in the Fires column
                            FirebaseDatabase.getInstance().reference.child("Fires").child(name).child(firebaseAuth.uid!!).setValue(fireData)

                            // When user selects the fire button, the dislike button and half button will be unselected
                            FirebaseDatabase.getInstance().reference.child("Halves").child(name).child(firebaseAuth.uid!!).removeValue()
                            FirebaseDatabase.getInstance().reference.child("Dislikes").child(name).child(firebaseAuth.uid!!).removeValue()
                        }
                    }
                    override fun onCancelled(error: DatabaseError) { /* Handle error */}
                })
            }
            else {
                // Remove the fire value
                FirebaseDatabase.getInstance().reference.child("Fires").child(name).child(firebaseAuth.uid!!).removeValue()
            }
        }
    }

    private fun halfProcess() {
        halfBtnLinearLayout = findViewById(R.id.aboutMovieOrSerieActivity_halfButtonId_linearLayoutId)
        halfButton = findViewById(R.id.aboutMovieOrSerieActivity_halfButtonId)
        numberOfHalfEditText = findViewById(R.id.aboutMovieOrSerieActivity_numberOfHalfId)

        isHalf(name)
        numberOfHalf(name)

        halfBtnLinearLayout.setOnClickListener {
            if (halfButton.tag.equals("unselectedHalf")) {
                var userReference: DatabaseReference = FirebaseDatabase.getInstance().reference.child("Users").child(firebaseAuth.uid!!)
                userReference.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {

                            // Get the current timestamp
                            var timestamp: Long = System.currentTimeMillis()
                            // Convert the timestamp to a readable date string
                            var date: String? = SimpleDateFormat("yyyy-dd-MM  HH:mm:ss", Locale.getDefault()).format(Date(timestamp))

                            val halfData: HashMap<String , Any> = HashMap()
                            halfData.put("picture" , picture)
                            halfData.put("timestamp", date!!) // Or i can do : fireData["timestamp"] = date

                            // Store data in the Halves column
                            FirebaseDatabase.getInstance().reference.child("Halves").child(name).child(firebaseAuth.uid!!).setValue(halfData)

                            // When user selects the half button, the dislike button and fire button will be unselected
                            FirebaseDatabase.getInstance().reference.child("Fires").child(name).child(firebaseAuth.uid!!).removeValue()
                            FirebaseDatabase.getInstance().reference.child("Dislikes").child(name).child(firebaseAuth.uid!!).removeValue()
                        }
                    }
                    override fun onCancelled(error: DatabaseError) { /* Handle error */}
                })
            }
            else {
                // Remove the half value
                FirebaseDatabase.getInstance().reference.child("Halves").child(name).child(firebaseAuth.uid!!).removeValue()
            }
        }
    }

    private fun dislikeProcess() {
        dislikeBtnLinearLayout = findViewById(R.id.aboutMovieOrSerieActivity_dislikeButtonId_linearLayoutId)
        dislikeButton = findViewById(R.id.aboutMovieOrSerieActivity_dislikeButtonId)
        numberOfDislikeEditText = findViewById(R.id.aboutMovieOrSerieActivity_numberOfDislikeId)

        isDislike(name)
        numberOfDislike(name)

        dislikeBtnLinearLayout.setOnClickListener {
            if (dislikeButton.tag.equals("unselectedDislike")) {
                var userReference: DatabaseReference = FirebaseDatabase.getInstance().reference.child("Users").child(firebaseAuth.uid!!)
                userReference.addListenerForSingleValueEvent(object: ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {

                            // Get the current timestamp
                            var timestamp: Long = System.currentTimeMillis()
                            // Convert the timestamp to a readable date string
                            var date: String? = SimpleDateFormat("yyyy-dd-MM  HH:mm:ss", Locale.getDefault()).format(Date(timestamp))

                            val dislikeData: HashMap<String , Any> = HashMap()
                            dislikeData.put("picture" , picture)
                            dislikeData.put("timestamp", date!!) // Or i can do : fireData["timestamp"] = date

                            // Store the username in the Dislikes column
                            FirebaseDatabase.getInstance().reference.child("Dislikes").child(name).child(firebaseAuth.uid!!).setValue(dislikeData)

                            // When user selects the dislike button, the fire button and half button will be unselected
                            FirebaseDatabase.getInstance().reference.child("Fires").child(name).child(firebaseAuth.uid!!).removeValue()
                            FirebaseDatabase.getInstance().reference.child("Halves").child(name).child(firebaseAuth.uid!!).removeValue()
                        }
                    }
                    override fun onCancelled(error: DatabaseError) {}
                })
            }
            else {
                // Remove the dislike value
                FirebaseDatabase.getInstance().reference.child("Dislikes").child(name).child(firebaseAuth.uid!!).removeValue()
            }
        }
    }

    private fun playTrailerProcess(trailerUrl: String?) {
        playTrailerBtn = findViewById(R.id.aboutMovieOrSerieActivity_playTrailerButtonId)
        playTrailerBtn.setOnClickListener {v->

            //Must remove the the first link (http://www.youtube.com/watch?v=) to get just the Id and For that we used Substring
            var url: String = ""
            if (trailerUrl!!.contains("https://www.youtube.com/")) {
                // Extract the video ID using substring
                var startIndex: Int = "https://www.youtube.com/watch?v=".length
                url = trailerUrl.substring(startIndex)
            }

            var intent: Intent = Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + url))
            // If the YouTube app is not installed, open the link in a web browser
            if (intent.resolveActivity(packageManager) == null) {
                intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/watch?v=" + url))
            }
            startActivity(intent)

        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun fireByClickingTwoTimeOnPicture() {

        isFire(name)
        numberOfFire(name)

        //We use GestureDetector for longPress or double click ...
        val gestureDetector = GestureDetector(this, object : SimpleOnGestureListener() {
            override fun onDoubleTap(e: MotionEvent): Boolean {
                //Here we do what we want !!
                onDoubleTapFireProcess()
                fireAnimationProcess()
                return super.onDoubleTap(e)
            }
            override fun onLongPress(e: MotionEvent) {
                //Show Picture of Movie Or Serie By Clicking One Time On The Picture
                var intent: Intent = Intent(this@AboutMovieOrSerieActivity , ShowPictureOfMSActivity()::class.java)
                intent.putExtra("Picture", picture)
                intent.putExtra("Name", name)
                startActivity(intent)
                super.onLongPress(e)
            }
        })

        backgroundBtn.setOnTouchListener(object: View.OnTouchListener {
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                gestureDetector.onTouchEvent(event!!)
                return true
            }
        })
    }

    private fun onDoubleTapFireProcess() {
        if(fireButton.tag.equals("unselectedFire")) {
            var userReference: DatabaseReference = FirebaseDatabase.getInstance().reference.child("Users").child(firebaseAuth.uid!!)
            userReference.addListenerForSingleValueEvent(object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {

                        // Get the current timestamp
                        var timestamp: Long = System.currentTimeMillis()
                        // Convert the timestamp to a readable date string
                        var date: String? = SimpleDateFormat("yyyy-dd-MM  HH:mm:ss", Locale.getDefault()).format(Date(timestamp))

                        val fireData: HashMap<String , Any> = HashMap()
                        fireData.put("picture" , picture)
                        fireData.put("timestamp", date!!) // Or i can do : fireData["timestamp"] = date

                        // Store the username in the Fires column
                        FirebaseDatabase.getInstance().reference.child("Fires").child(name).child(firebaseAuth.uid!!).setValue(fireData)

                        // When user selects the fire button, the dislike and half button will be unselected
                        FirebaseDatabase.getInstance().reference.child("Dislikes").child(name).child(firebaseAuth.uid!!).removeValue()
                        FirebaseDatabase.getInstance().reference.child("Halves").child(name).child(firebaseAuth.uid!!).removeValue()
                    }
                }
                override fun onCancelled(error: DatabaseError) { /* Handle error */ }
            })
        }
    }

    private fun fireAnimationProcess() {
        var fireAnimation: LottieAnimationView = findViewById(R.id.aboutMovieOrSerieActivity_fireLottieAnimation)

        // Show the fireAnimation with scale animation
        fireAnimation.scaleX = 0f
        fireAnimation.scaleY = 0f
        fireAnimation.visibility = View.VISIBLE

        // Scale animation
        var scaleXAnimator: ObjectAnimator = ObjectAnimator.ofFloat(fireAnimation , "scaleX" , 0f , 1f)
        var scaleYAnimator: ObjectAnimator = ObjectAnimator.ofFloat(fireAnimation , "scaleY" , 0f , 1f)

        var scaleAnimatorSet: AnimatorSet = AnimatorSet()
        scaleAnimatorSet.playTogether(scaleXAnimator , scaleYAnimator)
        scaleAnimatorSet.setDuration(600) // Adjust the duration as needed
        scaleAnimatorSet.start()

        //Play Fire animation
        fireAnimation.playAnimation()
        fireAnimation.addAnimatorListener(object: Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {}
            override fun onAnimationEnd(animation: Animator) {
                // Hide the fireAnimation when the animation ends
                fireAnimation.visibility = View.GONE
            }
            override fun onAnimationCancel(animation: Animator) {}
            override fun onAnimationRepeat(animation: Animator) {}
        })
    }

    private fun isFire(name: String) {
        var firebaseUser: FirebaseUser = FirebaseAuth.getInstance().currentUser!!
        var reference: DatabaseReference = FirebaseDatabase.getInstance().reference.child("Fires").child(name)
        reference.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.child(firebaseUser.uid).exists()) {
                    fireButton.setImageResource(R.drawable.selected_fire_icon)
                    fireButton.tag = "selectedFire"
                }
                else {
                    fireButton.setImageResource(R.drawable.unselected_fire_icon)
                    fireButton.tag = "unselectedFire"
                }
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }
    private fun numberOfFire(name: String) {
        var reference: DatabaseReference = FirebaseDatabase.getInstance().reference.child("Fires").child(name)
        reference.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                numberOfFires = snapshot.childrenCount.toString()
                numberOfFireEditText.text = numberOfFires
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun isHalf(name: String) {
        var firebaseUser: FirebaseUser = FirebaseAuth.getInstance().currentUser!!
        var reference: DatabaseReference = FirebaseDatabase.getInstance().reference.child("Halves").child(name)
        reference.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.child(firebaseUser.uid).exists()) {
                    halfButton.setImageResource(R.drawable.selected_half_icon)
                    halfButton.tag = "selectedHalf"
                }
                else {
                    halfButton.setImageResource(R.drawable.unselected_half_icon)
                    halfButton.tag = "unselectedHalf"
                }
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }
    private fun numberOfHalf(name: String) {
        var reference: DatabaseReference = FirebaseDatabase.getInstance().reference.child("Halves").child(name)
        reference.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                numberOfHalves = snapshot.childrenCount.toString()
                numberOfHalfEditText.text = numberOfHalves
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun isDislike(name: String) {
        var firebaseUser: FirebaseUser = FirebaseAuth.getInstance().currentUser!!
        var reference: DatabaseReference = FirebaseDatabase.getInstance().reference.child("Dislikes").child(name)

        reference.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.child(firebaseUser.uid).exists()) {
                    dislikeButton.setImageResource(R.drawable.selected_dislike_icon)
                    dislikeButton.tag = "selectedDislike"
                }
                else {
                    dislikeButton.setImageResource(R.drawable.unselected_dislike_icon)
                    dislikeButton.tag = "unselectedDislike"
                }
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }
    private fun numberOfDislike(name: String) {
        var reference: DatabaseReference = FirebaseDatabase.getInstance().reference.child("Dislikes").child(name)
        reference.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                numberOfDislikes = snapshot.childrenCount.toString()
                numberOfDislikeEditText.text = numberOfDislikes
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun bottomSheetOfViewMyList(name: String , pictureInBottomSheet: String) {

        val bottomSheetViewOfViewMyList: View = LayoutInflater.from(this).inflate( R.layout.bottom_sheet_of_view_mylist,
            findViewById(R.id.bottomSheetLayout_viewMyList_containerId) )

        // Set margins to the content view
        val layoutParams = CoordinatorLayout.LayoutParams(
            CoordinatorLayout.LayoutParams.MATCH_PARENT,
            CoordinatorLayout.LayoutParams.WRAP_CONTENT
        )
        layoutParams.setMargins(20, 0, 20, 32)
        bottomSheetViewOfViewMyList.layoutParams = layoutParams

        val bottomSheetDialog: BottomSheetDialog = BottomSheetDialog(this, R.style.BottomSheetDialogTheme)
        bottomSheetDialog.setContentView(bottomSheetViewOfViewMyList)

        bottomSheetDialog.show()

        // Initialize the variables of Bottom Sheet and set data to them
        val textTextView: TextView = bottomSheetViewOfViewMyList.findViewById(R.id.bottomSheetLayout_viewMyList_textId)
        val pictureImageView: ImageView = bottomSheetViewOfViewMyList.findViewById(R.id.bottomSheetLayout_viewMyList_pictureId)
        val viewItemsButton: CardView = bottomSheetViewOfViewMyList.findViewById(R.id.bottomSheetLayout_viewMyList_viewItemsButtonId)

        textTextView.text = "$name added to My List successfully"

        Glide.with(this).load(pictureInBottomSheet).apply(RequestOptions.fitCenterTransform()).into(pictureImageView)

        viewItemsButton.setOnClickListener {
            var intent: Intent = Intent(this , MyListActivity::class.java)
            startActivity(intent)
        }

        // Close the bottom sheet after 4000 milliseconds using Coroutines or Thread.sleep()
        val duration: Long = 4000 // milliseconds

//        // Option 1: Using Coroutines (preferred)
//        lifecycleScope.launchWhenStarted {
//            delay(duration)
//            bottomSheetDialog.dismiss()
//        }

        // Option 2: Using Thread.sleep() (less preferred)
        // Use with caution due to potential main thread blocking!
        Thread {
            Thread.sleep(duration)
            runOnUiThread {
                bottomSheetDialog.dismiss()
            }
        }.start()

        // Option 3: Using Coroutines (less preferred due to potential memory leaks)
//        lifecycleScope.launch {
//            delay(duration)
//            bottomSheetDialog.dismiss()
//        }

    }

    private val userListReference: DatabaseReference = FirebaseDatabase.getInstance().reference.child("Users")
    private val myListReference: DatabaseReference = FirebaseDatabase.getInstance().reference.child("MyList")

    private fun addToMyListProcess() {
        myListBtn = findViewById(R.id.aboutMovieOrSerieActivity_myListButtonId)
        addToMyListIcon = findViewById(R.id.aboutMovieOrSerieActivity_myListButton_AddIconId)

        myListBtn.setOnClickListener {
            if (addToMyListIcon.tag.equals("notInMyList")) {
                var userReference: DatabaseReference = userListReference.child(firebaseAuth.uid!!)
                userReference.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            var username: String? = snapshot.child("username").getValue(String::class.java)

                            // Generate a unique key for the msId of mylist
                            var movieSerieId_ofMyList = myListReference.child(firebaseAuth.uid!!).push().key!!

                            // Get the current timestamp
                            var timestamp: Long = System.currentTimeMillis()
                            // Convert the timestamp to a readable date string
                            var date: String? = SimpleDateFormat("yyyy-dd-MM  HH:mm:ss", Locale.getDefault()).format(Date(timestamp))

                            val movieOrSerieData: HashMap<String , Any> = HashMap()
                            movieOrSerieData.put("id" , movieSerieId_ofMyList)
                            movieOrSerieData.put("picture" , picture)
                            movieOrSerieData.put("timestamp", date!!) // Or i can do : movieOrSerieData["timestamp"] = date

                            // Store movie or serie in the MyList column
                            myListReference.child(firebaseAuth.uid!!).child(name).setValue(movieOrSerieData)

                            // Show the bottom sheet
                            bottomSheetOfViewMyList(name , picture)

                            var modelOfMyList = ModelOfMyList()
                            modelOfMyList.apply {
                                this.name = name
                            }

                        }
                    }
                    override fun onCancelled(error: DatabaseError) {}
                })
            }

            else {
                // Remove the Movie From My list
                var userReference: DatabaseReference = userListReference.child(firebaseAuth.uid!!)
                userReference.addListenerForSingleValueEvent(object: ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            var username: String? = snapshot.child("username").getValue(String::class.java)
                            // get the id

                            myListReference.child(firebaseAuth.uid!!).child(name).removeValue()

//                            Toast.makeText(this@AboutMovieOrSerieActivity , name + " removed from My List successfully!", Toast.LENGTH_SHORT).show()
                        }
                    }
                    override fun onCancelled(error: DatabaseError) {}
                })
            }
        }

        isItInMylist()
    }

    private fun isItInMylist() {
        var firebaseUser: FirebaseUser = FirebaseAuth.getInstance().currentUser!!
        var userReference: DatabaseReference = userListReference.child(firebaseUser.uid)

        userReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {

                    var username: String = snapshot.child("username").getValue(String::class.java)!!

                    var reference = myListReference.child(firebaseAuth.uid!!)

                    reference.addValueEventListener(object: ValueEventListener{
                        override fun onDataChange(snapshot: DataSnapshot) {
                            if(snapshot.child(name).exists()) {
                                addToMyListIcon.setImageResource(R.drawable.checked_add_mylist_icon)
                                addToMyListIcon.tag = "inMyList"
                            }
                            else {
                                addToMyListIcon.setImageResource(R.drawable.my_list_icon)
                                addToMyListIcon.tag = "notInMyList"
                            }
                        }
                        override fun onCancelled(error: DatabaseError) {}
                    })
                }
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun commentsProcess() {
        commentEditText = findViewById(R.id.aboutMovieOrSerieActivity_commentEditTextId)
        sendCommentBtn = findViewById(R.id.aboutMovieOrSerieActivity_sendCommentBtnId)
        sendCommentBtnIcon = findViewById(R.id.aboutMovieOrSerieActivity_sendCommentBtnId_Icon)

        //EditText Process
        commentEditText.editText!!.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                // Check if the EditText is empty
                if (s.toString().trim().isEmpty()) {
                    // EditText is empty, set the button to gray
                    sendCommentBtnIcon.setImageResource(R.drawable.send_comment_gray_icon)
                    sendCommentBtn.isEnabled = false // Disable the button
                } else {
                    // EditText is not empty, set the button to red
                    sendCommentBtnIcon.setImageResource(R.drawable.send_comment_icon)
                    sendCommentBtn.isEnabled = true // Enable the button
                }
            }
        })

        if (sendCommentBtn.isEnabled) {
            //Send Button
            sendCommentBtn.setOnClickListener {
                var comment: String = commentEditText.editText!!.text.toString()
                if (!comment.isEmpty()) {
                    // Hide the keyboard
                    var imm: InputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(commentEditText.windowToken, 0)

                    var userReference: DatabaseReference = FirebaseDatabase.getInstance().reference.child("Users").child(firebaseAuth.uid!!)
                    userReference.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            if (snapshot.exists()) {
                                var username: String = snapshot.child("username").getValue(String::class.java)!!
                                var profilePic: String = snapshot.child("profilePic").getValue(String::class.java)!!

                                // Generate a unique key for the comment
                                var commentId: String = FirebaseDatabase.getInstance().reference.child("Comments").child(name).child(firebaseAuth.uid!!).push().key!!

                                // Get the current timestamp
                                var timestamp: Long = System.currentTimeMillis()
                                // Convert the timestamp to a readable date string
                                var date: String = SimpleDateFormat("yyyy-dd-MM  HH:mm", Locale.getDefault()).format(Date(timestamp))

                                var commentData: HashMap<String, Any> = HashMap()
                                commentData.put("username" , username)
                                commentData.put("profilePic" , profilePic)
                                commentData.put("comment", comment)
                                commentData.put("timestamp", date)

                                // Store the comment in the Comments column
                                FirebaseDatabase.getInstance().reference.child("Comments").child(name).child(firebaseAuth.uid!!).child(commentId).setValue(commentData)

                                // Clear the EditText after sending the comment
                                commentEditText.editText!!.setText("")
                                // Clear the focus after sending the comment
                                commentEditText.clearFocus()
                            }
                        }
                        override fun onCancelled(error: DatabaseError) {}
                    })
                }
            }
        }
        recyclerViewOfCommentsProcess()
    }

    private fun recyclerViewOfCommentsProcess() {
        var noCommentsYet: LinearLayout = findViewById(R.id.aboutMovieOrSerieActivity_noCommYet_linearLayoutId)
        recyclerViewOfComments = findViewById(R.id.aboutMovieOrSerieActivity_comments_recyclerViewId)
        recyclerViewOfComments.layoutManager = LinearLayoutManager(this)
        recyclerViewOfComments.setHasFixedSize(true)

        commentsList = ArrayList()

        var reference: DatabaseReference = FirebaseDatabase.getInstance().reference.child("Comments").child(name)
        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                commentsList.clear() // Clear the list to avoid duplicates when onDataChange is called multiple times

                if (snapshot.exists()) {
                    // There are comments
                    for(dataSnapshot: DataSnapshot in snapshot.children) {
                        var userId: String = dataSnapshot.key!! // Get the userId from the key
                        for (commentSnapshot: DataSnapshot in dataSnapshot.children) {
                            var commentId: String = commentSnapshot.key!! // Get the commentId from the key

                            var username: String = commentSnapshot.child("username").getValue(String::class.java)!!
                            var profilePic: String = commentSnapshot.child("profilePic").getValue(String::class.java)!!
                            var comment: String = commentSnapshot.child("comment").getValue(String::class.java)!!
                            var dateOfComment: String = commentSnapshot.child("timestamp").getValue(String::class.java)!!

                            var modelOfComments: ModelOfComments = ModelOfComments()
                            modelOfComments.profilePic = profilePic
                            modelOfComments.username = username
                            modelOfComments.comment = comment // Set the individual comment
                            modelOfComments.dateOfComment = dateOfComment

                            //I will send the name of Movie and commenId and usernameId too  , cuz i will need them when i want to delete the comment
                            modelOfComments.name = name
                            modelOfComments.commentId = commentId
                            modelOfComments.userId = userId

                            commentsList.add(modelOfComments)
                        }
                    }

                    // Order all comments by timestamp
                    Collections.sort(commentsList, object : Comparator<ModelOfComments?> {
                        override fun compare(comment1: ModelOfComments?, comment2: ModelOfComments?): Int {
                            return comment2!!.dateOfComment.compareTo(comment1!!.dateOfComment)
                        }
                    })

                    adapterOfComments = AdapterOfComments(this@AboutMovieOrSerieActivity , commentsList)
                    recyclerViewOfComments.adapter = adapterOfComments
                    adapterOfComments.notifyDataSetChanged()
                    recyclerViewOfComments.visibility = View.VISIBLE  // Show RecyclerView
                    noCommentsYet.visibility = View.GONE  // Hide "noCommentYet" view
                }
                else {
                    // No comments
                    recyclerViewOfComments.visibility = View.GONE  // Hide RecyclerView
                    noCommentsYet.visibility = View.VISIBLE  // Show "noCommentYet" view
                }

            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }
}