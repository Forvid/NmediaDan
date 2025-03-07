package ru.netology.nmedia.viewmodel

import android.app.Application
import androidx.lifecycle.*
import ru.netology.nmedia.repository.PostRepository
import ru.netology.nmedia.repository.PostRepositoryFileImpl
import ru.netology.nmedia.dto.Post

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

    fun changeContent(content: String) {
        val text = content.trim()
        val post = _edited.value ?: return // Исправлено: не вызывает copy() у null
        if (post.content == text) return
        _edited.value = post.copy(content = text)
    }

    fun save() {
        _edited.value?.let { post ->
            if (post.id == 0L) {
                repository.save(post) // Новый пост
            } else {
                repository.update(post) // Обновление
            }
        }
        _edited.value = null
    }

    fun cancelEditing() {
        _edited.value = null
    }
}
