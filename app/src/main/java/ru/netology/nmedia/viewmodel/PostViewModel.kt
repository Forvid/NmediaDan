package ru.netology.nmedia.viewmodel

import android.app.Application
import androidx.lifecycle.*
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.repository.PostRepositoryFileImpl

class PostViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = PostRepositoryFileImpl(application)

    val data: LiveData<List<Post>> = repository.getAll()

    private val _edited = MutableLiveData<Post?>()
    val edited: LiveData<Post?> = _edited

    fun likeById(id: Long)    = repository.likeById(id)
    fun shareById(id: Long)   = repository.shareById(id)
    fun removeById(id: Long)  = repository.removeById(id)

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
        val text = content.trim()
        val post = _edited.value ?: return
        if (post.content != text) {
            repository.save(post.copy(content = text))
        }
        _edited.value = null
    }

    fun cancelEditing() {
        _edited.value = null
    }

    companion object {
        fun provideFactory(app: Application): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    @Suppress("UNCHECKED_CAST")
                    return PostViewModel(app) as T
                }
            }
    }
}
