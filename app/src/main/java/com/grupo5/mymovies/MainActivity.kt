package com.grupo5.mymovies

import android.opengl.Visibility
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.grupo5.mymovies.adapters.MovieAdapter
import com.grupo5.mymovies.model.MoviesService
import kotlinx.coroutines.*
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.coroutines.CoroutineContext
import com.grupo5.mymovies.model.Movie


class MainActivity : AppCompatActivity(), CoroutineScope {

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    private lateinit var job: Job
    private lateinit var mAdapter : MovieAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val recyclerView = findViewById<View>(R.id.moviesRecycleView) as RecyclerView
        val progress = findViewById<View>(R.id.progress) as ProgressBar
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.itemAnimator = DefaultItemAnimator()
        mAdapter = MovieAdapter(ArrayList<Movie>())
        recyclerView.adapter = mAdapter

        job = Job()
        progress.visibility = View.VISIBLE
        recyclerView.visibility = View.GONE

        launch(Dispatchers.IO) {
            val moviesResponse = createMoviesService().getMoviesAsync(getString(R.string.api_key))
            Log.v("API Response size: ", moviesResponse.results.size.toString())
            withContext(Dispatchers.Main) {
                mAdapter.movies = moviesResponse.results
                mAdapter.notifyDataSetChanged()
                progress.visibility = View.GONE
                recyclerView.visibility = View.VISIBLE
            }
        }
    }


    private fun createMoviesService(): MoviesService {
        return Retrofit.Builder()
            .baseUrl("https://api.themoviedb.org/3/").addConverterFactory(GsonConverterFactory.create())
            .build()
            .run { create<MoviesService>(MoviesService::class.java) }

    }

    override fun onDestroy() {
        job.cancel()
        super.onDestroy()
    }
}
