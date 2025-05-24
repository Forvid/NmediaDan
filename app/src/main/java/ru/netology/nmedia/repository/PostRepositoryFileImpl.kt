package ru.netology.nmedia.repository

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.netology.nmedia.api.ApiModule
import ru.netology.nmedia.data.room.AppDb
import ru.netology.nmedia.data.room.toEntity
import ru.netology.nmedia.dto.Post

class PostRepositoryImpl(context: Context) : PostRepository {
    private val dao = AppDb.getInstance(context).postDao()
    private val api = ApiModule.postsApi

    override fun getAll(): LiveData<List<Post>> =
        // читаем сразу из БД; toDto берётся из PostEntity.kt
        dao.getAll().map { list -> list.map { it.toDto() } }

    override suspend fun likeById(id: Long) = withContext(Dispatchers.IO) {
        val before = dao.getById(id)
        val toggled = before.copy(
            likedByMe = !before.likedByMe,
            likes = before.likes + if (before.likedByMe) -1 else 1
        )
        dao.insert(toggled) // локально
        val resp = if (before.likedByMe) api.unlike(id) else api.like(id)
        if (!resp.isSuccessful) {
            dao.insert(before) // откат
            throw RuntimeException("Ошибка лайка: ${resp.code()}")
        }
    }

    override suspend fun removeById(id: Long) = withContext(Dispatchers.IO) {
        val before = dao.getById(id)
        dao.removeById(id)
        val resp = api.delete(id)
        if (!resp.isSuccessful) {
            dao.insert(before) // откат
            throw RuntimeException("Ошибка удаления: ${resp.code()}")
        }
    }

    override suspend fun save(post: Post) = withContext(Dispatchers.IO) {
        dao.insert(post.toEntity()) // локально
        val resp = if (post.id == 0L) api.create(post) else api.update(post.id, post)
        if (!resp.isSuccessful) {
            throw RuntimeException("Ошибка сохранения: ${resp.code()}")
        }
    }
}
