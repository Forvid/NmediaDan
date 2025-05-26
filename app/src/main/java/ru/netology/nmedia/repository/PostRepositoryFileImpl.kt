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
            .map { list -> list.map { it.toDto() } }
            .asLiveData()

    override fun newPostsCount(): LiveData<Int> =
        dao.newPostsCount().asLiveData()

    override suspend fun fetchFromServer() = withContext(Dispatchers.IO) {
        val maxId = dao.getAllFlow().map { it.maxOfOrNull(PostEntity::id) ?: 0L }.first()
        val resp = api.getNewer(maxId)
        if (!resp.isSuccessful) throw RuntimeException("Ошибка сети: ${resp.code()}")
        val posts = resp.body().orEmpty()
        dao.insertAll(posts.map { it.toEntity(isNew = true) })
    }

    override suspend fun markAllRead() = withContext(Dispatchers.IO) {
        dao.markAllRead()
    }

    override suspend fun likeById(id: Long) = withContext(Dispatchers.IO) {
    }

    override suspend fun removeById(id: Long) = withContext(Dispatchers.IO) {
    }

    override suspend fun save(post: Post) = withContext(Dispatchers.IO) {
    }
}
