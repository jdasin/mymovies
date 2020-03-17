package com.grupo5.mymovies.model

import retrofit2.http.GET
import retrofit2.http.Query

interface MoviesService {

    @GET("discover/movie?")
    suspend fun getMoviesAsync(@Query("api_key") apiKey: String): MoviesResponse

}
