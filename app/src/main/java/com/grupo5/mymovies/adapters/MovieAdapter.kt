package com.grupo5.mymovies.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.grupo5.mymovies.R
import com.grupo5.mymovies.model.Movie

import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.movie_item.view.*

class MovieAdapter(var movies: List<Movie>) :
    RecyclerView.Adapter<MovieAdapter.MovieViewHolder>() {
    fun updateUsers(movies: List<Movie>) {
        this.movies = movies;
        notifyDataSetChanged()
    }
    override fun onCreateViewHolder(parent: ViewGroup, p1: Int) = MovieViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.movie_item, parent, false)
    )

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        holder.bind(movies[position])
    }

    override fun getItemCount() = movies.size

    class MovieViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val imageView = view.image
        private val title = view.title
        private val voteAverage = view.vote_average
        fun bind(movie: Movie) {
            title.text = movie.title
            voteAverage.text = movie.voteAverage.toString()
            Picasso.get().load("https://image.tmdb.org/t/p/w500/" + movie.posterPath).into(imageView)
        }
    }
}