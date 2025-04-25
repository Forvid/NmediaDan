package ru.netology.nmedia.api

import retrofit2.Response
import retrofit2.http.*
import ru.netology.nmedia.dto.Post

interface PostsApiService {
    @GET("api/posts")
    suspend fun getAll(): Response<List<Post>>

    @POST("api/posts")
    suspend fun create(@Body post: Post): Response<Post>

    @PUT("api/posts/{id}")
    suspend fun update(@Path("id") id: Long, @Body post: Post): Response<Post>

    @POST("api/posts/{id}/likes")
    suspend fun like(@Path("id") id: Long): Response<Post>

    @DELETE("api/posts/{id}/likes")
    suspend fun unlike(@Path("id") id: Long): Response<Post>
}
