package com.msg.msgalaxy.MVCOfFires_dislikes.forFires

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
import com.msg.msgalaxy.MVCOfFires_dislikes.forDislikes.AdapterOfDislikes
import com.msg.msgalaxy.MVCOfFires_dislikes.forDislikes.ModelOfDislikes
import com.msg.msgalaxy.R
import de.hdodenhof.circleimageview.CircleImageView

class AdapterOfFires(var context: Context, var firesList: ArrayList<ModelOfFires>) : RecyclerView.Adapter<AdapterOfFires.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        var view: View = LayoutInflater.from(context).inflate(R.layout.fires_dislikes_card_item, parent, false)
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int = firesList.size

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        var modelOfFires: ModelOfFires = firesList.get(position)
        holder.username!!.text = modelOfFires.username
        holder.fireIcon!!.setImageResource(R.drawable.selected_fire_icon)

        //Glide for downloaing the profile picture from Fb
        Glide.with(context).load(modelOfFires.profilePic).listener(object : RequestListener<Drawable> {
            override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>, isFirstResource: Boolean): Boolean {
                holder.lottieAnimationLoading!!.visibility = View.GONE
                return false
            }
            override fun onResourceReady(resource: Drawable, model: Any, target: Target<Drawable>, dataSource: DataSource, isFirstResource: Boolean): Boolean {
                // Image loaded successfully
                holder.lottieAnimationLoading!!.visibility = View.GONE
                return false
            }
        }).into(holder.profilePicImageView!!)
    }

    inner class MyViewHolder(i: View) : RecyclerView.ViewHolder(i) {
        lateinit var linearItem: LinearLayout
        var profilePicImageView: CircleImageView? = null
        var username: TextView? = null
        var fireIcon: ImageView? = null
        var lottieAnimationLoading: LottieAnimationView? = null

        init {
            linearItem = itemView.findViewById(R.id.firesDislikesCardItem_linearLayoutId)
            profilePicImageView = i.findViewById(R.id.firesDislikesCardItem_profilePicId)
            username = i.findViewById(R.id.firesDislikesCardItem_usernameId)
            fireIcon = i.findViewById(R.id.firesDislikesCardItem_firesOrDislikesIconId)
            lottieAnimationLoading = i.findViewById(R.id.firesDislikesCardItem_loadingLottie)

            profilePicImageView!!.setOnClickListener {
                showPic()
            }
            linearItem.setOnClickListener {
                showPic()
            }
        }

        private fun showPic() {
            var position: Int = adapterPosition
            var modelOfFires = firesList.get(position)

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

            Glide.with(context).load(modelOfFires.profilePic).apply(RequestOptions.fitCenterTransform()).into(profilePicImageView)
        }
    }
}