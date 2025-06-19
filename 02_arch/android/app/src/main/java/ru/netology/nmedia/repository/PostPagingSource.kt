package ru.netology.nmedia.repository

import androidx.paging.PagingSource
import androidx.paging.PagingState
import ru.netology.nmedia.api.ApiService
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.error.ApiError

class PostPagingSource(
    private val service: ApiService,
    private val token: String,
) : PagingSource<Long, Post>() {

    override fun getRefreshKey(state: PagingState<Long, Post>): Long? = null

    override suspend fun load(params: LoadParams<Long>): LoadResult<Long, Post> {
        return try {
            val response = when (params) {
                is LoadParams.Refresh ->
                    service.getLatest("Bearer $token", params.loadSize)
                is LoadParams.Prepend ->
                    return LoadResult.Page(
                        data = emptyList(),
                        prevKey = params.key,
                        nextKey = null
                    )
                is LoadParams.Append ->
                    service.getBefore("Bearer $token", params.key, params.loadSize)
            }

            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            val body = response.body()
                ?: throw ApiError(response.code(), response.message())

            val nextKey = body.lastOrNull()?.id
            LoadResult.Page(
                data = body,
                prevKey = params.key,
                nextKey = nextKey
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}
