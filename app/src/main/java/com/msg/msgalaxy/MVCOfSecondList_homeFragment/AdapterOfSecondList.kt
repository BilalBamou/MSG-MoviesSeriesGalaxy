package com.msg.msgalaxy.MVCOfSecondList_homeFragment

import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
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
import com.msg.msgalaxy.R

class AdapterOfSecondList (var context: Context, var secondListList: ArrayList<ModelOfSecondList>) : RecyclerView.Adapter<AdapterOfSecondList.MyViewHolder>()  {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        var view: View = LayoutInflater.from(context).inflate(R.layout.home_fragment_second_list_card_item , parent , false)
        var myViewHolder = MyViewHolder(view)
        return myViewHolder
    }

    override fun getItemCount(): Int = secondListList.size

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        var modelOfSecondList: ModelOfSecondList = secondListList.get(position)
        Glide.with(context).load(modelOfSecondList.picture).apply(RequestOptions.fitCenterTransform())
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>, isFirstResource: Boolean): Boolean {
                    return false
                }

                override fun onResourceReady(resource: Drawable, model: Any, target: Target<Drawable>?, dataSource: DataSource, isFirstResource: Boolean): Boolean {
                    // Image loaded successfully
                    holder.loadingAnimation!!.visibility = View.GONE
                    return false
                }
            }).into(holder.moviePicture!!)
        Glide.with(context).load(modelOfSecondList.rank).into(holder.rankTop9!!)
    }

    inner class MyViewHolder : RecyclerView.ViewHolder , View.OnClickListener {

        var showButton: CardView? = null
        var moviePicture: ImageView? = null
        var loadingAnimation: LottieAnimationView? = null
        var rankTop9: ImageView? = null

        constructor(iv: View) : super(iv) {
            showButton = iv.findViewById(R.id.homeFragment_secondList_carditem_showButtonId)
            moviePicture = iv.findViewById(R.id.homeFragment_secondList_carditem_pictureId)
            loadingAnimation = iv.findViewById(R.id.homeFragment_secondList_carditem_loadingLottie)
            rankTop9 = iv.findViewById(R.id.homeFragment_secondList_carditem_rankId)

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
            var modelOfSecondList = secondListList.get(position)

            intent.putExtra("Picture" , modelOfSecondList.picture)
            intent.putExtra("Name", modelOfSecondList.name)
            intent.putExtra("Year" , modelOfSecondList.year)
            intent.putExtra("Duration" , modelOfSecondList.duration)
            intent.putExtra("Rating" , modelOfSecondList.rating)
            intent.putExtra("Description" , modelOfSecondList.description)
            intent.putExtra("Type" , modelOfSecondList.type)
            intent.putExtra("Trailer" , modelOfSecondList.trailerUrl)
            context.startActivity(intent)
        }

        private fun showPic() {
            var position: Int = adapterPosition
            var modelOfSecondList = secondListList.get(position)

            val bottomSheetViewOfShowProfilePic: View = LayoutInflater.from(context).inflate( R.layout.bottom_sheet_of_show_picture,
                itemView.findViewById(R.id.bottomSheetLayout_showPicture_containerId) )

            val bottomSheetDialog: BottomSheetDialog = BottomSheetDialog(context, R.style.BottomSheetDialogTheme)
            bottomSheetDialog.setContentView(bottomSheetViewOfShowProfilePic)

            val bottomSheetBehavior: BottomSheetBehavior<FrameLayout> = bottomSheetDialog.behavior
            bottomSheetBehavior.peekHeight = context.resources.displayMetrics.heightPixels // hadi bach tchad the whole screen

            bottomSheetDialog.show()

            // Initialize the variables of Bottom Sheet and set data to them
            val picture: ImageView = bottomSheetViewOfShowProfilePic.findViewById(R.id.bottomSheetLayout_showPicture_pictureId)

            Glide.with(context).load(modelOfSecondList.picture).apply(RequestOptions.fitCenterTransform()).into(picture)
        }
    }
}