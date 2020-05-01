package com.grupo5.mymovies

import android.Manifest
import android.annotation.SuppressLint
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.grupo5.mymovies.adapters.MovieAdapter
import com.grupo5.mymovies.model.Movie
import com.grupo5.mymovies.model.MoviesService
import com.karumi.dexter.Dexter
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.single.BasePermissionListener
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.resume


class MainActivity : AppCompatActivity(), CoroutineScope {

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    private lateinit var job: Job
    private lateinit var mAdapter: MovieAdapter
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        moviesRecycleView.setHasFixedSize(true)
        moviesRecycleView.layoutManager = LinearLayoutManager(this)
        moviesRecycleView.itemAnimator = DefaultItemAnimator()
        mAdapter = MovieAdapter(ArrayList<Movie>())
        moviesRecycleView.adapter = mAdapter

        job = Job()
        progress.visibility = View.VISIBLE
        moviesRecycleView.visibility = View.GONE

        launch(Dispatchers.IO) {
            val location = getLocation()
            val moviesResponse = createMoviesService().getMoviesAsync(getString(R.string.api_key), getRegionFromLocation(location))
            Log.v("API Response size: ", moviesResponse.results.size.toString())
            withContext(Dispatchers.Main) {
                mAdapter.movies = moviesResponse.results
                mAdapter.notifyDataSetChanged()
                progress.visibility = View.GONE
                moviesRecycleView.visibility = View.VISIBLE
            }
        }
    }

    private suspend fun getLocation(): Location? {
        val success = requestCoarseLocationPermission()
        return if (success) findLastLocation() else null
    }

    @SuppressLint("MissingPermission")
    private suspend fun findLastLocation(): Location? =
        suspendCancellableCoroutine { continuation ->
            fusedLocationClient.lastLocation
                .addOnCompleteListener {
                    continuation.resume(it.result)
                }
        }

    private suspend fun requestCoarseLocationPermission(): Boolean =
        suspendCancellableCoroutine { continuation ->
            Dexter
                .withActivity(this@MainActivity)
                .withPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
                .withListener(object : BasePermissionListener() {
                    override fun onPermissionGranted(response: PermissionGrantedResponse?) {
                        continuation.resume(true)
                    }

                    override fun onPermissionDenied(response: PermissionDeniedResponse?) {
                        continuation.resume(false)
                    }
                }
                ).check()
        }

    private fun getRegionFromLocation(location: Location?): String {
        val geocoder = Geocoder(this@MainActivity)
        val fromLocation = location?.let {
            geocoder.getFromLocation(
                location.latitude,
                location.longitude,
                1
            )
        }
        return fromLocation?.firstOrNull()?.countryCode ?: "US"
    }

    private fun createMoviesService(): MoviesService {
        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY

        val httpClient = OkHttpClient.Builder()
        httpClient.addInterceptor(logging)

        return Retrofit.Builder()
            .baseUrl("https://api.themoviedb.org/3/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(httpClient.build())
            .build()
            .run { create<MoviesService>(MoviesService::class.java) }

    }

    override fun onDestroy() {
        job.cancel()
        super.onDestroy()
    }
}
