package ru.netology.nmedia.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ru.netology.nmedia.dto.Post

class PostRepositoryInMemoryImpl : PostRepository {
    private val posts = mutableListOf(
        Post(
            id = 1,
            author = "Нетология. Университет интернет-профессий будущего",
            published = "21 мая в 18:36",
            content = "Привет, это новая Нетология! Когда-то Нетология начиналась с интенсивов по онлайн-маркетингу...",
            likedByMe = false,
            likes = 999,
            shares = 50,
            views = 100_000
        )
    )

    private val _data = MutableLiveData<List<Post>>(posts)
    override val data: LiveData<List<Post>> = _data  // корректное переопределение

    override fun getAll(): LiveData<List<Post>> = data

    override fun likeById(id: Long) {
        val updatedPosts = posts.map {
            if (it.id == id) {
                it.copy(
                    likedByMe = !it.likedByMe,
                    likes = if (it.likedByMe) it.likes - 1 else it.likes + 1
                )
            } else {
                it
            }
        }
        posts.clear()
        posts.addAll(updatedPosts)
        _data.value = updatedPosts
    }

    override fun shareById(id: Long) {
        val updatedPosts = posts.map {
            if (it.id == id) {
                it.copy(shares = it.shares + 1)
            } else {
                it
            }
        }
        posts.clear()
        posts.addAll(updatedPosts)
        _data.value = updatedPosts
    }
}
