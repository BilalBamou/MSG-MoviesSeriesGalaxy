package com.msg.msgalaxy.profileActivities.MVCMyList

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
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.msg.msgalaxy.AboutMovieOrSerieActivity
import com.msg.msgalaxy.R

class AdapterOfMyList(var context: Context , var listOfMylist: ArrayList<ModelOfMyList>) : RecyclerView.Adapter<AdapterOfMyList.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        var view = LayoutInflater.from(context).inflate(R.layout.mylist_card_item , parent , false)
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int = listOfMylist.size

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        var modelOfMyList = listOfMylist.get(position)

        holder.name!!.text = modelOfMyList.name

        Glide.with(context).load(modelOfMyList.picture).apply(RequestOptions.fitCenterTransform())
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>, isFirstResource: Boolean): Boolean {
                    return false
                }

                override fun onResourceReady(resource: Drawable, model: Any, target: Target<Drawable>?, dataSource: DataSource, isFirstResource: Boolean): Boolean {
                    // Image loaded successfully
                    holder.loadingAnimation!!.visibility = View.GONE
                    return false
                }

            }).into(holder.picture!!)
    }
    inner class MyViewHolder(i: View) : RecyclerView.ViewHolder(i) , View.OnClickListener {
        var showBtn: CardView? = null
        var picture: ImageView? = null
        var loadingAnimation: LottieAnimationView? = null
        var name: TextView? = null

        init {
            showBtn = i.findViewById(R.id.myListCardItem_showButtonId)
            picture = i.findViewById(R.id.myListCardItem_pictureId)
            loadingAnimation = i.findViewById(R.id.myListCarditem_loadingLottie)
            name = i.findViewById(R.id.myListCardItem_nameId)

            showBtn!!.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            var position = getAdapterPosition()
            var modelOfMyList: ModelOfMyList = listOfMylist.get(position)
            getTheDataOfMovieOrSerieFromFB(modelOfMyList.name)
        }

    }

    private fun getTheDataOfMovieOrSerieFromFB(name: String) {
        var reference: DatabaseReference = FirebaseDatabase.getInstance().reference.child("allMsgData")
        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (childSnapshot: DataSnapshot in snapshot.children) {
                    var nameFB: String? = childSnapshot.child("Name").getValue(String::class.java)
                    if (name.equals(nameFB)) {
                        var picture = childSnapshot.child("Picture").getValue(String::class.java)
                        var year = childSnapshot.child("Year").getValue(String::class.java)
                        var duration = childSnapshot.child("Duration").getValue(String::class.java)
                        var rating = childSnapshot.child("Rating").getValue(String::class.java)
                        var description = childSnapshot.child("Description").getValue(String::class.java)
                        var type = childSnapshot.child("Type").getValue(String::class.java)
                        var trailer = childSnapshot.child("Trailer").getValue(String::class.java)

                        var intent = Intent(context, AboutMovieOrSerieActivity::class.java)
                        intent.putExtra("Picture", picture)
                        intent.putExtra("Name", nameFB)
                        intent.putExtra("Year", year)
                        intent.putExtra("Duration", duration)
                        intent.putExtra("Rating", rating)
                        intent.putExtra("Description", description)
                        intent.putExtra("Type", type)
                        intent.putExtra("Trailer", trailer)
                        context.startActivity(intent)

                        return; // Stop iterating once you've found the match

                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

}