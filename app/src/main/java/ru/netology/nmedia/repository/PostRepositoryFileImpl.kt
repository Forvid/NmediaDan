package ru.netology.nmedia.repository

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import ru.netology.nmedia.api.ApiModule
import ru.netology.nmedia.data.room.AppDb
import ru.netology.nmedia.data.room.PostEntity
import ru.netology.nmedia.data.room.toEntity
import ru.netology.nmedia.dto.Post

class PostRepositoryImpl(context: Context) : PostRepository {
    private val dao = AppDb.getInstance(context).postDao()
    private val api = ApiModule.postsApi

    override fun getAll(): LiveData<List<Post>> =
        dao.getAllFlow()
            .map { list -> list.map(PostEntity::toDto) }
            .asLiveData()

    override fun newPostsCount(): LiveData<Int> =
        dao.newPostsCount().asLiveData()

    override suspend fun fetchFromServer(): Unit = withContext(Dispatchers.IO) {
        val maxId = dao.getAllFlow()
            .map { it.maxOfOrNull(PostEntity::id) ?: 0L }
            .first()

        val resp = api.getNewer(maxId)
        if (!resp.isSuccessful) throw RuntimeException("Ошибка сети: ${resp.code()}")

        val posts = resp.body().orEmpty()
        dao.insertAll(posts.map { it.toEntity(isNew = true) })

        return@withContext
    }

    override suspend fun markAllRead(): Unit = withContext(Dispatchers.IO) {
        dao.markAllRead()
        return@withContext
    }

    override suspend fun likeById(id: Long): Unit = withContext(Dispatchers.IO) {
        // 1) локально отклик
        dao.likeById(id)
        // 2) на сервере
        val resp = api.like(id)
        if (!resp.isSuccessful) {
            dao.likeById(id)
            throw RuntimeException("Ошибка лайка: ${resp.code()}")
        }
    }

    override suspend fun removeById(id: Long): Unit = withContext(Dispatchers.IO) {
        // 1) локально
        dao.removeById(id)
        // 2) на сервере
        val resp = api.delete(id)
        if (!resp.isSuccessful) {
            throw RuntimeException("Ошибка удаления: ${resp.code()}")
        }
    }

    override suspend fun save(post: Post): Unit = withContext(Dispatchers.IO) {
        // 1) локально
        dao.save(post.toEntity(isNew = false))
        // 2) на сервере
        val resp = if (post.id == 0L) api.create(post) else api.update(post.id, post)
        if (!resp.isSuccessful) {
            throw RuntimeException("Ошибка сохранения: ${resp.code()}")
        }
    }

}
