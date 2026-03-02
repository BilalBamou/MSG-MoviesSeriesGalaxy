package com.msg.msgalaxy.MVCOfComments_Reply

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.msg.msgalaxy.MVCOfComments.AdapterOfReplies
import com.msg.msgalaxy.MVCOfComments.ModelOfComments
import com.msg.msgalaxy.R
import de.hdodenhof.circleimageview.CircleImageView
import java.text.SimpleDateFormat
import java.util.Collections
import java.util.Date
import java.util.Locale

class ReplyActivity : AppCompatActivity() {

    private lateinit var firebaseAuth: FirebaseAuth

    private lateinit var arrowBack: CardView

    private lateinit var usernameTxtView: TextView
    private lateinit var profilePicImageView: CircleImageView
    private lateinit var lottieAnimationLoading: LottieAnimationView
    private lateinit var commentTxtView: TextView
    private lateinit var dataOfCommentTxtView: TextView

    //About Replies
    private lateinit var commentEditText: TextInputLayout
    private lateinit var sendCommentBtn: CardView
    private lateinit var sendCommentBtnIcon: ImageView
    private lateinit var recyclerViewOfComments: RecyclerView
    private lateinit var adapterOfReplies: AdapterOfReplies
    private lateinit var repliesList: ArrayList<ModelOfReplies>

