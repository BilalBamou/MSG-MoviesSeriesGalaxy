package com.msg.msgalaxy.MVCOfComments_PeopleWhoReacted.forLikes

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
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
import com.msg.msgalaxy.R
import de.hdodenhof.circleimageview.CircleImageView

class AdapterOfLikes (var context: Context, var likeList: ArrayList<ModelOfLikes>) : RecyclerView.Adapter<AdapterOfLikes.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.people_who_reacted_card_item, parent, false)
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int = likeList.size

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        var modelOfLikes: ModelOfLikes = likeList.get(position)
        holder.username.text = modelOfLikes.username
        holder.icon.setImageResource(R.drawable.like_comments_reaction)

        //Glide for downloaing the profile picture from Fb
        Glide.with(context).load(modelOfLikes.profilePic).listener(object :
            RequestListener<Drawable> {
            override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>, isFirstResource: Boolean): Boolean {
                holder.lottieAnimationLoading.visibility = View.GONE
                return false
            }
            override fun onResourceReady(resource: Drawable, model: Any, target: Target<Drawable>, dataSource: DataSource, isFirstResource: Boolean): Boolean {
                // Image loaded successfully
                holder.lottieAnimationLoading.visibility = View.GONE
                return false
            }
        }).into(holder.profilePicImageView)
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) , View.OnClickListener {
        var linearItem: LinearLayout
        lateinit var profilePicImageView: CircleImageView
        lateinit var username: TextView
        lateinit var icon: ImageView
        lateinit var lottieAnimationLoading: LottieAnimationView

        init {
            linearItem = itemView.findViewById(R.id.peopleWhoReactedCardItem_linearLayoutId)
            profilePicImageView = itemView.findViewById(R.id.peopleWhoReactedCardItem_profilePicId)
            username = itemView.findViewById(R.id.peopleWhoReactedCardItem_usernameId)
            icon = itemView.findViewById(R.id.peopleWhoReactedCardItem_reactionIconId)
            lottieAnimationLoading = itemView.findViewById(R.id.peopleWhoReactedCardItem_loadingLottie)

            linearItem.setOnClickListener(this)
            profilePicImageView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            when(v!!.id) {
                R.id.peopleWhoReactedCardItem_linearLayoutId -> showPic()
                R.id.peopleWhoReactedCardItem_profilePicId -> showPic()
            }
        }

        private fun showPic() {
            var position: Int = adapterPosition
            var model = likeList.get(position)

            val bottomSheetViewOfShowProfilePic: View = LayoutInflater.from(context).inflate( R.layout.bottom_sheet_of_show_profilepic,
                itemView.findViewById(R.id.bottomSheetLayout_showProfilePic_containerId) )


            val bottomSheetDialog: BottomSheetDialog = BottomSheetDialog(context, R.style.BottomSheetDialogTheme)
            bottomSheetDialog.setContentView(bottomSheetViewOfShowProfilePic)

            // Set up BottomSheetDialog's behavior
            // Hadi bach ndir ch7al ykon fl Height dyal BottomSheet f Screen dl phone when i click dik sa3a n9dar ndir liha scroll 3adi
            val bottomSheetBehavior: BottomSheetBehavior<FrameLayout> = bottomSheetDialog.behavior
            bottomSheetBehavior.peekHeight = context.resources.displayMetrics.heightPixels // hadi bach tchad the whole screen

            bottomSheetDialog.show()

            // Initialize the variables of Bottom Sheet and set data to them
            val profilePicImageView: CircleImageView = bottomSheetViewOfShowProfilePic.findViewById(R.id.bottomSheetLayout_showProfilePic_profilePicId)

            Glide.with(context).load(model.profilePic).apply(RequestOptions.fitCenterTransform()).into(profilePicImageView)
        }
    }
}