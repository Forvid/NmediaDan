package ru.netology.nmedia.repository

import androidx.lifecycle.LiveData
import ru.netology.nmedia.dto.Post

interface PostRepository {
    fun getAll(): LiveData<List<Post>>
    fun likeById(postId: Long)
    fun shareById(postId: Long)
    fun save(post: Post)
    fun removeById(postId: Long)
    fun update(post: Post)

}
