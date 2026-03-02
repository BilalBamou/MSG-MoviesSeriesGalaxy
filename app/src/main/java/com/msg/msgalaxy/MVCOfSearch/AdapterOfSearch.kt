package com.msg.msgalaxy.MVCOfSearch

import android.content.Context
import android.content.Intent
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
import com.msg.msgalaxy.AboutMovieOrSerieActivity
import com.msg.msgalaxy.R

class AdapterOfSearch(var context: Context, var searchList: ArrayList<ModelOfSearch>) : RecyclerView.Adapter<AdapterOfSearch.MyViewHolder>(){

    //About the Filtered List
    fun setFilteredList(filteredList: ArrayList<ModelOfSearch>) {
        this.searchList = filteredList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        var view: View = LayoutInflater.from(context).inflate(R.layout.search_fragment_search_list_card_item , parent , false)
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int = searchList.size

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        var modelOfSearch: ModelOfSearch = searchList.get(position)

        holder.name.text = modelOfSearch.name
        holder.rating.text = modelOfSearch.rating
        holder.year.text = modelOfSearch.year
        holder.type.text = modelOfSearch.type
        holder.duration.text = modelOfSearch.duration

        Glide.with(context).load(modelOfSearch.picture).apply(RequestOptions.fitCenterTransform())
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>, isFirstResource: Boolean): Boolean {
                    return false
                }
                override fun onResourceReady(resource: Drawable, model: Any, target: Target<Drawable>?, dataSource: DataSource, isFirstResource: Boolean): Boolean {
                    // Image loaded successfully
                    holder.loadingAnimation.visibility = View.GONE
                    return false
                }
            }).into(holder.picture)
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) , View.OnClickListener {
        lateinit var showButton: CardView
        lateinit var picture: ImageView
        lateinit var name: TextView
        lateinit var rating: TextView
        lateinit var year: TextView
        lateinit var type: TextView
        lateinit var duration: TextView
        lateinit var loadingAnimation: LottieAnimationView

        init {
            showButton = itemView.findViewById(R.id.searchFragment_searchList_carditem_showButtonId)
            picture = itemView.findViewById(R.id.searchFragment_searchList_carditem_pictureId)
            name = itemView.findViewById(R.id.searchFragment_searchList_carditem_nameId)
            rating = itemView.findViewById(R.id.searchFragment_searchList_carditem_ratingId)
            year = itemView.findViewById(R.id.searchFragment_searchList_carditem_yearId)
            type = itemView.findViewById(R.id.searchFragment_searchList_carditem_typeId)
            duration = itemView.findViewById(R.id.searchFragment_searchList_carditem_durationId)

            loadingAnimation = itemView.findViewById(R.id.searchFragment_searchList_carditem_loadingLottie)

            showButton.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            var intent: Intent = Intent(context , AboutMovieOrSerieActivity::class.java)

            //The position of each movie
            var position: Int = layoutPosition
            var modelOfSearch: ModelOfSearch = searchList.get(position)

            intent.putExtra("Picture" , modelOfSearch.picture)
            intent.putExtra("Name", modelOfSearch.name)
            intent.putExtra("Year" , modelOfSearch.year)
            intent.putExtra("Duration" , modelOfSearch.duration)
            intent.putExtra("Rating" , modelOfSearch.rating)
            intent.putExtra("Description" , modelOfSearch.description)
            intent.putExtra("Type" , modelOfSearch.type)
            intent.putExtra("Trailer" , modelOfSearch.trailerUrl)

            context.startActivity(intent)
        }


    }
}