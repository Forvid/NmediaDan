package ru.netology.nmedia.viewmodel

import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.repository.PostRepository
import javax.inject.Inject

@HiltViewModel
class PostViewModel @Inject constructor(
    private val repository: PostRepository
) : ViewModel() {

    val data: LiveData<List<Post>> = repository.getAll()
    val newCount: LiveData<Int> = repository.newPostsCount()

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private val _edited = MutableLiveData<Post?>()
    val edited: LiveData<Post?> = _edited

    init {
        // при старте подгружаем новые
        refresh()
    }

    fun refresh() = viewModelScope.launch {
        try {
            repository.fetchFromServer()
        } catch (e: Exception) {
            _error.value = e.message
        }
    }

    fun markAllRead() = viewModelScope.launch {
        repository.markAllRead()
    }

    fun likeById(id: Long) = viewModelScope.launch {
        try {
            repository.likeById(id)
        } catch (e: Exception) {
            _error.value = e.message
        }
    }

    fun removeById(id: Long) = viewModelScope.launch {
        try {
            repository.removeById(id)
        } catch (e: Exception) {
            _error.value = e.message
        }
    }

    fun save(post: Post) = viewModelScope.launch {
        try {
            repository.save(post)
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
}
