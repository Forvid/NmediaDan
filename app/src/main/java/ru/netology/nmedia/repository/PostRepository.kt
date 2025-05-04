package ru.netology.nmedia.repository

import androidx.lifecycle.LiveData
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.util.Result

interface PostRepository {
    fun getAll(): LiveData<Result<List<Post>>>
    fun save(post: Post): LiveData<Result<Post>>
    fun likeById(postId: Long): LiveData<Result<Post>>
    fun shareById(postId: Long): LiveData<Result<Post>>
    fun removeById(postId: Long): LiveData<Result<Unit>>
    fun update(post: Post): LiveData<Result<Post>>
}
