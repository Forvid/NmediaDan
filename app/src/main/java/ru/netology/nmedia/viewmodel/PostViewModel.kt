package ru.netology.nmedia.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.repository.PostRepositoryInMemoryImpl
import ru.netology.nmedia.repository.PostRepository

class PostViewModel(application: Application) : AndroidViewModel(application) {

    // Используем именно in‑memory реализацию
    private val repository: PostRepository = PostRepositoryInMemoryImpl()

    // Всё, что нам выдал репозиторий
    val data: LiveData<List<Post>> = repository.getAll()

    // Сейчас редактируемый пост
    private val _edited = MutableLiveData<Post?>()
    val edited: LiveData<Post?> get() = _edited

    // Лайк/Удалить/Редактировать/Создать/Сохранить

    fun likeById(postId: Long) = repository.likeById(postId)
    fun removeById(postId: Long) = repository.removeById(postId)

    fun edit(post: Post) {
        _edited.value = post
    }

    fun createNewPost() {
        _edited.value = Post(
            id = 0,
            author = "я",
            content = "",
            published = "сегодня"
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
            if (post.id == 0L) repository.save(post)
            else                 repository.update(post)
        }
        _edited.value = null
    }

    fun cancelEditing() {
        _edited.value = null
    }
}
