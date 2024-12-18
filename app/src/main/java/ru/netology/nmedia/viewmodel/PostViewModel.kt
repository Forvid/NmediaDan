package ru.netology.nmedia.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.repository.PostRepository

class PostViewModel : ViewModel() {
    private val repository = PostRepository()

    val data: LiveData<Post> = repository.data

    fun like() {
        repository.like()
    }

    fun share() {
        repository.share()
    }
}
