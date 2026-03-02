package com.msg.msgalaxy.MVCOfFifthList_homeFragment

import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
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
import com.msg.msgalaxy.AboutMovieOrSerieActivity
import com.msg.msgalaxy.MVCOfFirstList_homeFragment.AdapterOfFirstList
import com.msg.msgalaxy.MVCOfFourthList_homeFragment.ModelOfFourthList
import com.msg.msgalaxy.R

class AdapterOfFifthList(var context: Context, var fifthListList: ArrayList<ModelOfFifthList>) : RecyclerView.Adapter<AdapterOfFifthList.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        var view: View = LayoutInflater.from(context).inflate(R.layout.home_fragment_fifth_list_card_item , parent , false)
        var myViewHolder = MyViewHolder(view)
        return myViewHolder
    }

    override fun getItemCount(): Int = fifthListList.size

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        var modelOfFifthList = fifthListList.get(position)

        holder.name!!.text = modelOfFifthList.name

        holder.yearOfOscarWinnig!!.text = "${modelOfFifthList.year.toInt() + 1}"

        Glide.with(context).load(modelOfFifthList.picture).apply(RequestOptions.fitCenterTransform()).listener(object :
            RequestListener<Drawable> {
            override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>, isFirstResource: Boolean): Boolean {
                return false
            }
            override fun onResourceReady(resource: Drawable, model: Any, target: Target<Drawable>?, dataSource: DataSource, isFirstResource: Boolean): Boolean {
                // Image loaded successfully
                holder.loadingAnimation!!.visibility = View.GONE
                return false
            }
        }).into(holder.moviePicture!!)
    }


    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) , View.OnClickListener{
        var showButton: CardView? = null
        var moviePicture: ImageView? = null
        var loadingAnimation: LottieAnimationView? = null
        var name: TextView? = null
        var yearOfOscarWinnig: TextView? = null

        init {
            showButton = itemView.findViewById(R.id.homeFragment_fifthList_carditem_showButtonId)
            moviePicture = itemView.findViewById(R.id.homeFragment_fifthList_carditem_pictureId)
            loadingAnimation = itemView.findViewById(R.id.homeFragment_fifthList_carditem_loadingLottie)
            name = itemView.findViewById(R.id.homeFragment_fifthList_textView_nameId)
            yearOfOscarWinnig = itemView.findViewById(R.id.homeFragment_fifthList_yearOfWinnig)

            showButton!!.setOnClickListener(this)

            showButton!!.setOnLongClickListener(object : View.OnLongClickListener{
                override fun onLongClick(v: View?): Boolean {
                    showPic()
                    return true
                }
            })
        }

        override fun onClick(v: View?) {
            var intent = Intent(context , AboutMovieOrSerieActivity::class.java)

            //The position of each movie
            var position: Int = layoutPosition
            var modelOfFifthList = fifthListList.get(position)

            intent.putExtra("Picture" , modelOfFifthList.picture)
            intent.putExtra("Name", modelOfFifthList.name)
            intent.putExtra("Year" , modelOfFifthList.year)
            intent.putExtra("Duration" , modelOfFifthList.duration)
            intent.putExtra("Rating" , modelOfFifthList.rating)
            intent.putExtra("Description" , modelOfFifthList.description)
            intent.putExtra("Type" , modelOfFifthList.type)
            intent.putExtra("Trailer" , modelOfFifthList.trailerUrl)
            context.startActivity(intent)
        }

        private fun showPic() {
            var position: Int = adapterPosition
            var modelOfFifthList = fifthListList.get(position)

            val bottomSheetViewOfShowProfilePic: View = LayoutInflater.from(context).inflate( R.layout.bottom_sheet_of_show_picture,
                itemView.findViewById(R.id.bottomSheetLayout_showPicture_containerId) )

            val bottomSheetDialog: BottomSheetDialog = BottomSheetDialog(context, R.style.BottomSheetDialogTheme)
            bottomSheetDialog.setContentView(bottomSheetViewOfShowProfilePic)

            val bottomSheetBehavior: BottomSheetBehavior<FrameLayout> = bottomSheetDialog.behavior
            bottomSheetBehavior.peekHeight = context.resources.displayMetrics.heightPixels // hadi bach tchad the whole screen

            bottomSheetDialog.show()

            // Initialize the variables of Bottom Sheet and set data to them
            val picture: ImageView = bottomSheetViewOfShowProfilePic.findViewById(R.id.bottomSheetLayout_showPicture_pictureId)

            Glide.with(context).load(modelOfFifthList.picture).apply(RequestOptions.fitCenterTransform()).into(picture)
        }
    }
}