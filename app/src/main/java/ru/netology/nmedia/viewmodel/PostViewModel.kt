package ru.netology.nmedia.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.repository.PostRepository
import ru.netology.nmedia.repository.PostRepositoryInMemoryImpl

// Значение для сброса состояния
private val empty = Post(
    id = 0,
    content = "",
    author = "Unknown",
    likedByMe = false,
    published = ""
)

class PostViewModel : ViewModel() {
    private val repository: PostRepository = PostRepositoryInMemoryImpl()

    // LiveData со всеми постами
    val data: LiveData<List<Post>> = repository.getAll()

    // MutableLiveData для редактируемого поста
    private val _edited = MutableLiveData(empty)
    val edited: LiveData<Post> get() = _edited

    // Сохранение поста
    fun save() {
        _edited.value?.let {
            if (it.id == 0L) { // Если это новый пост
                repository.save(it)
            } else { // Если редактируется существующий пост
                repository.update(it)
            }
        }
        _edited.postValue(empty) // Сброс редактируемого поста
    }

    // Редактирование поста
    fun edit(post: Post) {
        _edited.value = post
    }

    // Изменение содержимого поста
    fun changeContent(content: String) {
        val trimmedContent = content.trim()
        if (_edited.value?.content == trimmedContent) return
        _edited.postValue(_edited.value?.copy(content = trimmedContent))
    }

    // Лайк поста
    fun likeById(postId: Long) {
        Log.d("PostViewModel", "Liking post: $postId")
        repository.likeById(postId)
    }

    // Шеринг поста
    fun shareById(postId: Long) {
        repository.shareById(postId)
    }

    // Удаление поста
    fun removeById(postId: Long) {
        repository.removeById(postId)
    }

    // Отмена редактирования
    fun clearEdit() {
        _edited.postValue(empty)
    }

    // Новый метод like (если нужен)
    fun like(post: Post) {
        Log.d("PostViewModel", "Liking post: ${post.id}")
        repository.likeById(post.id)
    }
}
