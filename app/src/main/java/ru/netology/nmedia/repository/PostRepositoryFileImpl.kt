package ru.netology.nmedia.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.*
import ru.netology.nmedia.api.ApiModule
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.util.Result

class PostRepositoryRetrofitImpl : PostRepository {
    private val api = ApiModule.postsApi
    private val ioScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    override fun getAll(): LiveData<Result<List<Post>>> {
        val result = MutableLiveData<Result<List<Post>>>()
        ioScope.launch {
            runCatching { api.getAll() }
                .onSuccess { resp ->
                    if (resp.isSuccessful) {
                        result.postValue(Result.Success(resp.body()!!))
                    } else {
                        result.postValue(Result.Error(resp.code(), resp.errorBody()?.string()))
                    }
                }
                .onFailure { ex ->
                    result.postValue(Result.Exception(ex as Exception))
                }
        }
        return result
    }

    override fun save(post: Post): LiveData<Result<Post>> {
        val result = MutableLiveData<Result<Post>>()
        ioScope.launch {
            runCatching {
                if (post.id == 0L) api.create(post)
                else              api.create(post.copy(id = post.id)) // всегда POST
            }
                .onSuccess { resp ->
                    if (resp.isSuccessful) {
                        result.postValue(Result.Success(resp.body()!!))
                    } else {
                        result.postValue(Result.Error(resp.code(), resp.errorBody()?.string()))
                    }
                }
                .onFailure { t ->
                    result.postValue(Result.Exception(t as Exception))
                }
        }
        return result
    }

    override fun update(post: Post): LiveData<Result<Post>> = save(post)

    override fun likeById(postId: Long): LiveData<Result<Post>> {
        val result = MutableLiveData<Result<Post>>()
        ioScope.launch {
            val allResp = api.getAll()
            if (!allResp.isSuccessful) {
                result.postValue(Result.Error(allResp.code(), allResp.errorBody()?.string()))
                return@launch
            }
            val old = allResp.body()!!.first { it.id == postId }
            runCatching {
                if (old.likedByMe) api.unlike(postId) else api.like(postId)
            }
                .onSuccess { resp ->
                    if (resp.isSuccessful) {
                        result.postValue(Result.Success(resp.body()!!))
                    } else {
                        result.postValue(Result.Error(resp.code(), resp.errorBody()?.string()))
                    }
                }
                .onFailure { t ->
                    result.postValue(Result.Exception(t as Exception))
                }
        }
        return result
    }

    override fun shareById(postId: Long): LiveData<Result<Post>> {
        // сервер не обрабатывает shares, имитируем локально + POST
        val dummy = MutableLiveData<Result<Post>>()
        ioScope.launch {
            // здесь просто возвращаем ошибку по умолчанию или успех без тела
            dummy.postValue(Result.Error(501, "Не поддерживается"))
        }
        return dummy
    }

    override fun removeById(postId: Long): LiveData<Result<Unit>> {
        val result = MutableLiveData<Result<Unit>>()
        ioScope.launch {
            runCatching { api.delete(postId) }
                .onSuccess { resp ->
                    if (resp.isSuccessful) {
                        result.postValue(Result.Success(Unit))
                    } else {
                        result.postValue(Result.Error(resp.code(), resp.errorBody()?.string()))
                    }
                }
                .onFailure { t ->
                    result.postValue(Result.Exception(t as Exception))
                }
        }
        return result
    }
}
