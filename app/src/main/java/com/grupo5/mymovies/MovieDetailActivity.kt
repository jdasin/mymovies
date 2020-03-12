package com.grupo5.mymovies

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.grupo5.mymovies.model.ResultsItem
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_movie_detail.*

class MovieDetailActivity : AppCompatActivity() {

    companion object {
        const val URL_IMAGE_BASE = "https://image.tmdb.org/t/p/w500"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie_detail)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val movieExample = ResultsItem(
            popularity = 98.167,
            voteCount = 21173,
            video = false,
            posterPath = "/qJ2tW6WMUDux911r6m7haRef0WH.jpg",
            id = 155,
            adult = false,
            backdropPath = "/hqkIcbrOHL86UncnHIsHVcVmzue.jpg",
            originalLanguage = "en",
            originalTitle = "The Dark Knight",
            genreIds = listOf(),
            title = "The Dark Knight",
            voteAverage = 8.4,
            overview = "Batman raises the stakes in his war on crime. With the help of Lt. Jim Gordon and District Attorney Harvey Dent, Batman sets out to dismantle the remaining criminal organizations that plague the streets. The partnership proves to be effective, but they soon find themselves prey to a reign of chaos unleashed by a rising criminal mastermind known to the terrified citizens of Gotham as the Joker.",
            releaseDate = "2008-07-16"
        )
        setViews(movieExample)
    }

    private fun setViews(movieExample: ResultsItem) {
        Picasso.get().load(URL_IMAGE_BASE + movieExample.posterPath).into(iv_movie_image)
        tv_movie_name.text = movieExample.title
        tv_overview.text = movieExample.overview
        tv_release_date.text = movieExample.releaseDate
        tv_vote_average.text = movieExample.voteAverage.toString()
    }
}
