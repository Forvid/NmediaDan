package ru.netology.nmedia.viewmodel

import android.app.Application
import androidx.lifecycle.*
import kotlinx.coroutines.launch
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.repository.PostRepositoryImpl

class PostViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = PostRepositoryImpl(application)

    // Лайва без сетевых ошибок — зеркалим БД
    val data: LiveData<List<Post>> = repository.getAll()

    // Ошибка как строка для Snackbar
    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    // Для открытия редактора
    private val _edited = MutableLiveData<Post?>()
    val edited: LiveData<Post?> = _edited

    init {
        loadAll()
    }

    fun loadAll() {
        viewModelScope.launch {
            try {
                repository.getAll().value // триггер, но фактически мы делаем save каждого поста:
                repository.getAll().value?.forEach { repository.save(it) }
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

    fun edit(post: Post) {
        _edited.value = post
    }

    fun createNewPost() {
        _edited.value = Post(id = 0L, author = "Me", content = "", published = "")
    }

    fun changeContentAndSave(content: String) {
        val post = _edited.value ?: return
        val trimmed = content.trim()
        if (post.content != trimmed) {
            save(post.copy(content = trimmed))
        }
        _edited.value = null
    }

    fun cancelEditing() {
        _edited.value = null
    }
}
