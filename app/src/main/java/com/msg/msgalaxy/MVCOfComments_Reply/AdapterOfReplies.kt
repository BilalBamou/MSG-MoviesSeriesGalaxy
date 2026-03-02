package com.msg.msgalaxy.MVCOfComments

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.msg.msgalaxy.MVCOfComments_PeopleWhoReacted.PeopleWhoReactedActivity
import com.msg.msgalaxy.MVCOfComments_Reply.ModelOfReplies
import com.msg.msgalaxy.MVCOfComments_Reply.ReplyActivity
import com.msg.msgalaxy.R
import de.hdodenhof.circleimageview.CircleImageView
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AdapterOfReplies(var activity: Activity, var repliesList: ArrayList<ModelOfReplies>) :
    RecyclerView.Adapter<AdapterOfReplies.MyViewHolder>() {

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var currentUser: FirebaseUser
    private lateinit var name: String

    init {
        firebaseAuth = FirebaseAuth.getInstance()
        currentUser = firebaseAuth.currentUser!!
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        var view: View = LayoutInflater.from(activity).inflate(R.layout.comments_card_item, parent, false)
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int = repliesList.size

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        var modelOfReplies: ModelOfReplies = repliesList.get(position)

        holder.username.text = modelOfReplies.username // or holder.username.setText(modelOfReplies.username)
        holder.comment.text = modelOfReplies.comment
        holder.dateOfComment.text = modelOfReplies.dateOfComment

        //Glide for downloaing the profile picture from Fb
        Glide.with(activity).load(modelOfReplies.profilePic)
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>, isFirstResource: Boolean): Boolean {
                    return false
                }

                override fun onResourceReady(resource: Drawable, model: Any, target: Target<Drawable>?, dataSource: DataSource, isFirstResource: Boolean): Boolean {
                    // Image loaded successfully
                    holder.lottieAnimationLoading.visibility = View.GONE
                    return false
                }
            }).into(holder.profilePicImageView)

        // this variables for deletion
        name = modelOfReplies.name

        var replyId = modelOfReplies.replyId
        var profilePic = modelOfReplies.profilePic
        var username = modelOfReplies.username
        var comment = modelOfReplies.comment
        var dataOfComment = modelOfReplies.dateOfComment

        var icon1 = holder.numberOfReactions_likeIcon
        var icon2 = holder.numberOfReactions_loveIcon
        var icon3 = holder.numberOfReactions_laughIcon
        var icon4 = holder.numberOfReactions_angryIcon

        // Reactions to Comments
        holder.reactionButton.setOnClickListener {
            if (holder.likeReactionIcon.tag == "selectedLike") {
                // If the reaction is already selected (Like), remove it
                removeReactionFromFirebase(replyId, "Likes")
            }
            else if (holder.loveReactionIcon.tag == "selectedLove") {
                // If the reaction is already selected (Love), remove it
                removeReactionFromFirebase(replyId, "Loves")
            }
            else if (holder.laughReactionIcon.tag == "selectedLaugh") {
                // If the reaction is already selected (Laugh), remove it
                removeReactionFromFirebase(replyId, "Laughs")
            }
            else if (holder.angryReactionIcon.tag == "selectedAngry") {
                // If the reaction is already selected (Angry), remove it
                removeReactionFromFirebase(replyId, "Angry")
            }
            else {
                // If no reaction is selected, add Like
                addLike_Love_Laugh_AngryToFirebase(replyId, "Likes")
            }
            // Call isLike and isLove and isLaugh and isAngry to update the state after the action
            isLike(name, replyId, holder.likeReactionIcon, holder.likeReactionName , holder.defaultLikeReactionName)
            isLove(name, replyId, holder.loveReactionIcon, holder.loveReactionName, holder.defaultLikeReactionName)
            isLaugh(name, replyId, holder.laughReactionIcon, holder.laughReactionName, holder.defaultLikeReactionName)
            isAngry(name, replyId, holder.angryReactionIcon, holder.angryReactionName, holder.defaultLikeReactionName)
        }

        holder.reactionButton.setOnLongClickListener(object: View.OnLongClickListener{
            override fun onLongClick(v: View?): Boolean {
                if (currentUser != null) {
                    showDialogOfReactions(activity ,replyId , holder.likeReactionIcon , holder.defaultLikeReactionName ,
                        holder.likeReactionName , holder.loveReactionName , holder.laughReactionName , holder.angryReactionName )
                }
                return true
            }
        })

        // Call isLike and isLove and isLaugh and isAngry to set the initial state of reaction views
        isLike(name, replyId, holder.likeReactionIcon, holder.likeReactionName , holder.defaultLikeReactionName)
        isLove(name, replyId, holder.loveReactionIcon, holder.loveReactionName, holder.defaultLikeReactionName)
        isLaugh(name, replyId, holder.laughReactionIcon, holder.laughReactionName, holder.defaultLikeReactionName)
        isAngry(name, replyId, holder.angryReactionIcon, holder.angryReactionName, holder.defaultLikeReactionName)

        // Update the number of reactions after the asynchronous operation is completed
        updateNumberOfReactions(replyId, holder.numberOfReactions , icon1, icon2, icon3, icon4 , holder.seeAllReactionsBtn)

        // About the replies Of Comment
        holder.replyButton.setOnClickListener {
            aboutReplyProcess( name , username , profilePic , comment , replyId , dataOfComment)
        }

        // Put Count of replies for each comment
        val replyReference: DatabaseReference = FirebaseDatabase.getInstance().reference
            .child("CommentReplies").child(name).child(replyId)
        replyReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(replySnapshot: DataSnapshot) {
                val replyCount: Long = replySnapshot.childrenCount
                if (replyCount == 0L) {
                    holder.numberOfReplies.visibility = View.GONE
                }
                else {
                    holder.numberOfReplies.visibility = View.VISIBLE
                    holder.numberOfReplies.text = replyCount.toString()
                }
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    inner class MyViewHolder(iv: View) : RecyclerView.ViewHolder(iv) {

        var username: TextView
        var profilePicImageView: CircleImageView
        var comment: TextView
        var commentBtn: RelativeLayout
        var dateOfComment: TextView
        var lottieAnimationLoading: LottieAnimationView

        // About reactions
        var reactionButton: CardView

        // Default Like
        var defaultLikeReactionName: TextView
        // Like
        var likeReactionName: TextView
        var likeReactionIcon: ImageView
        // Love
        var loveReactionName: TextView
        var loveReactionIcon: ImageView
        // Laugh
        var laughReactionName: TextView
        var laughReactionIcon: ImageView
        // Laugh
        var angryReactionName: TextView
        var angryReactionIcon: ImageView

        // Number of reactions
        var seeAllReactionsBtn: CardView
        var numberOfReactions: TextView
        var numberOfReactions_likeIcon: ImageView
        var numberOfReactions_loveIcon: ImageView
        var numberOfReactions_laughIcon: ImageView
        var numberOfReactions_angryIcon: ImageView

        // About reply
        var replyButton: CardView
        var numberOfReplies: TextView

        init {
            username = iv.findViewById(R.id.commentsCardItem_usernameId)
            profilePicImageView = iv.findViewById(R.id.commentsCardItem_profilePicId)
            comment = iv.findViewById(R.id.commentsCardItem_commentId)
            commentBtn = iv.findViewById(R.id.commentsCardItem_relativeLayout_CommentBtn)
            dateOfComment = iv.findViewById(R.id.commentsCardItem_dateId)
            lottieAnimationLoading = iv.findViewById(R.id.commentsCardItem_loadingLottie)
            reactionButton = iv.findViewById(R.id.commentsCardItem_reactionName_ButtonId)

            // About reactions
            // Default Like
            defaultLikeReactionName = iv.findViewById(R.id.commentsCardItem_reactionNameId_defaultLike)
            // Like
            likeReactionName = iv.findViewById(R.id.commentsCardItem_reactionNameId_like)
            likeReactionIcon = iv.findViewById(R.id.commentsCardItem_reactionIconId_like)
            // Love
            loveReactionName = iv.findViewById(R.id.commentsCardItem_reactionNameId_love)
            loveReactionIcon = iv.findViewById(R.id.commentsCardItem_reactionIconId_love)
            // Laugh
            laughReactionName = iv.findViewById(R.id.commentsCardItem_reactionNameId_laugh)
            laughReactionIcon = iv.findViewById(R.id.commentsCardItem_reactionIconId_laugh)
            // Angry
            angryReactionName = iv.findViewById(R.id.commentsCardItem_reactionNameId_angry)
            angryReactionIcon = iv.findViewById(R.id.commentsCardItem_reactionIconId_angry)

            // Number of reactions
            seeAllReactionsBtn = iv.findViewById(R.id.commentsCardItem_allReactions_ButtonId)
            numberOfReactions = iv.findViewById(R.id.commentsCardItem_numberOfReactions)
            numberOfReactions_likeIcon = iv.findViewById(R.id.commentsCardItem_numberOfreactionIconId_like)
            numberOfReactions_loveIcon = iv.findViewById(R.id.commentsCardItem_numberOfreactionIconId_love)
            numberOfReactions_laughIcon = iv.findViewById(R.id.commentsCardItem_numberOfreactionIconId_laugh)
            numberOfReactions_angryIcon = iv.findViewById(R.id.commentsCardItem_numberOfreactionIconId_angry)

            // About reply
            replyButton = iv.findViewById(R.id.commentsCardItem_reply_ButtonId)
            numberOfReplies = iv.findViewById(R.id.commentsCardItem_numberOfReplies)

            commentBtn.setOnClickListener {
                var position: Int = adapterPosition
                var modelOfReplies: ModelOfReplies = repliesList.get(position)

                var replyId = modelOfReplies.replyId
                var userId = modelOfReplies.userId
                var comment = modelOfReplies.comment
                var profilePic = modelOfReplies.profilePic
                var username = modelOfReplies.username

                if (currentUser != null && modelOfReplies.userId.equals(currentUser.uid)) {
                    showDialogOfDeleteEdit(activity,replyId, userId , comment , profilePic , username)
                }
                else {
                    // the comments is not of the current user
                    showDialogOfCopyReport_Reactions(activity,replyId, userId , comment , profilePic , username)
                }
            }

            profilePicImageView.setOnClickListener {
                var position: Int = adapterPosition
                var modelOfReplies: ModelOfReplies = repliesList.get(position)
                showDialogOfProfilePic(activity , modelOfReplies.profilePic)
            }
        }
    }

    private fun aboutReplyProcess(name: String , username: String, profilePic: String, comment: String, replyId: String, dataOfComment: String) {

        var intent: Intent = Intent(activity , ReplyActivity::class.java)
        intent.putExtra("name" , name )
        intent.putExtra("username" , username )
        intent.putExtra("profilePic" , profilePic )
        intent.putExtra("comment" , comment)
        intent.putExtra("commentId" , replyId )
        intent.putExtra("dataOfComment" , dataOfComment )

        activity.startActivity(intent)

    }

    private fun showDialogOfReactions(activity: Activity , replyId: String , reactionIcon: ImageView , defaultLikeReactionName: TextView ,
                                      likeReactionName: TextView , loveReactionName: TextView , laughReactionName: TextView , angryReactionName: TextView ) {

        val inflater = activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val popupView: View = inflater.inflate(R.layout.custom_alert_dialog_of_comment_reactions, null)

        val popupWindow = PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true)

        // Check if reactionName is not null before getting its location
        likeReactionName.let { reactionTextView ->
            val location = IntArray(2)
            reactionTextView.getLocationOnScreen(location)

            // Set the offset based on your requirements
            val yOffset = location[1] + popupWindow.height - 125 // Above the TextView with a vertical offset of 20dp

            popupWindow.showAtLocation(reactionTextView, Gravity.START or Gravity.TOP, 50, yOffset)
        }

        // Like reaction
        likeReactionProcess(popupView , popupWindow, replyId , reactionIcon , likeReactionName , defaultLikeReactionName)
        // Love reaction
        loveReactionProcess(popupView , popupWindow , replyId , reactionIcon , loveReactionName , defaultLikeReactionName)
        // Laugh reaction
        laughReactionProcess(popupView , popupWindow , replyId , reactionIcon , laughReactionName , defaultLikeReactionName)
        // Angry reaction
        angryReactionProcess(popupView , popupWindow , replyId , reactionIcon , angryReactionName , defaultLikeReactionName)

    }
    private fun likeReactionProcess(popupView: View , popupWindow: PopupWindow , replyId: String , reactionIcon: ImageView , likeReactionName: TextView , defaultLikeReactionName: TextView) {
        var likeReaction: CardView? = popupView.findViewById(R.id.like_comments_reactionId)
        likeReaction!!.setOnClickListener {
            popupWindow.dismiss()
            if (reactionIcon.tag.equals("unselectedLike")) {
                // Like the comment
                addLike_Love_Laugh_AngryToFirebase(replyId, "Likes")
                // Remove love or laugh or angry if it exists
                removeReactionFromFirebase(replyId, "Loves")
                removeReactionFromFirebase(replyId, "Laughs")
                removeReactionFromFirebase(replyId, "Angry")
            } else {
                // If "Love" or "Laugh" or "Angry" exists, change it to "Like"
                removeReactionFromFirebase(replyId, "Loves")
                removeReactionFromFirebase(replyId, "Laughs")
                removeReactionFromFirebase(replyId, "Angry")
                // Like the comment
                addLike_Love_Laugh_AngryToFirebase(replyId, "Likes")
            }
            // Call isLike to update the state after the action
            isLike(name, replyId, reactionIcon, likeReactionName , defaultLikeReactionName)
        }
    }
    private fun loveReactionProcess(popupView: View , popupWindow: PopupWindow , replyId: String , reactionIcon: ImageView , loveReactionName: TextView , defaultLikeReactionName: TextView) {
        var loveReaction: CardView? = popupView.findViewById(R.id.love_comments_reactionId)
        loveReaction!!.setOnClickListener {
            popupWindow.dismiss()
            if (reactionIcon.tag.equals("unselectedLove")) {
                // Love the comment
                addLike_Love_Laugh_AngryToFirebase(replyId, "Loves")
                // Remove like or laugh or angry if it exists
                removeReactionFromFirebase(replyId, "Likes")
                removeReactionFromFirebase(replyId, "Laughs")
                removeReactionFromFirebase(replyId, "Angry")
            } else {
                // If "Like" or "Laugh" or "Angry" exists, change it to "Love"
                removeReactionFromFirebase(replyId, "Likes")
                removeReactionFromFirebase(replyId, "Laughs")
                removeReactionFromFirebase(replyId, "Angry")
                // Love the comment
                addLike_Love_Laugh_AngryToFirebase(replyId, "Loves")
            }
            // Call isLove to update the state after the action
            isLove(name, replyId, reactionIcon, loveReactionName , defaultLikeReactionName)

        }
    }
    private fun laughReactionProcess(popupView: View , popupWindow: PopupWindow , replyId: String , reactionIcon: ImageView , laughReactionName: TextView , defaultLikeReactionName: TextView) {
        var laughReaction: CardView? = popupView.findViewById(R.id.laugh_comments_reactionId)
        laughReaction!!.setOnClickListener {
            popupWindow.dismiss()
            if (reactionIcon.tag.equals("unselectedLaugh")) {
                // Laugh to the comment
                addLike_Love_Laugh_AngryToFirebase(replyId, "Laughs")
                // Remove like or love or angry if it exists
                removeReactionFromFirebase(replyId, "Likes")
                removeReactionFromFirebase(replyId, "Loves")
                removeReactionFromFirebase(replyId, "Angry")
            } else {
                // If "Like" or "Love" or "Angry" exists, change it to "Laugh"
                removeReactionFromFirebase(replyId, "Likes")
                removeReactionFromFirebase(replyId, "Loves")
                removeReactionFromFirebase(replyId, "Angry")
                // Laugh to the comment
                addLike_Love_Laugh_AngryToFirebase(replyId, "Laughs")
            }
            // Call isLaugh to update the state after the action
            isLaugh(name, replyId, reactionIcon, laughReactionName , defaultLikeReactionName)

        }
    }

    private fun angryReactionProcess(popupView: View , popupWindow: PopupWindow , replyId: String , reactionIcon: ImageView , angryReactionName: TextView , defaultLikeReactionName: TextView) {
        var angryReaction: CardView? = popupView.findViewById(R.id.angry_comments_reactionId)
        angryReaction!!.setOnClickListener {
            popupWindow.dismiss()
            if (reactionIcon.tag.equals("unselectedAngry")) {
                // Angry to the comment
                addLike_Love_Laugh_AngryToFirebase(replyId, "Angry")
                // Remove like or love or laugh if it exists
                removeReactionFromFirebase(replyId, "Likes")
                removeReactionFromFirebase(replyId, "Loves")
                removeReactionFromFirebase(replyId, "Laughs")
            } else {
                // If "Like" or "Love" or "Laugh" exists, change it to "Angry"
                removeReactionFromFirebase(replyId, "Likes")
                removeReactionFromFirebase(replyId, "Loves")
                removeReactionFromFirebase(replyId, "Laughs")
                // Angry to the comment
                addLike_Love_Laugh_AngryToFirebase(replyId, "Angry")
            }
            // Call isAngry to update the state after the action
            isAngry(name, replyId, reactionIcon, angryReactionName , defaultLikeReactionName)
        }
    }

    private fun addLike_Love_Laugh_AngryToFirebase(replyId: String, reactionType: String) {
        var userReference: DatabaseReference = FirebaseDatabase.getInstance().reference.child("Users").child(firebaseAuth.uid!!)
        userReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    // Get the current timestamp
                    var timestamp: Long = System.currentTimeMillis()
                    // Convert the timestamp to a readable date string
                    var date: String = SimpleDateFormat("yyyy-dd-MM  HH:mm:ss", Locale.getDefault()).format(Date(timestamp))

                    var data: HashMap<String, Any> = HashMap()
                    data.put("timestamp", date)

                    // Store data in the CommentsReactions - Likes or Loves or Laughs or Angry
                    FirebaseDatabase.getInstance().reference.child("CommentsReactions").child(name).child(replyId).child(reactionType).child(currentUser.uid).setValue(data)
                }
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun removeReactionFromFirebase(replyId: String, reactionType: String) {
        // Remove the reaction from the specified type (Likes or Loves or Laughs or Angry)
        FirebaseDatabase.getInstance().reference.child("CommentsReactions").child(name).child(replyId).child(reactionType).child(currentUser.uid).removeValue()
    }

    private fun isLike(name: String, replyId: String, likeReactionIcon: ImageView, likeReactionName: TextView , defaultLikeReactionName: TextView) {
        var reference: DatabaseReference = FirebaseDatabase.getInstance().reference.child("CommentsReactions").child(name)
            .child(replyId).child("Likes")

        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val isLiked = snapshot.child(currentUser.uid).exists()
                if (isLiked) {
                    // User has liked this comment
                    likeReactionIcon.visibility = View.VISIBLE // like icon - visible
                    likeReactionName.visibility = View.VISIBLE // like txt - visible
                    defaultLikeReactionName.visibility = View.GONE // default like - gone
                    likeReactionIcon.tag = "selectedLike"
                } else {
                    // User has not liked this comment - so it will return to normal Like
                    likeReactionIcon.visibility = View.GONE // like icon - gone
                    likeReactionName.visibility = View.GONE // like txt - gone
                    defaultLikeReactionName.visibility = View.VISIBLE // default like - visible
                    likeReactionIcon.tag = "unselectedLike"
                }
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }
    private fun isLove(name: String, replyId: String, loveReactionIcon: ImageView, loveReactionName: TextView , defaultLikeReactionName: TextView) {
        var reference: DatabaseReference = FirebaseDatabase.getInstance().reference.child("CommentsReactions").child(name)
            .child(replyId).child("Loves")

        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val isLoved = snapshot.child(currentUser.uid).exists()
                if (isLoved) {
                    // User has loved this comment
                    loveReactionIcon.visibility = View.VISIBLE // love icon - visible
                    loveReactionName.visibility = View.VISIBLE // love txt - visible
                    defaultLikeReactionName.visibility = View.GONE // default like - gone
                    loveReactionIcon.tag = "selectedLove"
                } else {
                    // User has not loved this comment - so it will return to normal Like
                    loveReactionIcon.visibility = View.GONE // love icon - gone
                    loveReactionName.visibility = View.GONE // love txt - gone
                    loveReactionIcon.tag = "unselectedLove"
                }
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }
    private fun isLaugh(name: String, replyId: String, laughReactionIcon: ImageView, laughReactionName: TextView , defaultLikeReactionName: TextView) {
        var reference: DatabaseReference = FirebaseDatabase.getInstance().reference.child("CommentsReactions").child(name)
            .child(replyId).child("Laughs")

        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val isLaughed = snapshot.child(currentUser.uid).exists()
                if (isLaughed) {
                    // User has laughed this comment
                    laughReactionIcon.visibility = View.VISIBLE // laugh icon - visible
                    laughReactionName.visibility = View.VISIBLE // laugh txt - visible
                    defaultLikeReactionName.visibility = View.GONE // default like - gone
                    laughReactionIcon.tag = "selectedLaugh"
                } else {
                    // User has not laughed this comment - so it will return to normal Like
                    laughReactionIcon.visibility = View.GONE // laugh icon - gone
                    laughReactionName.visibility = View.GONE // laugh txt - gone
                    laughReactionIcon.tag = "unselectedLaugh"
                }
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }
    private fun isAngry(name: String, replyId: String, angryReactionIcon: ImageView, angryReactionName: TextView , defaultLikeReactionName: TextView) {
        var reference: DatabaseReference = FirebaseDatabase.getInstance().reference.child("CommentsReactions").child(name)
            .child(replyId).child("Angry")

        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val isAngry = snapshot.child(currentUser.uid).exists()
                if (isAngry) {
                    // User has got angry this comment
                    angryReactionIcon.visibility = View.VISIBLE // angry icon - visible
                    angryReactionName.visibility = View.VISIBLE // angry txt - visible
                    defaultLikeReactionName.visibility = View.GONE // default like - gone
                    angryReactionIcon.tag = "selectedAngry"
                } else {
                    // User has not got angry this comment - so it will return to normal Like
                    angryReactionIcon.visibility = View.GONE // angry icon - gone
                    angryReactionName.visibility = View.GONE // angry txt - gone
                    angryReactionIcon.tag = "unselectedAngry"
                }
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun updateNumberOfReactions(replyId: String, numberOfReactions: TextView , icon1: ImageView , icon2: ImageView , icon3: ImageView , icon4: ImageView , seeAllReactionsBtn: CardView) {
        val likesReference: DatabaseReference = FirebaseDatabase.getInstance().reference
            .child("CommentsReactions").child(name).child(replyId).child("Likes")
        val lovesReference: DatabaseReference = FirebaseDatabase.getInstance().reference
            .child("CommentsReactions").child(name).child(replyId).child("Loves")
        val laughsReference: DatabaseReference = FirebaseDatabase.getInstance().reference
            .child("CommentsReactions").child(name).child(replyId).child("Laughs")
        val angryReference: DatabaseReference = FirebaseDatabase.getInstance().reference
            .child("CommentsReactions").child(name).child(replyId).child("Angry")
        likesReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(likesSnapshot: DataSnapshot) {
                val likesCount: Long = likesSnapshot.childrenCount

                lovesReference.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(lovesSnapshot: DataSnapshot) {
                        val lovesCount: Long = lovesSnapshot.childrenCount

                        laughsReference.addValueEventListener(object : ValueEventListener {
                            override fun onDataChange(laughsSnapshot: DataSnapshot) {
                                val laughsCount: Long = laughsSnapshot.childrenCount

                                angryReference.addValueEventListener(object : ValueEventListener {
                                    override fun onDataChange(angrySnapshot: DataSnapshot) {
                                        val angryCount: Long = angrySnapshot.childrenCount

                                        // Calculate the total count (likes + loves + laughs + angry)
                                        val totalCount = likesCount + lovesCount + laughsCount + angryCount

                                        if (totalCount == 0L) {
                                            numberOfReactions.visibility = View.GONE
                                            icon1.visibility = View.GONE
                                            icon2.visibility = View.GONE
                                            icon3.visibility = View.GONE
                                            icon4.visibility = View.GONE
                                        }
                                        else {
                                            numberOfReactions.visibility = View.VISIBLE
                                            // Set the total count in the TextView
                                            numberOfReactions.text = totalCount.toString()

                                            // Set the visibility of icons based on counts and comparisons
                                            comparisonsBetweenReactions(likesCount , lovesCount , laughsCount , angryCount , icon1 , icon2 , icon3 , icon4)

                                            // See who did reactions to a comment
                                            seeAllReactionsBtn.setOnClickListener {
                                                seeAllReactionsProcess(replyId , totalCount , likesCount , lovesCount , laughsCount , angryCount)
                                            }
                                        }
                                    }
                                    override fun onCancelled(error: DatabaseError) {}
                                })
                            }
                            override fun onCancelled(error: DatabaseError) {}
                        })
                    }
                    override fun onCancelled(error: DatabaseError) {}
                })
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun seeAllReactionsProcess(replyId: String, totalCount: Long, likesCount: Long, lovesCount: Long, laughsCount: Long, angryCount: Long) {
        var intent = Intent(activity , PeopleWhoReactedActivity::class.java)
        intent.putExtra("name" , name)
        intent.putExtra("commentId" , replyId)
        intent.putExtra("totalCount" , totalCount)
        intent.putExtra("likesCount" , likesCount)
        intent.putExtra("lovesCount" , lovesCount)
        intent.putExtra("laughsCount" , laughsCount)
        intent.putExtra("angryCount" , angryCount)
        activity.startActivity(intent)
    }

    private fun comparisonsBetweenReactions(likesCount: Long, lovesCount: Long, laughsCount: Long, angryCount: Long, icon1: ImageView, icon2: ImageView, icon3: ImageView, icon4: ImageView) {
        if ( likesCount>0L && lovesCount==0L &&  laughsCount==0L && angryCount==0L ) {
            icon1.visibility = View.VISIBLE
            icon1.setImageResource(R.drawable.like_comments_reaction)
            icon2.visibility = View.GONE
            icon3.visibility = View.GONE
            icon4.visibility = View.GONE
        }
        else if ( lovesCount>0L && likesCount==0L &&  laughsCount==0L && angryCount==0L ) {
            icon1.visibility = View.VISIBLE
            icon1.setImageResource(R.drawable.love_comments_reaction)
            icon2.visibility = View.GONE
            icon3.visibility = View.GONE
            icon4.visibility = View.GONE
        }
        else if ( laughsCount>0L && likesCount==0L &&  lovesCount==0L && angryCount==0L ) {
            icon1.visibility = View.VISIBLE
            icon1.setImageResource(R.drawable.laugh_comments_reaction)
            icon2.visibility = View.GONE
            icon3.visibility = View.GONE
            icon4.visibility = View.GONE
        }
        else if ( angryCount>0L && likesCount==0L &&  lovesCount==0L && laughsCount==0L ) {
            icon1.visibility = View.VISIBLE
            icon1.setImageResource(R.drawable.angry_comments_reaction)
            icon2.visibility = View.GONE
            icon3.visibility = View.GONE
            icon4.visibility = View.GONE
        }
        else if ( likesCount>0L && lovesCount>0L &&  laughsCount==0L && angryCount==0L ) {
            icon1.visibility = View.VISIBLE
            icon1.setImageResource(R.drawable.like_comments_reaction)
            icon2.visibility = View.VISIBLE
            icon2.setImageResource(R.drawable.love_comments_reaction)
            icon3.visibility = View.GONE
            icon4.visibility = View.GONE
        }
        else if ( likesCount>0L && lovesCount==0L &&  laughsCount>0L && angryCount==0L ) {
            icon1.visibility = View.VISIBLE
            icon1.setImageResource(R.drawable.like_comments_reaction)
            icon2.visibility = View.VISIBLE
            icon2.setImageResource(R.drawable.laugh_comments_reaction)
            icon3.visibility = View.GONE
            icon4.visibility = View.GONE
        }
        else if ( likesCount>0L && lovesCount==0L &&  laughsCount==0L && angryCount>0L ) {
            icon1.visibility = View.VISIBLE
            icon1.setImageResource(R.drawable.like_comments_reaction)
            icon2.visibility = View.VISIBLE
            icon2.setImageResource(R.drawable.angry_comments_reaction)
            icon3.visibility = View.GONE
            icon4.visibility = View.GONE
        }
        else if ( likesCount==0L && lovesCount>0L &&  laughsCount>0L && angryCount==0L ) {
            icon1.visibility = View.VISIBLE
            icon1.setImageResource(R.drawable.love_comments_reaction)
            icon2.visibility = View.VISIBLE
            icon2.setImageResource(R.drawable.laugh_comments_reaction)
            icon3.visibility = View.GONE
            icon4.visibility = View.GONE
        }
        else if ( likesCount==0L && lovesCount>0L &&  laughsCount==0L && angryCount>0L ) {
            icon1.visibility = View.VISIBLE
            icon1.setImageResource(R.drawable.love_comments_reaction)
            icon2.visibility = View.VISIBLE
            icon2.setImageResource(R.drawable.angry_comments_reaction)
            icon3.visibility = View.GONE
            icon4.visibility = View.GONE
        }
        else if ( likesCount==0L && lovesCount==0L &&  laughsCount>0L && angryCount>0L ) {
            icon1.visibility = View.VISIBLE
            icon1.setImageResource(R.drawable.laugh_comments_reaction)
            icon2.visibility = View.VISIBLE
            icon2.setImageResource(R.drawable.angry_comments_reaction)
            icon3.visibility = View.GONE
            icon4.visibility = View.GONE
        }
        else if ( likesCount>0L && lovesCount>0L &&  laughsCount>0L && angryCount==0L ) {
            icon1.visibility = View.VISIBLE
            icon1.setImageResource(R.drawable.like_comments_reaction)
            icon2.visibility = View.VISIBLE
            icon2.setImageResource(R.drawable.love_comments_reaction)
            icon3.visibility = View.VISIBLE
            icon3.setImageResource(R.drawable.laugh_comments_reaction)
            icon4.visibility = View.GONE
        }
        else if ( likesCount>0L && lovesCount>0L &&  laughsCount==0L && angryCount>0L ) {
            icon1.visibility = View.VISIBLE
            icon1.setImageResource(R.drawable.like_comments_reaction)
            icon2.visibility = View.VISIBLE
            icon2.setImageResource(R.drawable.love_comments_reaction)
            icon3.visibility = View.VISIBLE
            icon3.setImageResource(R.drawable.angry_comments_reaction)
            icon4.visibility = View.GONE
        }
        else if ( likesCount>0L && lovesCount==0L &&  laughsCount>0L && angryCount>0L ) {
            icon1.visibility = View.VISIBLE
            icon1.setImageResource(R.drawable.like_comments_reaction)
            icon2.visibility = View.VISIBLE
            icon2.setImageResource(R.drawable.laugh_comments_reaction)
            icon3.visibility = View.VISIBLE
            icon3.setImageResource(R.drawable.angry_comments_reaction)
            icon4.visibility = View.GONE
        }
        else if ( likesCount==0L && lovesCount>0L &&  laughsCount>0L && angryCount>0L ) {
            icon1.visibility = View.VISIBLE
            icon1.setImageResource(R.drawable.love_comments_reaction)
            icon2.visibility = View.VISIBLE
            icon2.setImageResource(R.drawable.laugh_comments_reaction)
            icon3.visibility = View.VISIBLE
            icon3.setImageResource(R.drawable.angry_comments_reaction)
            icon4.visibility = View.GONE
        }
        else {
            icon1.visibility = View.VISIBLE
            icon2.visibility = View.VISIBLE
            icon3.visibility = View.VISIBLE
            icon4.visibility = View.VISIBLE
            icon1.setImageResource(R.drawable.like_comments_reaction)
            icon2.setImageResource(R.drawable.love_comments_reaction)
            icon3.setImageResource(R.drawable.laugh_comments_reaction)
            icon4.setImageResource(R.drawable.angry_comments_reaction)
        }
    }

    private fun showDialogOfProfilePic(activity: Activity, profilePic: String) {
        val bottomSheetViewOfShowProfilePic: View = LayoutInflater.from(activity).inflate( R.layout.bottom_sheet_of_show_profilepic,
            activity.findViewById(R.id.bottomSheetLayout_showProfilePic_containerId) )


        val bottomSheetDialog: BottomSheetDialog = BottomSheetDialog(activity, R.style.BottomSheetDialogTheme)
        bottomSheetDialog.setContentView(bottomSheetViewOfShowProfilePic)

        // Set up BottomSheetDialog's behavior
        // Hadi bach ndir ch7al ykon fl Height dyal BottomSheet f Screen dl phone when i click dik sa3a n9dar ndir liha scroll 3adi
        val bottomSheetBehavior: BottomSheetBehavior<FrameLayout> = bottomSheetDialog.behavior
        bottomSheetBehavior.peekHeight = activity.resources.displayMetrics.heightPixels // hadi bach tchad the whole screen

        bottomSheetDialog.show()

        // Initialize the variables of Bottom Sheet and set data to them
        val profilePicImageView: CircleImageView = bottomSheetViewOfShowProfilePic.findViewById(R.id.bottomSheetLayout_showProfilePic_profilePicId)

        Glide.with(activity).load(profilePic).apply(RequestOptions.fitCenterTransform()).into(profilePicImageView)

    }

    private fun showDialogOfDeleteEdit(activity: Activity, replyId: String, userId: String , comment: String , profilePic: String , username: String) {
        var bottomSheetView: View = LayoutInflater.from(activity).inflate(R.layout.bottom_sheet_of_delete_and_edit_comment,
            activity.findViewById(R.id.bottomSheetLayout_deleteComment_containerId))

        var bottomSheetDialog: BottomSheetDialog = BottomSheetDialog(activity, R.style.BottomSheetDialogTheme)

        bottomSheetDialog.setContentView(bottomSheetView)
        bottomSheetDialog.show()

        // for Copying the comment
        var copyBtn: LinearLayout? = bottomSheetDialog.findViewById(R.id.bottomSheetLayout_copyComment_copyBtnId)
        copyBtn!!.visibility = View.VISIBLE
        copyBtn.setOnClickListener {
            copyCommentDialog(comment , bottomSheetDialog)
        }

        // for Deleting the comment
        var deleteBtn: LinearLayout? = bottomSheetDialog.findViewById(R.id.bottomSheetLayout_deleteComment_deleteBtnId)
        deleteBtn!!.setOnClickListener {
            deleteDialog(userId , replyId , bottomSheetDialog)
        }

        // for Editing the comment
        var editBtn: LinearLayout? = bottomSheetDialog.findViewById(R.id.bottomSheetLayout_editComment_editBtnId)
        editBtn!!.setOnClickListener {
            editDialog(userId , replyId , comment , profilePic , username , bottomSheetDialog)
        }
    }

    private fun deleteDialog(userId: String, replyId: String, bottomSheetDialog: BottomSheetDialog) {
        if (currentUser != null && userId.equals(currentUser.uid)) {
            var alertDialog: AlertDialog.Builder = AlertDialog.Builder(activity)
                .setCancelable(false)
                .setTitle("DELETE")
                .setMessage("Are you sure you want to delete this comment ?")
                .setPositiveButton("DELETE", object: DialogInterface.OnClickListener {
                    override fun onClick(dialog: DialogInterface?, which: Int) {
                        deleteFromFB(replyId , bottomSheetDialog)
                    }
                })
            alertDialog.setNegativeButton("Cancel", object: DialogInterface.OnClickListener {
                override fun onClick(dialog: DialogInterface?, which: Int) {
                    dialog!!.cancel()
                }
            })

            var dialog: AlertDialog = alertDialog.create()

            // Change The Color of LOG OUT Btn
            dialog.setOnShowListener {
                var positiveButton: Button = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                positiveButton.setTextColor(Color.parseColor("#FF002E"))
                var negativeButton: Button = dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
                negativeButton.setTextColor(Color.parseColor("#F0F3F8"))
            }
            dialog.show()
        }
    }

    private fun deleteFromFB(replyId: String, bottomSheetDialog: BottomSheetDialog) {
        var userReference: DatabaseReference = FirebaseDatabase.getInstance().reference.child("Users").child(firebaseAuth.uid!!)
        userReference.addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    var ref: DatabaseReference = FirebaseDatabase.getInstance().getReference("CommentReplies").child(name)
                    ref.addListenerForSingleValueEvent(object: ValueEventListener{
                        override fun onDataChange(snapshot: DataSnapshot) {
                            for (commentSnapshot: DataSnapshot in snapshot.children) {
                                var commentKey: String = commentSnapshot.key!!
                                for (replySnapshot in commentSnapshot.children) {
                                    var replyKey: String = replySnapshot.key!!
                                    if (replyKey != null && replyKey.equals(replyId)) {
                                        replySnapshot.ref.removeValue()
                                        Toast.makeText(activity, "You have successfully deleted your comment!", Toast.LENGTH_SHORT).show()
                                        bottomSheetDialog.dismiss()
                                        return
                                    }
                                }
                            }
                            // If the loop completes without finding the comment
                            Toast.makeText(activity, "Comment not found!", Toast.LENGTH_SHORT).show()
                            bottomSheetDialog.dismiss()
                        }

                        override fun onCancelled(error: DatabaseError) {}
                    })
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun editDialog(userId: String, replyId: String , comment: String , profilePicP: String, usernameP: String , bottomSheetDialog: BottomSheetDialog) {
        if (currentUser != null && userId.equals(currentUser.uid)) {
            bottomSheetDialog.dismiss()

            var builder: AlertDialog.Builder = AlertDialog.Builder(activity)
            var view: View = activity.layoutInflater.inflate(R.layout.custom_alert_dialog_of_edit_comment , null)

            // Initialize the variables
            var profilePic: CircleImageView = view.findViewById(R.id.customAlertDialogOf_editComment_profilePicId)
            var username: TextView = view.findViewById(R.id.customAlertDialogOf_editComment_usernameId)
            var editCommentEditTxt : TextInputLayout = view.findViewById(R.id.customAlertDialogOf_editComment_textInputLayoutId)
            var saveBtn: CardView = view.findViewById(R.id.customAlertDialogOf_editComment_saveButtonId)
            var cancelBtn: CardView = view.findViewById(R.id.customAlertDialogOf_editComment_cancelButtonId)
            var saveBtn_saveTxt: TextView = view.findViewById(R.id.customAlertDialogOf_editComment_saveButtonId_textView)
            var saveBtn_loadingAnimation: LottieAnimationView = view.findViewById(R.id.customAlertDialogOf_editComment_saveButtonId_loadingLottie)

            // Set the old comment to the Edittext
            editCommentEditTxt.editText!!.setText(comment)
            username.text = usernameP
            Glide.with(activity).load(profilePicP).apply(RequestOptions.fitCenterTransform()).into(profilePic)

            builder.setView(view)
            var dialog: AlertDialog = builder.create()
            dialog.show()

            // for Saving the comment
            saveBtn.setOnClickListener {
                var newComment: String = editCommentEditTxt.editText!!.text.toString()

                if(newComment.isEmpty()) {
                    Toast.makeText(activity, "Please enter your new comment", Toast.LENGTH_SHORT).show()
                }
                else {
                    saveBtn_saveTxt.visibility = View.GONE
                    saveBtn_loadingAnimation.visibility = View.VISIBLE
                    // Hide the keyboard
                    var inputMethodManager: InputMethodManager = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    inputMethodManager.hideSoftInputFromWindow(saveBtn.getWindowToken(), 0)
                    editFromFB(replyId , newComment , dialog , saveBtn_saveTxt , saveBtn_loadingAnimation)
                }
            }

            // for Canceling
            cancelBtn.setOnClickListener {
                dialog.dismiss()
            }
        }
    }

    private fun editFromFB(replyId: String , newComment: String , dialog: AlertDialog , saveBtn_saveTxt: TextView , saveBtn_loadingAnimation: LottieAnimationView) {
        var userReference: DatabaseReference = FirebaseDatabase.getInstance().reference.child("Users").child(firebaseAuth.uid!!)
        userReference.addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {

                    var ref: DatabaseReference = FirebaseDatabase.getInstance().getReference("CommentReplies").child(name)
                    ref.addListenerForSingleValueEvent(object: ValueEventListener{
                        override fun onDataChange(snapshot: DataSnapshot) {
                            for (commentSnapshot: DataSnapshot in snapshot.children) {
                                var commentKey: String = commentSnapshot.key!!
                                for (replySnapshot: DataSnapshot in commentSnapshot.children) {
                                    var replyKey: String = replySnapshot.key!!
                                    if (replyKey != null && replyKey.equals(replyId)) {
                                        if (replySnapshot.child(firebaseAuth.uid!!).hasChild("comment")) {
                                            // Update the oldComment to newComment
                                            replySnapshot.child(firebaseAuth.uid!!).child("comment").ref.setValue(newComment)
                                            Toast.makeText(activity, "You have successfully edited your comment!", Toast.LENGTH_SHORT).show()
                                            dialog.dismiss()

                                            saveBtn_saveTxt.visibility = View.VISIBLE
                                            saveBtn_loadingAnimation.visibility = View.GONE
                                        }
                                        return
                                    }
                                }
                            }
                        }
                        override fun onCancelled(error: DatabaseError) {}
                    })
                }
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun showDialogOfCopyReport_Reactions(activity: Activity, replyId: String, userId: String , comment: String , profilePic: String , username: String) {
        var bottomSheetView: View = LayoutInflater.from(activity).inflate(R.layout.bottom_sheet_of_delete_and_edit_comment,
            activity.findViewById(R.id.bottomSheetLayout_deleteComment_containerId))

        var bottomSheetDialog: BottomSheetDialog = BottomSheetDialog(activity, R.style.BottomSheetDialogTheme)

        bottomSheetDialog.setContentView(bottomSheetView)
        bottomSheetDialog.show()

        // Initialize the variables
        var delete_copy_icon: ImageView? = bottomSheetDialog.findViewById(R.id.bottomSheetLayout_delete_copy_iconOneId)
        var delete_copy_text: TextView? = bottomSheetDialog.findViewById(R.id.bottomSheetLayout_delete_copy_textOneId)
        var edit_report_icon: ImageView? = bottomSheetDialog.findViewById(R.id.bottomSheetLayout_edit_report_iconTwoId)
        var edit_report_text: TextView? = bottomSheetDialog.findViewById(R.id.bottomSheetLayout_edit_report_textTwoId)

        // set icons and titles
        delete_copy_icon!!.setImageResource(R.drawable.copy_comment_icon)
        delete_copy_text!!.text = "Copy"

        edit_report_icon!!.setImageResource(R.drawable.report_comment_icon)
        edit_report_text!!.text = "Report comment"

        // for Deleting the comment
        var copyCommentBtn: LinearLayout? = bottomSheetDialog.findViewById(R.id.bottomSheetLayout_deleteComment_deleteBtnId)

        copyCommentBtn!!.setOnClickListener {
            copyCommentDialog(comment , bottomSheetDialog)
        }

        // for Editing the comment
        var reportCommentBtn: LinearLayout? = bottomSheetDialog.findViewById(R.id.bottomSheetLayout_editComment_editBtnId)
        reportCommentBtn!!.setOnClickListener {
            reportCommentDialog(userId , replyId , comment , profilePic , username , bottomSheetDialog)
        }
    }

    private fun copyCommentDialog(comment: String , bottomSheetDialog: BottomSheetDialog) {
        val clipboard = activity.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

        val clip: ClipData = ClipData.newPlainText("Comment", comment)
        clipboard.setPrimaryClip(clip)

        Toast.makeText(activity, "Comment copied to clipboard", Toast.LENGTH_SHORT).show()
        bottomSheetDialog.dismiss()
    }

    private fun reportCommentDialog(userId: String, replyId: String, comment: String, profilePicP: String, usernameP: String, bottomSheetDialog: BottomSheetDialog) {

        if (currentUser != null && !userId.equals(currentUser.uid)) {

            var builder: AlertDialog.Builder = AlertDialog.Builder(activity)
            var view: View = activity.layoutInflater.inflate(R.layout.custom_alert_dialog_of_report_comment , null)

            // Initialize the variables
            var profilePic: CircleImageView = view.findViewById(R.id.customAlertDialogOf_reportComment_profilePicId)
            var username: TextView = view.findViewById(R.id.customAlertDialogOf_reportComment_usernameId)
            var reportedComment: TextView = view.findViewById(R.id.customAlertDialogOf_reportComment_reportedCommentId)
            var reasonForReportEditTxt : TextInputLayout = view.findViewById(R.id.customAlertDialogOf_reportComment_textInputLayoutId)
            var submitBtn: CardView = view.findViewById(R.id.customAlertDialogOf_reportComment_submitButtonId)
            var cancelBtn: CardView = view.findViewById(R.id.customAlertDialogOf_reportComment_cancelButtonId)
            var submitBtn_saveTxt: TextView = view.findViewById(R.id.customAlertDialogOf_reportComment_submitButtonId_textView)
            var submitBtn_loadingAnimation: LottieAnimationView = view.findViewById(R.id.customAlertDialogOf_reportComment_submitButtonId_loadingLottie)

            // Set data
            username.text = usernameP
            Glide.with(activity).load(profilePicP).apply(RequestOptions.fitCenterTransform()).into(profilePic)
            reportedComment.text = comment

            builder.setView(view)
            var dialog: AlertDialog = builder.create()
            dialog.show()

            // for Saving the comment
            submitBtn.setOnClickListener {
                var reasonForReport: String = reasonForReportEditTxt.editText!!.text.toString()

                if(reasonForReport.isEmpty()) {
                    Toast.makeText(activity, "Please enter your reason for report.", Toast.LENGTH_SHORT).show()
                }
                else {
                    // Hide the keyboard
                    var inputMethodManager: InputMethodManager = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    inputMethodManager.hideSoftInputFromWindow(submitBtn.getWindowToken(), 0)

                    Toast.makeText(activity, "Report submitted successfully. Thank you for your feedback!", Toast.LENGTH_SHORT).show()
//                    Snackbar.make(view, "Report submitted successfully. Thank you for your feedback!", Snackbar.LENGTH_SHORT).show()
                    dialog.dismiss()
                    bottomSheetDialog.dismiss()
                }
            }
            // for Canceling
            cancelBtn.setOnClickListener {
                dialog.dismiss()
            }
        }

    }
}