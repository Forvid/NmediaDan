package ru.netology.nmedia.viewmodel

import android.app.Application
import androidx.lifecycle.*
import kotlinx.coroutines.launch
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.repository.PostRepositoryImpl

class PostViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = PostRepositoryImpl(application)

    /** Лента из БД */
    val data: LiveData<List<Post>> = repository.getAll()

    /** Текст ошибки для Snackbar */
    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    /** Редактирование */
    private val _edited = MutableLiveData<Post?>()
    val edited: LiveData<Post?> = _edited

    init {
        viewModelScope.launch {
            try {
                repository.fetchFromServer()
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    /** Позволяет вручную повторить fetch из UI */
    fun refresh() {
        viewModelScope.launch {
            try {
                repository.fetchFromServer()
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    fun likeById(id: Long) {
        viewModelScope.launch {
            try {
                repository.likeById(id)
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    fun removeById(id: Long) {
        viewModelScope.launch {
            try {
                repository.removeById(id)
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    fun save(post: Post) {
        viewModelScope.launch {
            try {
                repository.save(post)
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    fun edit(post: Post)        = _edited.postValue(post)
    fun createNewPost()         = _edited.postValue(Post(0, "Me", "", "", 0, 0, 0, false, null))
    fun changeContentAndSave(content: String) {
        val post = _edited.value ?: return
        val trimmed = content.trim()
        if (post.content != trimmed) save(post.copy(content = trimmed))
        _edited.value = null
    }
    fun cancelEditing()         = _edited.postValue(null)
}
