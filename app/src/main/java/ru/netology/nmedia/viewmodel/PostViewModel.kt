package ru.netology.nmedia.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.repository.PostRepository
import ru.netology.nmedia.repository.PostRepositoryFileImpl

class PostViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: PostRepository = PostRepositoryFileImpl(application)
    val data: LiveData<List<Post>> = repository.getAll()

    private val _edited = MutableLiveData<Post?>()
    val edited: LiveData<Post?> get() = _edited

    fun likeById(id: Long) = repository.likeById(id)
    fun removeById(id: Long) = repository.removeById(id)

    fun edit(post: Post) {
        _edited.value = post
    }

    fun createNewPost() {
        _edited.value = Post(
            id = 0,
            author = "Me",
            content = "",
            published = "Сегодня"
        )
    }

    fun changeContent(content: String) {
        val text = content.trim()
        val post = _edited.value ?: return
        if (post.content == text) return
        _edited.value = post.copy(content = text)
    }

    fun save() {
        _edited.value?.let { post ->
            if (post.id == 0L) {
                repository.save(post) // Создание нового поста
            } else {
                repository.update(post) // Обновление существующего
            }
        }
        _edited.value = null
    }

    fun cancelEditing() {
        _edited.value = null
    }
}
