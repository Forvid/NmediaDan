package ru.netology.nmedia.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.repository.PostRepository
import ru.netology.nmedia.repository.PostRepositoryInMemoryImpl

class PostViewModel : ViewModel() {
    private val repository: PostRepository = PostRepositoryInMemoryImpl()

    // Нужно получить все посты
    val data: LiveData<List<Post>> = repository.getAll()

    fun likeById(postId: Long) {
        repository.likeById(postId)
    }

    fun share(postId: Long) {
        repository.shareById(postId)
    }
}
