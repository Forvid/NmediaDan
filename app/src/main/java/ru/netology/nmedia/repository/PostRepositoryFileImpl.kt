package ru.netology.nmedia.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import ru.netology.nmedia.api.PostsApiService
import ru.netology.nmedia.data.room.AppDb
import ru.netology.nmedia.data.room.PostEntity
import ru.netology.nmedia.data.room.toEntity
import ru.netology.nmedia.dto.Post
import javax.inject.Inject

class PostRepositoryImpl @Inject constructor(
    db: AppDb,
    private val api: PostsApiService,
) : PostRepository {
    private val dao = db.postDao()

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
    }

    override suspend fun markAllRead(): Unit = withContext(Dispatchers.IO) {
        dao.markAllRead()
    }

    override suspend fun likeById(id: Long): Unit = withContext(Dispatchers.IO) {
        dao.likeById(id)
        val resp = api.like(id)
        if (!resp.isSuccessful) {
            dao.likeById(id)
            throw RuntimeException("Ошибка лайка: ${resp.code()}")
        }
    }

    override suspend fun removeById(id: Long): Unit = withContext(Dispatchers.IO) {
        dao.removeById(id)
        val resp = api.delete(id)
        if (!resp.isSuccessful) throw RuntimeException("Ошибка удаления: ${resp.code()}")
    }

    override suspend fun save(post: Post): Unit = withContext(Dispatchers.IO) {
        dao.save(post.toEntity(isNew = false))
        val resp = if (post.id == 0L) api.create(post) else api.update(post.id, post)
        if (!resp.isSuccessful) throw RuntimeException("Ошибка сохранения: ${resp.code()}")
    }
}
