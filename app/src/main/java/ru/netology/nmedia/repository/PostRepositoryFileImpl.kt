package ru.netology.nmedia.repository

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.*
import ru.netology.nmedia.api.ApiModule
import ru.netology.nmedia.dto.Post
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.*

class PostRepositoryFileImpl(
    context: Context
) : PostRepository {
    private val api = ApiModule.postsApi
    private val gson = Gson()
    private val file by lazy { File(context.filesDir, "posts.json") }
    private val type = TypeToken.getParameterized(List::class.java, Post::class.java).type

    // локальный кеш + LiveData
    private var posts = mutableListOf<Post>()
    private val _data = MutableLiveData<List<Post>>(emptyList())
    override fun getAll(): LiveData<List<Post>> = _data

    // Инициализация: сначала из файла, потом из сети
    init {
        loadFromFile()
        _data.value = posts.toList()
        // теперь подгружаем с сервера
        CoroutineScope(Dispatchers.IO).launch {
            fetchFromServer()
        }
    }

    override fun save(post: Post) {
        CoroutineScope(Dispatchers.IO).launch {
            val response = if (post.id == 0L) {
                api.create(post)
            } else {
                api.update(post.id, post)
            }
            if (response.isSuccessful) {
                response.body()?.let { saved ->
                    syncLocal(saved)
                    saveToFile()
                }
            }
        }
    }
    override fun update(post: Post) {
        // Просто перенаправляем в save — там уже идёт логика create vs update на сервере
        save(post)
    }

    override fun likeById(id: Long) {
        CoroutineScope(Dispatchers.IO).launch {
            val old = posts.first { it.id == id }
            val response = if (old.likedByMe) api.unlike(id) else api.like(id)
            if (response.isSuccessful) {
                response.body()?.let { updated ->
                    syncLocal(updated)
                    saveToFile()
                }
            }
        }
    }

    override fun shareById(id: Long) {
        posts.replaceAll { if (it.id == id) it.copy(shares = it.shares + 1) else it }
        persistAndPost()
        saveToFile()
    }

    override fun removeById(id: Long) {
        posts.removeAll { it.id == id }
        persistAndPost()
        saveToFile()
    }

    // Скачиваем весь список с сервера
    private suspend fun fetchFromServer() {
        runCatching { api.getAll() }
            .onSuccess { resp ->
                if (resp.isSuccessful) {
                    resp.body()?.let { serverPosts ->
                        posts = serverPosts.toMutableList()
                        persistAndPost()
                        saveToFile()
                    }
                }
            }
            .onFailure { it.printStackTrace() }
    }

    // Синхронизировать один пост
    private fun syncLocal(updated: Post) {
        posts.replaceAll { if (it.id == updated.id) updated else it }
        persistAndPost()
    }

    // Обновить LiveData из `posts`
    private fun persistAndPost() {
        _data.postValue(posts.toList())
    }

    // Загрузка из файла
    private fun loadFromFile() {
        if (!file.exists()) return
        try {
            BufferedReader(FileReader(file)).use { reader ->
                val list: List<Post> = gson.fromJson(reader, type) ?: emptyList()
                posts = list.toMutableList()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // Сохранение в файл
    private fun saveToFile() {
        try {
            BufferedWriter(FileWriter(file)).use { writer ->
                gson.toJson(posts, writer)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}
