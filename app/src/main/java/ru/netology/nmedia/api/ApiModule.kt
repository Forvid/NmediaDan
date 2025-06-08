package ru.netology.nmedia.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ApiModule @Inject constructor() {
    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val postsApi: PostsApiService by lazy {
        retrofit.create(PostsApiService::class.java)
    }

    companion object {
        private const val BASE_URL = "http://10.0.2.2:9999/"
    }
}