    private var name: String? = null
    private var username: String? = null
    private var profilePic: String? = null
    private var comment: String? = null
    private var mainCommentId: String? = null
    private var dataOfComment: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reply)

        firebaseAuth = FirebaseAuth.getInstance()

        // Initialize the variables
        arrowBack = findViewById(R.id.replyActivity_arrowBackId)
        usernameTxtView = findViewById(R.id.replyActivity_usernameId)
        profilePicImageView = findViewById(R.id.replyActivity_profilePicId)
        lottieAnimationLoading = findViewById(R.id.replyActivity_loadingLottie)
        commentTxtView = findViewById(R.id.replyActivity_commentId)
        dataOfCommentTxtView = findViewById(R.id.replyActivity_dateId)

        // Get data
        var bundle = intent.extras
        name = bundle!!.getString("name")
        username = bundle.getString("username")
        profilePic = bundle.getString("profilePic")
        comment = bundle.getString("comment")
        mainCommentId = bundle.getString("commentId")
        dataOfComment = bundle.getString("dataOfComment")

        // Set the data of the Main Comment
        setDataOfMainComment()

        commentsProcess()

        arrowBackProcess()
    }

    private fun setDataOfMainComment() {
        usernameTxtView.text = username
        commentTxtView.text = comment
        dataOfCommentTxtView.text = dataOfComment

        Glide.with(this).load(profilePic)
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>, isFirstResource: Boolean): Boolean {
                    return false
                }
                override fun onResourceReady(resource: Drawable, model: Any, target: Target<Drawable>?, dataSource: DataSource, isFirstResource: Boolean): Boolean {
                    // Image loaded successfully
                    lottieAnimationLoading.visibility = View.GONE
                    return false
                }
            }).into(profilePicImageView)
    }

    private fun commentsProcess() {
        commentEditText = findViewById(R.id.replyActivity_commentEditTextId)
        sendCommentBtn = findViewById(R.id.replyActivity_sendCommentBtnId)
        sendCommentBtnIcon = findViewById(R.id.replyActivity_sendCommentBtnId_Icon)

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

                                // Generate a unique key for the reply comment
                                var replycommentId: String = FirebaseDatabase.getInstance().reference.child("CommentReplies").child(name!!).child(mainCommentId!!).push().key!!

                                // Get the current timestamp
                                var timestamp: Long = System.currentTimeMillis()
                                // Convert the timestamp to a readable date string
                                var date: String = SimpleDateFormat("yyyy-dd-MM  HH:mm", Locale.getDefault()).format(Date(timestamp))

                                var commentData: HashMap<String, Any> = HashMap()
                                commentData.put("username", username)
                                commentData.put("profilePic", profilePic)
                                commentData.put("comment", comment)
                                commentData.put("timestamp", date)

                                // Store the comment in the Comments column
                                FirebaseDatabase.getInstance().reference.child("CommentReplies").child(name!!).child(mainCommentId!!).child(replycommentId).child(firebaseAuth.uid!!).setValue(commentData)

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
        var noReplyYet: LinearLayout = findViewById(R.id.replyActivity_noCommYet_linearLayoutId)
        recyclerViewOfComments = findViewById(R.id.replyActivity_comments_recyclerViewId)
        recyclerViewOfComments.layoutManager = LinearLayoutManager(this)
        recyclerViewOfComments.setHasFixedSize(true)

        repliesList = ArrayList()

            var reference: DatabaseReference = FirebaseDatabase.getInstance().reference.child("CommentReplies").child(name!!)
            reference.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {

                    repliesList.clear() // Clear the list to avoid duplicates when onDataChange is called multiple times

                    if (snapshot.exists()) {
                        // There are comments
                        for(dataSnapshot: DataSnapshot in snapshot.children) {
                            var mainCommentId1: String = dataSnapshot.key!! // Get the mainCommentId from the key
                            for (commentSnapshot: DataSnapshot in dataSnapshot.children) {
                                var replyId = commentSnapshot.key!! // get replyId
                                if (mainCommentId1.equals(mainCommentId)) {
                                    for (replySnapshot: DataSnapshot in commentSnapshot.children) {
                                        var userId: String = replySnapshot.key!! // Get the userId from the key

                                        var username: String = replySnapshot.child("username").getValue(String::class.java)!!
                                        var profilePic: String = replySnapshot.child("profilePic").getValue(String::class.java)!!
                                        var comment: String = replySnapshot.child("comment").getValue(String::class.java)!!
                                        var dateOfComment: String = replySnapshot.child("timestamp").getValue(String::class.java)!!

                                        var modelOfReplies: ModelOfReplies = ModelOfReplies()
                                        modelOfReplies.username = username
                                        modelOfReplies.profilePic = profilePic
                                        modelOfReplies.comment = comment // Set the individual comment
                                        modelOfReplies.dateOfComment = dateOfComment

                                        //I will send the name of Movie and replyId and usernameId too ... , cuz i will need them when i want to delete the comment
                                        modelOfReplies.name = name!!
                                        modelOfReplies.replyId = replyId
                                        modelOfReplies.userId = userId

                                        repliesList.add(modelOfReplies)
                                    }
                                }
                                else {
                                    // No comments
                                    recyclerViewOfComments.visibility = View.GONE  // Hide RecyclerView
                                    noReplyYet.visibility = View.VISIBLE
                                }
                            }
                        }

                        // Order all comments by timestamp
                        Collections.sort(repliesList, object : Comparator<ModelOfReplies?> {
                            override fun compare(comment1: ModelOfReplies?, comment2: ModelOfReplies?): Int {
                                return comment1!!.dateOfComment.compareTo(comment2!!.dateOfComment)
                            }
                        })

                        adapterOfReplies = AdapterOfReplies(this@ReplyActivity , repliesList)
                        recyclerViewOfComments.adapter = adapterOfReplies
                        adapterOfReplies.notifyDataSetChanged()
                        recyclerViewOfComments.visibility = View.VISIBLE  // Show RecyclerView
                        noReplyYet.visibility = View.GONE
                    }
                    else {
                        // No comments
                        recyclerViewOfComments.visibility = View.GONE  // Hide RecyclerView
                        noReplyYet.visibility = View.VISIBLE
                    }
                }
                override fun onCancelled(error: DatabaseError) {}
            })
    }

    private fun arrowBackProcess() {
        arrowBack.setOnClickListener {
            onBackPressed()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }
}