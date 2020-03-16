package com.grupo5.mymovies

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.grupo5.mymovies.model.MoviesService
import kotlinx.coroutines.*
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.coroutines.CoroutineContext

class MainActivity : AppCompatActivity(), CoroutineScope {

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    private lateinit var job: Job

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        job = Job()

        launch(Dispatchers.IO) {
            val moviesResponse = createMoviesService().getMoviesAsync(getString(R.string.api_key))
            Log.v("API Response size: ", moviesResponse.results.size.toString())
        }
    }


    private fun createMoviesService(): MoviesService {
        return Retrofit.Builder()
            .baseUrl("https://api.themoviedb.org/3/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .run { create<MoviesService>(MoviesService::class.java) }
    }

    override fun onDestroy() {
        job.cancel()
        super.onDestroy()
    }
}
