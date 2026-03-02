package com.msg.msgalaxy.MVCOfTopRated.forSeries

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import com.facebook.shimmer.Shimmer
import com.facebook.shimmer.ShimmerDrawable
import com.msg.msgalaxy.AboutMovieOrSerieActivity
import com.msg.msgalaxy.MVCOfTopRated.forMovies.ModelOfTopRatedMovies
import com.msg.msgalaxy.R

class AdapterOfTopRatedSeries(var context: Context, var topRatedSeriesList: ArrayList<ModelOfTopRatedSeries>) : RecyclerView.Adapter<AdapterOfTopRatedSeries.MyViewHolder>()  {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        var view = LayoutInflater.from(context).inflate(R.layout.toprated_fragment_toprated_movies_card_item, parent , false)
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int = topRatedSeriesList.size

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        var topRatedSeries: ModelOfTopRatedSeries = topRatedSeriesList.get(position)

        var rank: String = topRatedSeries.rank
        holder.serieName!!.text = "$rank. ${topRatedSeries.name}"
        holder.serieRating!!.text = topRatedSeries.rating
        holder.serieYear!!.text = topRatedSeries.year
        holder.serieDuration!!.text = topRatedSeries.duration

        //Initialize Shimmer Effects
        var shimmer: Shimmer = Shimmer.ColorHighlightBuilder()
            .setBaseColor(Color.parseColor("#F3F3F3"))
            .setBaseAlpha(1f)
            .setHighlightColor(Color.parseColor("#E7E7E7"))
            .setHighlightAlpha(1f)
            .setDropoff(50f)
            .build()

        //Initialize Shimmer Drawable
        var shimmerDrawable: ShimmerDrawable = ShimmerDrawable()
        shimmerDrawable.setShimmer(shimmer)

        Glide.with(context).load(topRatedSeries.picture).apply(RequestOptions.fitCenterTransform())
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>,
                    isFirstResource: Boolean
                ): Boolean {
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable,
                    model: Any,
                    target: Target<Drawable>?,
                    dataSource: DataSource,
                    isFirstResource: Boolean
                ): Boolean {
                    holder.loadingAnimation!!.visibility = View.GONE
                    return false
                }
            }).into(holder.seriePicture!!)
    }

    inner class MyViewHolder(iv: View) : RecyclerView.ViewHolder(iv) , View.OnClickListener {
        var showButton: CardView? = null
        var seriePicture: ImageView? = null
        var serieName: TextView? = null
        var serieRating: TextView? = null
        var serieYear: TextView? = null
        var serieDuration: TextView? = null
        var loadingAnimation: LottieAnimationView? = null

        init {
            showButton = itemView.findViewById(R.id.topRatedFragment_topRatedMovies_showButtonId)
            seriePicture = itemView.findViewById(R.id.topRatedFragment_topRatedMovies_pictureId)
            serieName = itemView.findViewById(R.id.topRatedFragment_topRatedMovies_nameId)
            serieRating = itemView.findViewById(R.id.topRatedFragment_topRatedMovies_ratingId)
            serieYear = itemView.findViewById(R.id.topRatedFragment_topRatedMovies_yearId)
            serieDuration = itemView.findViewById(R.id.topRatedFragment_topRatedMovies_durationId)

            loadingAnimation = itemView.findViewById(R.id.topRatedFragment_topRatedMovies_loadingLottie)

            showButton!!.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            var intent = Intent(context , AboutMovieOrSerieActivity::class.java)
            //The position of each movie
            var position: Int = getLayoutPosition()
            var modelOfTopRatedSeries = topRatedSeriesList.get(position)

            intent.putExtra("Picture" , modelOfTopRatedSeries.picture)
            intent.putExtra("Name", modelOfTopRatedSeries.name)
            intent.putExtra("Year" , modelOfTopRatedSeries.year)
            intent.putExtra("Duration" , modelOfTopRatedSeries.duration)
            intent.putExtra("Rating" , modelOfTopRatedSeries.rating)
            intent.putExtra("Description" , modelOfTopRatedSeries.description)
            intent.putExtra("Type" , modelOfTopRatedSeries.type)
            intent.putExtra("Trailer" , modelOfTopRatedSeries.trailerUrl)

            context.startActivity(intent)
        }
    }
}