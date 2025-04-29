package ru.netology.nmedia.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.repository.PostRepositoryFileImpl

class PostViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = PostRepositoryFileImpl(application)
    val data: LiveData<List<Post>> = repository.getAll()

    fun likeById(id: Long)   = repository.likeById(id)
    fun shareById(id: Long)  = repository.shareById(id)
    fun save(post: Post)     = repository.save(post)
    fun update(post: Post)   = repository.update(post)
    fun removeById(id: Long) = repository.removeById(id)
}
