package ru.netology.nmedia.viewmodel

import android.app.Application
import androidx.lifecycle.*
import kotlinx.coroutines.launch
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.repository.PostRepositoryImpl

class PostViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = PostRepositoryImpl(application)

    /** Локальная лента из БД */
    val data: LiveData<List<Post>> = repository.getAll()

    /** Сообщение об ошибке, показывается в Snackbar */
    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    /** Пост, который сейчас редактируется */
    private val _edited = MutableLiveData<Post?>()
    val edited: LiveData<Post?> = _edited

    init {
        // При запуске сходу подтягиваем с сервера
        viewModelScope.launch {
            try {
                repository.fetchFromServer()
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    /** Позволяет повторить сетевой fetch по кнопке Retry */
    fun refresh() {
        viewModelScope.launch {
            try {
                repository.fetchFromServer()
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    /** Локальный отклик + отправка на сервер с возможным откатом */
    fun likeById(id: Long) {
        viewModelScope.launch {
            try {
                repository.likeById(id)
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    /** Локальный отклик + удаление на сервере с возможным откатом */
    fun removeById(id: Long) {
        viewModelScope.launch {
            try {
                repository.removeById(id)
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    /** Сохранение нового или обновление существующего поста */
    fun save(post: Post) {
        viewModelScope.launch {
            try {
                repository.save(post)
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    /** Начать редактирование поста */
    fun edit(post: Post)        = _edited.postValue(post)
    /** Создать новый пустой пост */
    fun createNewPost()         = _edited.postValue(Post(
        id        = 0,
        author    = "Me",
        content   = "",
        published = "",
        likedByMe = false,
        likes     = 0,
        shares    = 0,
        views     = 0,
        video     = null
    ))
    /** Завершить ввод и сохранить */
    fun changeContentAndSave(content: String) {
        val post = _edited.value ?: return
        val trimmed = content.trim()
        if (post.content != trimmed) {
            save(post.copy(content = trimmed))
        }
        _edited.value = null
    }
    /** Отменить редактирование */
    fun cancelEditing()         = _edited.postValue(null)

    companion object {
        fun provideFactory(app: Application): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T =
                    PostViewModel(app) as T
            }
    }
}
