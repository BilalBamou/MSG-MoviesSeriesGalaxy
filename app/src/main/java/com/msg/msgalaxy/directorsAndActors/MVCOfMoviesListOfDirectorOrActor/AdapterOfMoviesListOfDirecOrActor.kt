package com.msg.msgalaxy.directorsAndActors.MVCOfMoviesListOfDirectorOrActor

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
import com.msg.msgalaxy.R
import de.hdodenhof.circleimageview.CircleImageView

class AdapterOfMoviesListOfDirecOrActor(var context: Context , var moviesOrSeriesList: ArrayList<ModelOfMoviesListOfDirecOrActor> ) : RecyclerView.Adapter<AdapterOfMoviesListOfDirecOrActor.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        var view = LayoutInflater.from(context).inflate(R.layout.movies_list_of_directors_or_actors_card_item , parent , false)
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int = moviesOrSeriesList.size

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        var modelOfList: ModelOfMoviesListOfDirecOrActor = moviesOrSeriesList.get(position)

        holder.name!!.text = modelOfList.name

        Glide.with(context).load(modelOfList.picture).apply(RequestOptions.fitCenterTransform())
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>, isFirstResource: Boolean): Boolean {
                    return false
                }

                override fun onResourceReady(resource: Drawable, model: Any, target: Target<Drawable>, dataSource: DataSource, isFirstResource: Boolean): Boolean {
                    // Image loaded successfully
                    holder.loadingAnimation!!.setVisibility(View.GONE)
                    return false
                }
            }).into(holder.picture!!)
    }

    inner class MyViewHolder(iv: View) : RecyclerView.ViewHolder(iv) , View.OnClickListener {

        var showBtn: CardView? = null
        var picture: ImageView? = null
        var loadingAnimation: LottieAnimationView? = null
        var name: TextView? = null

        init {
            showBtn = iv.findViewById(R.id.moviesListOfDirecOrActorsCardItem_showButtonId)
            picture = iv.findViewById(R.id.moviesListOfDirecOrActorsCardItem_pictureId)
            loadingAnimation = iv.findViewById(R.id.moviesListOfDirecOrActorsCardItem_loadingLottie)
            name = iv.findViewById(R.id.moviesListOfDirecOrActorsCardItem_nameId)

            showBtn!!.setOnClickListener(this)

        }

        override fun onClick(v: View?) {
            var position = getAdapterPosition()
            var modelOfMoviesListOfDirecOrActor = moviesOrSeriesList.get(position)

            var intent = Intent(context, AboutMovieOrSerieActivity::class.java)
            intent.putExtra("Picture", modelOfMoviesListOfDirecOrActor.picture)
            intent.putExtra("Name", modelOfMoviesListOfDirecOrActor.name)
            intent.putExtra("Year", modelOfMoviesListOfDirecOrActor.year)
            intent.putExtra("Duration", modelOfMoviesListOfDirecOrActor.duration)
            intent.putExtra("Rating", modelOfMoviesListOfDirecOrActor.rating)
            intent.putExtra("Description", modelOfMoviesListOfDirecOrActor.description)
            intent.putExtra("Type", modelOfMoviesListOfDirecOrActor.type)
            intent.putExtra("Trailer", modelOfMoviesListOfDirecOrActor.trailerUrl)
            context.startActivity(intent)
        }
    }

}