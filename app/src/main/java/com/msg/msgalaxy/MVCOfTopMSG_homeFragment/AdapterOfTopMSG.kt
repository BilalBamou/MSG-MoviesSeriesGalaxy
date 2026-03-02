package com.msg.msgalaxy.MVCOfTopMSG_homeFragment

import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.viewpager.widget.PagerAdapter
import com.airbnb.lottie.LottieAnimationView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.msg.msgalaxy.AboutMovieOrSerieActivity
import com.msg.msgalaxy.R

class AdapterOfTopMSG(var context: Context , var topMSGList: ArrayList<ModelOfTopMSG>) : PagerAdapter() {

    override fun getCount(): Int = this.topMSGList.size

    override fun isViewFromObject(view: View, `object`: Any): Boolean = view.equals(`object`)

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View?)
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        var view: View = LayoutInflater.from(context).inflate(R.layout.home_fragment_topmsg_card_item, container , false)
        var modelOfTopMSG: ModelOfTopMSG = topMSGList.get(position)

        var loadingAnimation: LottieAnimationView = view.findViewById(R.id.homeFragment_topMSG_carditem_loadingLottie)
        var name: TextView = view.findViewById(R.id.homeFragment_topMSG_carditem_nameId)
        var topMSGPicUrlImageView: ImageView = view.findViewById(R.id.homeFragment_topMSG_carditem_pictureId)
        var topMSGPic2: ImageView = view.findViewById(R.id.homeFragment_topMSG_carditem_posterPic_imageViewId2)
        var type: TextView = view.findViewById(R.id.homeFragment_topMSG_carditem_typeId)
        var year: TextView = view.findViewById(R.id.homeFragment_topMSG_carditem_yearId)
        var imdbRating: TextView = view.findViewById(R.id.homeFragment_topMSG_carditem_imdbRatingId)

        //The Url of Picture
        var topMSGPicUrl: String = modelOfTopMSG.picture
        Glide.with(context).load(topMSGPicUrl).apply(RequestOptions.fitCenterTransform())
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>, isFirstResource: Boolean): Boolean {
                    return false
                }
                override fun onResourceReady(resource: Drawable, model: Any, target: Target<Drawable>, dataSource: DataSource, isFirstResource: Boolean): Boolean {
                    // Image loaded successfully
                    loadingAnimation.visibility = View.GONE
                    return false
                }
            }).into(topMSGPicUrlImageView)

        Glide.with(context).load(topMSGPicUrl).apply(RequestOptions.fitCenterTransform())
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>, isFirstResource: Boolean): Boolean {
                    return false
                }
                override fun onResourceReady(resource: Drawable, model: Any, target: Target<Drawable>, dataSource: DataSource, isFirstResource: Boolean): Boolean {
                    // Image loaded successfully
                    loadingAnimation.visibility = View.GONE
                    return false
                }
            }).into(topMSGPic2)

        name.text = modelOfTopMSG.name
        type.text = modelOfTopMSG.type
        year.text = modelOfTopMSG.year
        imdbRating.text = modelOfTopMSG.rating

        //Button To See About Movie Or Serie Activity
        topMSGPicUrlImageView.setOnClickListener {
            var intent: Intent = Intent(context , AboutMovieOrSerieActivity::class.java)
            intent.putExtra("Picture" , modelOfTopMSG.picture)
            intent.putExtra("Name", modelOfTopMSG.name)
            intent.putExtra("Year" , modelOfTopMSG.year)
            intent.putExtra("Duration" , modelOfTopMSG.duration)
            intent.putExtra("Rating" , modelOfTopMSG.rating)
            intent.putExtra("Description" , modelOfTopMSG.description)
            intent.putExtra("Type" , modelOfTopMSG.type)
            intent.putExtra("Trailer" , modelOfTopMSG.trailerUrl)
            context.startActivity(intent)
        }

        container.addView(view)

        return view
    }
}