package com.msg.msgalaxy

import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.github.chrisbanes.photoview.PhotoView
import com.google.android.material.bottomsheet.BottomSheetDialog
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class ShowPictureOfMSActivity : AppCompatActivity() {

    private var pictureImageView: PhotoView? = null
    private var nameTxtView: TextView? = null
    private var picture: String? = null
    private var name: String? = null

    private var moreBtn: CardView? = null
    private var moreIcon: ImageView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_picture_of_msactivity)

        //Initialize the variables
        pictureImageView = findViewById(R.id.showPictureOfMSActivity_pictureId)
        nameTxtView = findViewById(R.id.showPictureOfMSActivity_nameId)

        // Initialize the variables
        moreBtn = findViewById(R.id.showPictureOfMSActivity_moreBtnId)
        moreIcon = findViewById(R.id.showPictureOfMSActivity_moreIconId)

        getPictureAndNameData()

        moreBtn!!.setOnClickListener {
            moreBtnProcess()
        }

        //Arrow Btn From MyList to Profile Fragment
        arrowBack()
    }

    private fun getPictureAndNameData() {
        var bundle: Bundle? = intent.extras
        if (bundle != null) {
            picture = bundle.getString("Picture")
            name = bundle.getString("Name")
        }

        //Set the name Of movie Or serie
        nameTxtView?.setText(name)

        //Set the picture
        Glide.with(this).load(picture).listener(object : RequestListener<Drawable> {
            override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>, isFirstResource: Boolean): Boolean {
               return false
            }
            override fun onResourceReady(resource: Drawable, model: Any, target: Target<Drawable>?, dataSource: DataSource, isFirstResource: Boolean): Boolean {
                // Image loaded successfully
                //  loadingData.setVisibility(View.GONE)
                //  loadingAnimation.setVisibility(View.GONE)
                return false;
            }
        }).into(pictureImageView!!)

    }

    private fun moreBtnProcess() {
        var bottomSheetView: View = LayoutInflater.from(this).inflate(R.layout.bottom_sheet_of_delete_and_edit_comment,
            findViewById(R.id.bottomSheetLayout_deleteComment_containerId))

        var bottomSheetDialog: BottomSheetDialog = BottomSheetDialog(this, R.style.BottomSheetDialogTheme)

        bottomSheetDialog.setContentView(bottomSheetView)
        bottomSheetDialog.show()

        // Initialize the variables
        var save_icon: ImageView? = bottomSheetDialog.findViewById(R.id.bottomSheetLayout_delete_copy_iconOneId)
        var save_text: TextView? = bottomSheetDialog.findViewById(R.id.bottomSheetLayout_delete_copy_textOneId)
        var share_icon: ImageView? = bottomSheetDialog.findViewById(R.id.bottomSheetLayout_edit_report_iconTwoId)
        var share_text: TextView? = bottomSheetDialog.findViewById(R.id.bottomSheetLayout_edit_report_textTwoId)

        // set icons and titles
        save_icon!!.setImageResource(R.drawable.save_to_phone_icon)
        save_text!!.text = "Save to phone"

        share_icon!!.setImageResource(R.drawable.share_icon)
        share_text!!.text = "Share external"

        // for save to phone the MS image
        var saveBtn: LinearLayout? = bottomSheetDialog.findViewById(R.id.bottomSheetLayout_deleteComment_deleteBtnId)
        saveBtn!!.setOnClickListener {
            saveToPhoneProcess(bottomSheetDialog)
        }

        // for share the MS image
        var shareBtn: LinearLayout? = bottomSheetDialog.findViewById(R.id.bottomSheetLayout_editComment_editBtnId)
        shareBtn!!.setOnClickListener {
            shareExternalProcess(bottomSheetDialog)
        }
    }

    private fun saveToPhoneProcess(bottomSheetDialog: BottomSheetDialog) {
        if (ContextCompat.checkSelfPermission(this@ShowPictureOfMSActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            // Permission already granted, you can now download the image
            saveImage()
        } else {
            // Permission not granted, request it
            ActivityCompat.requestPermissions((this@ShowPictureOfMSActivity as Activity?)!!, arrayOf<String>(Manifest.permission.WRITE_EXTERNAL_STORAGE), 1)
        }
        bottomSheetDialog.dismiss()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1 && grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // Permission granted, you can now download the image
            saveImage()
        } else {
            // Permission denied, handle it as needed (e.g., show a message to the user)
            Toast.makeText(applicationContext, "Please provide the required permissions!" , Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveImage() {
        //We should add this android:requestLegacyExternalStorage="true" to the manifests
        if (ContextCompat.checkSelfPermission(this@ShowPictureOfMSActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((this@ShowPictureOfMSActivity as Activity?)!!, arrayOf<String>(Manifest.permission.WRITE_EXTERNAL_STORAGE), 1)
            return  // Return to avoid attempting to save the image without the required permission.
        }

        var outputStream: FileOutputStream? = null

        val drawable = pictureImageView!!.drawable as BitmapDrawable
        val bitmap = drawable.bitmap

        // Use MediaStore to save the image to the gallery
        val values = ContentValues()
        values.put(MediaStore.Images.Media.DISPLAY_NAME, "MSG_" + System.currentTimeMillis())
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")

        // Specify the relative path where the image will be saved (e.g., Pictures/my app name)
        values.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + File.separator + resources.getString(R.string.app_name))

        val uri = applicationContext.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)

        try {
            outputStream = applicationContext.contentResolver.openOutputStream(uri!!) as FileOutputStream?
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream!!)
            Toast.makeText(applicationContext,"Saved successfully", Toast.LENGTH_SHORT).show()
        }
        catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(applicationContext, "Error: " + e.message, Toast.LENGTH_SHORT).show()
        }
        finally {
            try {
                outputStream?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun shareExternalProcess(bottomSheetDialog: BottomSheetDialog) {
        val drawable = pictureImageView!!.getDrawable() as BitmapDrawable
        val bitmap = drawable.bitmap

        try {
            val cachePath = File(applicationContext.externalCacheDir, "images")
            cachePath.mkdirs()
            val imageFile = File(cachePath, "msg.png")
            val outputStream = FileOutputStream(imageFile)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            outputStream.close()
            val imageUri = FileProvider.getUriForFile(applicationContext, "com.msg.msgalaxy.fileprovider", imageFile)

            // Use MediaStore to insert the image and get the image path
//            val path = MediaStore.Images.Media.insertImage(contentResolver, imageFile.absolutePath, "Title", null)

            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.type = "image/*"

            // Add a message or title to the intent
            val message = name
            shareIntent.putExtra(Intent.EXTRA_TEXT, message)

            shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri)
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            startActivity(Intent.createChooser(shareIntent, "Share Poster of $name via"))

            bottomSheetDialog.dismiss()
        } catch (e: Exception) {
            Toast.makeText(applicationContext, "Error: " + e.message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun arrowBack() {
        var arrowBack: CardView = findViewById(R.id.showPictureOfMSActivity_arrowBackId)
        arrowBack.setOnClickListener({
            //For Back from Activity to Fragment !!!!!
            onBackPressed()
        })
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }
}