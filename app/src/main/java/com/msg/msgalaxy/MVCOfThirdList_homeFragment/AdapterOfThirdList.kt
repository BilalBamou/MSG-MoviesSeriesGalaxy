package com.msg.msgalaxy.MVCOfThirdList_homeFragment

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
import com.msg.msgalaxy.R
import com.msg.msgalaxy.directorsAndActors.AboutDirectorOrActorActivity
import de.hdodenhof.circleimageview.CircleImageView

class AdapterOfThirdList(var context: Context, var thirdListList: ArrayList<ModelOfThirdList>) : RecyclerView.Adapter<AdapterOfThirdList.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        var view = LayoutInflater.from(context).inflate(R.layout.home_fragment_third_list_card_item , parent , false)
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int = thirdListList.size

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        var modelOfThirdList = thirdListList[position]

        holder.name!!.text = modelOfThirdList.directorName

        Glide.with(context).load(modelOfThirdList.directorPicture)
            .apply(RequestOptions.fitCenterTransform())
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>, isFirstResource: Boolean): Boolean {
                    return false
                }
                override fun onResourceReady(resource: Drawable, model: Any, target: Target<Drawable>, dataSource: DataSource, isFirstResource: Boolean): Boolean {
                    // Image loaded successfully
                    holder.loadingAnimation!!.visibility = View.GONE
                    return false
                }
            }).into(holder.directorPicture!!)
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var showButton: CardView? = null
        var directorPicture: ImageView? = null
        var loadingAnimation: LottieAnimationView? = null
        var name: TextView? = null

        init {
            showButton = itemView.findViewById(R.id.homeFragment_thirdList_carditem_showButtonId)
            directorPicture = itemView.findViewById(R.id.homeFragment_thirdList_carditem_directorPictureId)
            loadingAnimation = itemView.findViewById(R.id.homeFragment_thirdList_carditem_loadingLottie)
            name = itemView.findViewById(R.id.homeFragment_thirdList_textView_nameId)

            showButton!!.setOnClickListener {
                showBtnProcess()
            }

            showButton!!.setOnLongClickListener(object : View.OnLongClickListener{
                override fun onLongClick(v: View?): Boolean {
                    showPic()
                    return true
                }
            })

        }

        private fun showBtnProcess() {
            var intent: Intent = Intent(context , AboutDirectorOrActorActivity::class.java)
            //The position of each director

            var position = layoutPosition
            var modelOfThirdList = thirdListList.get(position)
            intent.putExtra("DirectorId" , modelOfThirdList.idOfDirector)
            intent.putExtra("DirectorPicture" , modelOfThirdList.directorPicture)
            intent.putExtra("DirectorName", modelOfThirdList.directorName)
            intent.putExtra("WhoIsHe" , modelOfThirdList.whoIsHe)
            intent.putExtra("BirthDate" , modelOfThirdList.birthDate)
            intent.putExtra("Nationality" , modelOfThirdList.nationality)
            intent.putExtra("DirectorAwards" , modelOfThirdList.directorAwards)
            context.startActivity(intent)
        }

        private fun showPic() {
            var position: Int = adapterPosition
            var modelOfThirdList = thirdListList.get(position)

            val bottomSheetViewOfShowProfilePic: View = LayoutInflater.from(context).inflate( R.layout.bottom_sheet_of_show_picture,
                itemView.findViewById(R.id.bottomSheetLayout_showPicture_containerId) )

            val bottomSheetDialog: BottomSheetDialog = BottomSheetDialog(context, R.style.BottomSheetDialogTheme)
            bottomSheetDialog.setContentView(bottomSheetViewOfShowProfilePic)

            val bottomSheetBehavior: BottomSheetBehavior<FrameLayout> = bottomSheetDialog.behavior
            bottomSheetBehavior.peekHeight = context.resources.displayMetrics.heightPixels // hadi bach tchad the whole screen

            bottomSheetDialog.show()

            // Initialize the variables of Bottom Sheet and set data to them
            val picture: ImageView = bottomSheetViewOfShowProfilePic.findViewById(R.id.bottomSheetLayout_showPicture_pictureId)

            Glide.with(context).load(modelOfThirdList.directorPicture).apply(RequestOptions.fitCenterTransform()).into(picture)
        }
    }
}