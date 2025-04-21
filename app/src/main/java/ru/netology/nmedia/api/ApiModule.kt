package ru.netology.nmedia.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiModule {
    private const val BASE_URL = "http://10.0.2.2:9999/"


    val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val postsApi: PostsApiService = retrofit.create(PostsApiService::class.java)
}
