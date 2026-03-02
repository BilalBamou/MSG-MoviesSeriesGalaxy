package com.msg.msgalaxy.directorsAndActors

import android.content.Context
import android.graphics.drawable.Drawable
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
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
import com.msg.msgalaxy.R
import com.msg.msgalaxy.SingletonOfFirebase
import com.msg.msgalaxy.directorsAndActors.MVCOfMoviesListOfDirectorOrActor.AdapterOfMoviesListOfDirecOrActor
import com.msg.msgalaxy.directorsAndActors.MVCOfMoviesListOfDirectorOrActor.ModelOfMoviesListOfDirecOrActor

class AboutDirectorOrActorActivity : AppCompatActivity() {

    private lateinit var pictureImgV: ImageView
    private lateinit var loadingAnimation: LottieAnimationView
    private lateinit var loadingData: RelativeLayout
    private lateinit var noInternet: RelativeLayout
    private lateinit var retryButton: CardView

    private lateinit var nameTxtV: TextView
    private lateinit var whoIsHeTxtV: TextView
    private lateinit var birthDateTxtV: TextView
    private lateinit var nationalityTxtV: TextView
    private lateinit var awardsTxtV: TextView

    private lateinit var directorId: String
    private lateinit var picture: String
    private lateinit var name: String
    private lateinit var whoIsHe: String
    private lateinit var birthDate: String
    private lateinit var nationality: String
    private lateinit var awards: String

    private lateinit var recyclerViewOfMoviesOrSeries: RecyclerView
    private lateinit var moviesOrSeriesList: ArrayList<ModelOfMoviesListOfDirecOrActor>
    private lateinit var adapter: AdapterOfMoviesListOfDirecOrActor

