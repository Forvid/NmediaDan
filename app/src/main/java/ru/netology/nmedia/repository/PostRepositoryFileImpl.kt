package ru.netology.nmedia.repository

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import ru.netology.nmedia.dto.Post
import java.io.*
import java.util.concurrent.TimeUnit

class PostRepositoryFileImpl(context: Context) : PostRepository {
    private val gson = Gson()
    private val file = File(context.filesDir, "posts.json")
    private val type = TypeToken.getParameterized(List::class.java, Post::class.java).type

    // Настраиваем OkHttpClient с таймаутами
    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    private val baseUrl = "http://10.0.2.2:9999"

    // Локальный кэш + LiveData
    private var posts = mutableListOf<Post>()
    private val _data = MutableLiveData<List<Post>>(emptyList())

    override fun getAll(): LiveData<List<Post>> {
        // 1) Загрузить из файла
        loadFromFile()
        _data.value = posts.toList()

        // 2) Подтянуть с сервера
        val request = Request.Builder()
            .url("$baseUrl/api/posts")
            .get()
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) = e.printStackTrace()
            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (!it.isSuccessful) return
                    val body = it.body?.string().orEmpty()
                    val serverPosts: List<Post> = gson.fromJson(body, type)
                    posts = serverPosts.toMutableList()
                    persistAndSaveFile()
                }
            }
        })

        return _data
    }

    override fun save(post: Post) {
        // выберем метод и URL; создадим JSON-тело
        val (method, url, body) = if (post.id == 0L) {
            val json = gson.toJson(post)
            Triple(
                "POST",
                "$baseUrl/api/posts",
                json.toRequestBody("application/json".toMediaTypeOrNull())
            )
        } else {
            val json = gson.toJson(post)
            Triple(
                "PUT",
                "$baseUrl/api/posts/${post.id}",
                json.toRequestBody("application/json".toMediaTypeOrNull())
            )
        }

        val request = Request.Builder()
            .url(url)
            .method(method, body)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) = e.printStackTrace()
            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (!it.isSuccessful) return
                    val updated: Post = gson.fromJson(it.body!!.string(), Post::class.java)
                    syncLocal(updated)
                    persistAndSaveFile()
                }
            }
        })
    }

    override fun update(post: Post) = save(post)

    override fun likeById(postId: Long) {
        val old = posts.firstOrNull { it.id == postId } ?: return
        val method = if (old.likedByMe) "DELETE" else "POST"
        // для POST /likes нужно пустое тело, для DELETE — null
        val body = if (method == "POST") ByteArray(0).toRequestBody() else null

        val request = Request.Builder()
            .url("$baseUrl/api/posts/$postId/likes")
            .method(method, body)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) = e.printStackTrace()
            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (!it.isSuccessful) return
                    val updated: Post = gson.fromJson(it.body!!.string(), Post::class.java)
                    syncLocal(updated)
                    persistAndSaveFile()
                }
            }
        })
    }

    override fun shareById(postId: Long) {
        // локально увеличиваем счётчик, без серверного запроса
        posts.replaceAll { if (it.id == postId) it.copy(shares = it.shares + 1) else it }
        persistAndSaveFile()
    }

    override fun removeById(postId: Long) {
        val request = Request.Builder()
            .url("$baseUrl/api/posts/$postId")
            .delete()
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) = e.printStackTrace()
            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (!it.isSuccessful) return
                    posts.removeAll { it.id == postId }
                    persistAndSaveFile()
                }
            }
        })
    }

    // Обновить LiveData и сохранить в файл
    private fun persistAndSaveFile() {
        _data.postValue(posts.toList())
        saveToFile()
    }

    // Синхронизировать один пост в локальном кэше
    private fun syncLocal(updated: Post) {
        posts.replaceAll { if (it.id == updated.id) updated else it }
    }

    // Загрузка кеша из файла
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

    // Сохранение кеша в файл
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
