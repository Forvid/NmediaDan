package ru.netology.nmedia.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ru.netology.nmedia.dto.Post

class PostRepository {
    private val _data = MutableLiveData(
        Post(
            id = 1,
            author = "Нетология. Университет интернет-профессий будущего",
            content = "Привет, это новая Нетология! Когда-то Нетология начиналась с интенсивов по онлайн-маркетингу. Затем появились курсы по дизайну, разработке, аналитике и управлению. Мы растём сами и помогаем расти студентам: от новичков до уверенных профессионалов. Но самое важное остаётся с нами: мы верим, что в каждом уже есть сила, которая заставляет хотеть больше, целиться выше, бежать быстрее. Наша миссия — помочь встать на путь роста и начать цепочку перемен → http://netolo.gy/fyb",
            likedByMe = false,
            likes = 999,
            shares = 50,
            views = 1234,
            published = "21 мая в 18:36"
        )
    )
    val data: LiveData<Post> = _data

    fun like() {
        val currentPost = _data.value ?: return
        _data.value = currentPost.copy(
            likedByMe = !currentPost.likedByMe,
            likes = if (currentPost.likedByMe) currentPost.likes - 1 else currentPost.likes + 1
        )
    }

    fun share() {
        val currentPost = _data.value ?: return
        _data.value = currentPost.copy(
            shares = currentPost.shares + 1
        )
    }
}
