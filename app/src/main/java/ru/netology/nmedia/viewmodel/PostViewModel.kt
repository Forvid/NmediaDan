package ru.netology.nmedia.viewmodel

import android.app.Application
import androidx.lifecycle.*
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.repository.PostRepositoryRetrofitImpl
import ru.netology.nmedia.util.Result

class PostViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = PostRepositoryRetrofitImpl()

    private val _error = MutableLiveData<Result.Error>()
    val error: LiveData<Result.Error> = _error

    private val _data = MediatorLiveData<List<Post>>()
    val data: LiveData<List<Post>> = _data

    private val _edited = MutableLiveData<Post?>()
    val edited: LiveData<Post?> = _edited

    init {
        loadAll()
    }

    fun loadAll() {
        val source: LiveData<Result<List<Post>>> = repository.getAll()
        _data.addSource(source) { result ->
            when (result) {
                is Result.Success<List<Post>> -> _data.value = result.data
                is Result.Error               -> _error.value = result
                is Result.Exception           -> _error.value = Result.Error(-1, result.exception.message)
            }
            _data.removeSource(source)
        }
    }

    fun likeById(id: Long) {
        val old = _data.value.orEmpty()
        val updated = old.map {
            if (it.id == id) it.copy(likedByMe = !it.likedByMe,
                likes = if (it.likedByMe) it.likes - 1 else it.likes + 1) else it
        }
        _data.value = updated

        val source = repository.likeById(id)
        _data.addSource(source) { result ->
            when (result) {
                is Result.Success<Post> -> loadAll()
                is Result.Error, is Result.Exception -> {
                    _error.value = result as? Result.Error ?: Result.Error(-1, (result as Result.Exception).exception.message)
                    _data.value = old // откат
                }
            }
            _data.removeSource(source)
        }
    }

    fun save(post: Post) {
        val source: LiveData<Result<Post>> = repository.save(post)
        _data.addSource(source) { result ->
            when (result) {
                is Result.Success<Post>   -> loadAll()
                is Result.Error           -> _error.value = result
                is Result.Exception       -> _error.value = Result.Error(-1, result.exception.message)
            }
            _data.removeSource(source)
        }
    }

    fun removeById(id: Long) {
        val old = _data.value.orEmpty()
        val updated = old.filter { it.id != id }
        _data.value = updated

        val source = repository.removeById(id)
        _data.addSource(source) { result ->
            when (result) {
                is Result.Success<Unit> -> loadAll()
                is Result.Error, is Result.Exception -> {
                    _error.value = result as? Result.Error ?: Result.Error(-1, (result as Result.Exception).exception.message)
                    _data.value = old // откат
                }
            }
            _data.removeSource(source)
        }
    }


    fun edit(post: Post) {
        _edited.value = post
    }

    fun createNewPost() {
        _edited.value = Post(
            id = 0L,
            author = "Me",
            content = "",
            published = ""
        )
    }

    fun changeContentAndSave(content: String) {
        val post = _edited.value ?: return
        val text = content.trim()
        if (post.content != text) {
            save(post.copy(content = text))
        }
        _edited.value = null
    }

    fun cancelEditing() {
        _edited.value = null
    }


    companion object {
        fun provideFactory(app: Application): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T =
                    PostViewModel(app) as T
            }
    }
}