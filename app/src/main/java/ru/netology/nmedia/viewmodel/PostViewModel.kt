package ru.netology.nmedia.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ru.netology.nmedia.repository.PostRepository
import ru.netology.nmedia.repository.PostRepositoryInMemoryImpl
import ru.netology.nmedia.dto.Post

class PostViewModel : ViewModel() {
    private val repository: PostRepository = PostRepositoryInMemoryImpl()
    private val _data = MutableLiveData(repository.get())
    val data: LiveData<Post> get() = _data

    fun like() {
        _data.value = _data.value?.copy(
            likedByMe = !_data.value!!.likedByMe,
            likes = if (_data.value!!.likedByMe) _data.value!!.likes - 1 else _data.value!!.likes + 1
        )
    }

    fun share() {
        _data.value = _data.value?.copy(
            shares = _data.value!!.shares + 1
        )
    }
}
