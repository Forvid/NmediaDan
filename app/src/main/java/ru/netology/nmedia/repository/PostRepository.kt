package ru.netology.nmedia.repository

import androidx.lifecycle.LiveData
import ru.netology.nmedia.dto.Post

interface PostRepository {
    fun getAll(): LiveData<List<Post>>
    fun newPostsCount(): LiveData<Int>
    suspend fun fetchFromServer()
    suspend fun markAllRead()
    suspend fun likeById(id: Long)
    suspend fun removeById(id: Long)
    suspend fun save(post: Post)
}
