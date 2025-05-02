package ru.netology.nmedia.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.repository.PostRepositoryFileImpl

class PostViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = PostRepositoryFileImpl(application)

    // Листинг всех постов
    val data: LiveData<List<Post>> = repository.getAll()

    // Текущий редактируемый пост (null, когда ничего не редактируется)
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
            // Если это новый пост (id = 0) — POST, иначе POST (т.к. сервер не поддерживает PUT)
            repository.save(post.copy(content = text))
        }
        _edited.value = null
    }


    fun save() {
        _edited.value?.let { post ->
            if (post.id == 0L) repository.save(post)
            else               repository.update(post)
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
