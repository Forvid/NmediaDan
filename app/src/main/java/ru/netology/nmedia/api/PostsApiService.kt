package ru.netology.nmedia.api

import retrofit2.Response
import retrofit2.http.DELETE
import retrofit2.http.POST
import retrofit2.http.Path
import ru.netology.nmedia.dto.Post

interface PostsApiService {
    @POST("api/posts/{id}/likes")
    suspend fun like(@Path("id") id: Long): Response<Post>

    @DELETE("api/posts/{id}/likes")
    suspend fun unlike(@Path("id") id: Long): Response<Post>
}
