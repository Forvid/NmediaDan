package ru.netology.nmedia.viewmodel

import android.app.Application
import androidx.lifecycle.*
import kotlinx.coroutines.launch
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.repository.PostRepositoryImpl

class PostViewModel(application: Application) : AndroidViewModel(application) {
    private val repo = PostRepositoryImpl(application)

    val data     = repo.getAll()
    val newCount = repo.newPostsCount()

    private val _error = MutableLiveData<String?>()
    val error     = _error

    private val _edited = MutableLiveData<Post?>()
    val edited    = _edited

    init {
        // при старте подгружаем новые
        viewModelScope.launch {
            try {
                repo.fetchFromServer()
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    fun refresh() = viewModelScope.launch {
        try {
            repo.fetchFromServer()
        } catch (e: Exception) {
            _error.value = e.message
        }
    }

    fun markAllRead() = viewModelScope.launch {
        repo.markAllRead()
    }

    fun likeById(id: Long) = viewModelScope.launch {
        try {
            repo.likeById(id)
        } catch (e: Exception) {
            _error.value = e.message
        }
    }

    fun removeById(id: Long) = viewModelScope.launch {
        try {
            repo.removeById(id)
        } catch (e: Exception) {
            _error.value = e.message
        }
    }

    fun save(post: Post) = viewModelScope.launch {
        try {
            repo.save(post)
        } catch (e: Exception) {
            _error.value = e.message
        }
    }

    fun edit(post: Post) = _edited.postValue(post)
    fun createNewPost() = _edited.postValue(
        Post(0, "Me", "", "", likedByMe = false, likes = 0, shares = 0, views = 0, video = null)
    )

    fun changeContentAndSave(content: String) {
        val post = _edited.value ?: return
        val trimmed = content.trim()
        if (post.content != trimmed) save(post.copy(content = trimmed))
        _edited.value = null
    }

    fun cancelEditing() = _edited.postValue(null)

    companion object {
        fun provideFactory(app: Application): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T =
                    PostViewModel(app) as T
            }
    }
}
