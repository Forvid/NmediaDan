package ru.netology.nmedia.repository

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import ru.netology.nmedia.dto.Post
import java.io.*

class PostRepositoryFileImpl(
    context: Context
) : PostRepository {
    private val gson = Gson()
    private val file by lazy { File(context.filesDir, "posts.json") }
    private val type = TypeToken.getParameterized(List::class.java, Post::class.java).type
    private var nextId = 1L
    private var posts = listOf<Post>()
    private val data = MutableLiveData(posts)

    init {
        loadFromFile()
    }

    private fun loadFromFile() {
        if (!file.exists()) return
        try {
            BufferedReader(FileReader(file)).use {
                posts = gson.fromJson(it, type) ?: emptyList()
                nextId = (posts.maxOfOrNull { post -> post.id } ?: 0L) + 1
                data.value = posts
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun getAll(): LiveData<List<Post>> = data

    override fun save(post: Post) {
        posts = if (post.id == 0L) {
            listOf(
                post.copy(
                    id = nextId++,
                    author = "Me",
                    likedByMe = false,
                    published = "now"
                )
            ) + posts
        } else {
            posts.map { if (it.id == post.id) it.copy(content = post.content) else it }
        }
        data.value = posts
        saveToFile()
    }

    override fun likeById(id: Long) {
        posts = posts.map {
            if (it.id != id) it else it.copy(
                likedByMe = !it.likedByMe,
                likes = if (it.likedByMe) it.likes - 1 else it.likes + 1
            )
        }
        data.value = posts
        saveToFile()
    }

    override fun removeById(id: Long) {
        posts = posts.filter { it.id != id }
        data.value = posts
        saveToFile()
    }

    override fun update(post: Post) {
        posts = posts.map { if (it.id == post.id) post else it }
        data.value = posts
        saveToFile()
    }

    override fun shareById(id: Long) {
        posts = posts.map {
            if (it.id == id) it.copy(shares = it.shares + 1) else it
        }
        data.value = posts
        saveToFile()
    }

    private fun saveToFile() {
        try {
            BufferedWriter(FileWriter(file)).use {
                gson.toJson(posts, it)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}
