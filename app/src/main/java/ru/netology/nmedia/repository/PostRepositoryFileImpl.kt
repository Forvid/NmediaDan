package ru.netology.nmedia.repository

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.netology.nmedia.api.ApiModule
import ru.netology.nmedia.data.room.AppDb
import ru.netology.nmedia.data.room.PostEntity
import ru.netology.nmedia.data.room.toEntity
import ru.netology.nmedia.dto.Post

class PostRepositoryImpl(
    context: Context
) : PostRepository {
    private val dao = AppDb.getInstance(context).postDao()
    private val api = ApiModule.postsApi

    /** Сначала отдаём из БД, а по запросу к серверу обновляем кэш */
    override fun getAll(): LiveData<List<Post>> =
        dao.getAll().map { list -> list.map(PostEntity::toDto) }

    /** Перечитать с сервера и сохранить в БД */
    override suspend fun fetchFromServer(): Unit = withContext(Dispatchers.IO) {
        val resp = api.getAll()
        if (!resp.isSuccessful) {
            throw RuntimeException("Ошибка сети: ${resp.code()}")
        }
        resp.body()
            ?.map(Post::toEntity)
            ?.let { dao.insert(it) } // именно insert(List<PostEntity>)
    }

    /** Локальный отклик + HTTP-запрос, при ошибке откат */
    override suspend fun likeById(id: Long): Unit = withContext(Dispatchers.IO) {
        val before = dao.getById(id)
        val toggled = before.copy(
            likedByMe = !before.likedByMe,
            likes     = before.likes + if (before.likedByMe) -1 else +1
        )
        dao.insert(toggled)

        val resp = if (before.likedByMe) api.unlike(id) else api.like(id)
        if (!resp.isSuccessful) {
            dao.insert(before) // откат
            throw RuntimeException("Ошибка лайка: ${resp.code()}")
        }
    }

    /** Удалить локально + HTTP, при ошибке откат */
    override suspend fun removeById(id: Long): Unit = withContext(Dispatchers.IO) {
        val before = dao.getById(id)
        dao.removeById(id)

        val resp = api.delete(id)
        if (!resp.isSuccessful) {
            dao.insert(before) // откат
            throw RuntimeException("Ошибка удаления: ${resp.code()}")
        }
    }

    /** Сохранить новый или обновить, потом HTTP-запрос (без отката) */
    override suspend fun save(post: Post): Unit = withContext(Dispatchers.IO) {
        dao.insert(post.toEntity())

        val resp = if (post.id == 0L) api.create(post) else api.update(post.id, post)
        if (!resp.isSuccessful) {
            throw RuntimeException("Ошибка сохранения: ${resp.code()}")
        }
    }
}
