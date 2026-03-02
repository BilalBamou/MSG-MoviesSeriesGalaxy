package com.msg.msgalaxy.MVCOfTopRated.forMovies

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
import com.msg.msgalaxy.R

class AdapterOfTopRatedMovies(var context: Context, var topRatedMoviesList: ArrayList<ModelOfTopRatedMovies>) : RecyclerView.Adapter<AdapterOfTopRatedMovies.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        var view = LayoutInflater.from(context).inflate(R.layout.toprated_fragment_toprated_movies_card_item, parent , false)
        return MyViewHolder(view)
    }
    
    override fun getItemCount(): Int = topRatedMoviesList.size

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        var topRatedMovies: ModelOfTopRatedMovies = topRatedMoviesList.get(position)

        var rank: String = topRatedMovies.rank
        holder.movieName!!.text = "$rank. ${topRatedMovies.name}"
        holder.movieRating!!.text = topRatedMovies.rating
        holder.movieYear!!.text = topRatedMovies.year
        holder.movieDuration!!.text = topRatedMovies.duration

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

        Glide.with(context).load(topRatedMovies.picture).apply(RequestOptions.fitCenterTransform())
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
            }).into(holder.moviePicture!!)

    }

    inner class MyViewHolder(iv: View) : RecyclerView.ViewHolder(iv) , View.OnClickListener {
        var showButton: CardView? = null
        var moviePicture: ImageView? = null
        var movieName: TextView? = null
        var movieRating: TextView? = null
        var movieYear: TextView? = null
        var movieDuration: TextView? = null
        var loadingAnimation: LottieAnimationView? = null

        init {
            showButton = itemView.findViewById(R.id.topRatedFragment_topRatedMovies_showButtonId)
            moviePicture = itemView.findViewById(R.id.topRatedFragment_topRatedMovies_pictureId)
            movieName = itemView.findViewById(R.id.topRatedFragment_topRatedMovies_nameId)
            movieRating = itemView.findViewById(R.id.topRatedFragment_topRatedMovies_ratingId)
            movieYear = itemView.findViewById(R.id.topRatedFragment_topRatedMovies_yearId)
            movieDuration = itemView.findViewById(R.id.topRatedFragment_topRatedMovies_durationId)

            loadingAnimation = itemView.findViewById(R.id.topRatedFragment_topRatedMovies_loadingLottie)

            showButton!!.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            var intent = Intent(context , AboutMovieOrSerieActivity::class.java)
            //The position of each movie
            var position: Int = getLayoutPosition()
            var modelOfTopRatedMovies = topRatedMoviesList.get(position)

            intent.putExtra("Picture" , modelOfTopRatedMovies.picture)
            intent.putExtra("Name", modelOfTopRatedMovies.name)
            intent.putExtra("Year" , modelOfTopRatedMovies.year)
            intent.putExtra("Duration" , modelOfTopRatedMovies.duration)
            intent.putExtra("Rating" , modelOfTopRatedMovies.rating)
            intent.putExtra("Description" , modelOfTopRatedMovies.description)
            intent.putExtra("Type" , modelOfTopRatedMovies.type)
            intent.putExtra("Trailer" , modelOfTopRatedMovies.trailerUrl)

            context.startActivity(intent)
        }
    }
}