    //Singleton Design Pattern ( SingletonOfFirebase Class )
    private var singletonOfFirebase: SingletonOfFirebase = SingletonOfFirebase.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about_director_or_actor)

        // Initialize the variables
        loadingData = findViewById(R.id.aboutDirectorOrActorActivity_lottieLoadingAnimation_relativeLayoutId)
        noInternet = findViewById(R.id.aboutDirectorOrActorActivity_noInternet_relativeLayoutId)
        retryButton = findViewById(R.id.aboutDirectorOrActorActivity_retryButtonId)

        recyclerViewOfMoviesOrSeries = findViewById(R.id.aboutDirectorOrActorActivity_recyclerViewId)

        internetTestProcess()

        retryButton.setOnClickListener {
            onRetryButtonClick()
        }

        arrowBack()
    }

    fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = connectivityManager.activeNetwork
        val networkCapabilities = connectivityManager.getNetworkCapabilities(activeNetwork)
        return networkCapabilities != null && (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) )
    }

    private fun internetTestProcess() {
        if (!isNetworkAvailable(this)) {
            loadingData.visibility = View.GONE
            noInternet.visibility = View.VISIBLE
            return
        }
        loadingData.visibility = View.VISIBLE
        noInternet.visibility = View.GONE

        getDataFromTheActivity()

        recyclerViewWithAdapterProcess()
    }

    private fun onRetryButtonClick() {
        if (!isNetworkAvailable(this)) {
            // If still no internet
            loadingData.visibility = View.GONE
            noInternet.visibility = View.VISIBLE
            return
        }
        loadingData.visibility = View.VISIBLE
        noInternet.visibility = View.GONE

        getDataFromTheActivity()

        recyclerViewWithAdapterProcess()
    }

    private fun getDataFromTheActivity() {
        //Initiliaze variables
        pictureImgV = findViewById(R.id.aboutDirectorOrActorActivity_pictureId)
        nameTxtV = findViewById(R.id.aboutDirectorOrActorActivity_nameId)
        whoIsHeTxtV = findViewById(R.id.aboutDirectorOrActorActivity_whoIsHeId)
        birthDateTxtV = findViewById(R.id.aboutDirectorOrActorActivity_birthDateId)
        nationalityTxtV = findViewById(R.id.aboutDirectorOrActorActivity_nationalityId)
        awardsTxtV = findViewById(R.id.aboutDirectorOrActorActivity_awardsId)

        loadingAnimation = findViewById(R.id.aboutDirectorOrActorActivity_loadingLottieId)

        var bundle: Bundle = intent.extras!!

        directorId = bundle.getString("DirectorId")!!
        picture = bundle.getString("DirectorPicture")!!
        name = bundle.getString("DirectorName")!!
        whoIsHe = bundle.getString("WhoIsHe")!!
        birthDate = bundle.getString("BirthDate")!!
        nationality = bundle.getString("Nationality")!!
        awards = bundle.getString("DirectorAwards")!!

        //Setting the data into the activity
        Glide.with(this).load(picture).apply(RequestOptions.fitCenterTransform())
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>, isFirstResource: Boolean): Boolean {
                    loadingAnimation.visibility = View.GONE
                    return false
                }
                override fun onResourceReady(resource: Drawable, model: Any, target: Target<Drawable>?, dataSource: DataSource, isFirstResource: Boolean): Boolean {
                    // Image loaded successfully
                    loadingAnimation.visibility = View.GONE
                    return false
                }
            }).into(pictureImgV)

        nameTxtV.text = name
        whoIsHeTxtV.text = whoIsHe
        birthDateTxtV.text = "Born : $birthDate"
        nationalityTxtV.text = nationality
        awardsTxtV.text = awards
    }

    private fun recyclerViewWithAdapterProcess() {
        //Initialize the variables
        var gridLayoutManager: GridLayoutManager = GridLayoutManager(this , 2)
        recyclerViewOfMoviesOrSeries.layoutManager = gridLayoutManager
        recyclerViewOfMoviesOrSeries.setHasFixedSize(true)
        recyclerViewOfMoviesOrSeries.itemAnimator = DefaultItemAnimator()

        moviesOrSeriesList = ArrayList()

        //Get the data of Movie Or Serie From FB according to the director
        getMoviesOrSeriesFromFB()
    }

    private fun getMoviesOrSeriesFromFB() {
        var reference: DatabaseReference = FirebaseDatabase.getInstance().reference.child("Best Masterpiece Makers")
        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (dataSnapshot: DataSnapshot in snapshot.children) {
                    var id: String = dataSnapshot.key!!  // this is the Id ( 0 , 1 , 2 , 3 ...)

                    if (id.equals(directorId)) {
                        if (dataSnapshot.hasChild("Movies")) {
                            var moviesSnapshot: DataSnapshot = dataSnapshot.child("Movies")

                            for (movieSnapshot : DataSnapshot in moviesSnapshot.children) {
                                var movieName: String = movieSnapshot.child("Name").value.toString()
                                getRightMoviesFromAllMSGData_fromFB(movieName)
                            }
                        }
                    }
                    // Update the RecyclerView here, after fetching all movies
                    updateRecyclerView()
                }
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun getRightMoviesFromAllMSGData_fromFB(movieName: String) {
        var reference: DatabaseReference = FirebaseDatabase.getInstance().reference.child("allMsgData")
        moviesOrSeriesList.clear()
        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for ( dataSnapshot: DataSnapshot in snapshot.children ) {
                    var id: String = dataSnapshot.key!!
                    var Name: String = dataSnapshot.child("Name").value.toString()
                    if(movieName.equals(Name) ) {
                        var modelOfMoviesListOfDirecOrActor: ModelOfMoviesListOfDirecOrActor = ModelOfMoviesListOfDirecOrActor()

                        modelOfMoviesListOfDirecOrActor.picture = dataSnapshot.child("Picture").value.toString()
                        modelOfMoviesListOfDirecOrActor.name = Name
                        modelOfMoviesListOfDirecOrActor.year = dataSnapshot.child("Year").value.toString()
                        modelOfMoviesListOfDirecOrActor.duration = dataSnapshot.child("Duration").value.toString()
                        modelOfMoviesListOfDirecOrActor.rating = dataSnapshot.child("Rating").value.toString()
                        modelOfMoviesListOfDirecOrActor.description = dataSnapshot.child("Description").value.toString()
                        modelOfMoviesListOfDirecOrActor.type = dataSnapshot.child("Type").value.toString()
                        modelOfMoviesListOfDirecOrActor.trailerUrl = dataSnapshot.child("Trailer").value.toString()

                        moviesOrSeriesList.add(modelOfMoviesListOfDirecOrActor)
                    }
                    // Update the RecyclerView here, after fetching all movies
                    updateRecyclerView()
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    // I Moved this method outside the loop to update the RecyclerView only once
    private fun updateRecyclerView() {
        adapter = AdapterOfMoviesListOfDirecOrActor(this, moviesOrSeriesList)
        recyclerViewOfMoviesOrSeries.adapter = adapter
        adapter.notifyDataSetChanged()

        // Hide the LoadingAnimation when data is loaded
        loadingData.visibility = View.GONE
    }


    private fun arrowBack() {
        var arrowBack = findViewById<CardView>(R.id.aboutDirectorOrActorActivity_arrowBackId)
        // Or findViewById(R.id.aboutDirectorOrActorActivity_arrowBackId) as CardView

        arrowBack.setOnClickListener {
            onBackPressed()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }
}