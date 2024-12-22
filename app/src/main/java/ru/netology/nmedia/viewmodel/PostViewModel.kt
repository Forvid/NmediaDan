package ru.netology.nmedia.viewmodel

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
    private val _edited = MutableLiveData<Post>(Post(id = 0, content = "", author = "Unknown", likedByMe = false, published = ""))
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
        _edited.value = Post(id = 0, content = "", author = "Unknown", likedByMe = false, published = "") // Сброс редактируемого поста
    }

    // Редактирование поста
    fun edit(post: Post) {
        _edited.value = post
    }

    // Изменение содержимого поста
    fun changeContent(content: String) {
        val trimmedContent = content.trim()
        _edited.value = _edited.value?.copy(content = trimmedContent)
    }

    // Лайк поста
    fun likeById(postId: Long) {
        repository.likeById(postId)
    }

    // Шеринг поста
    fun share(postId: Long) {
        repository.shareById(postId)
    }

    // Удаление поста
    fun removeById(postId: Long) {
        repository.removeById(postId)
    }

    // Отмена редактирования
    fun cancelEdit() {
        _edited.value = Post(id = 0, content = "", author = "Unknown", likedByMe = false, published = "") // Возврат к пустому состоянию
    }
}

