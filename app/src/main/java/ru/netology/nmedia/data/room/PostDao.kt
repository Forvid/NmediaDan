package ru.netology.nmedia.data.room

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface PostDao {
    @Query("SELECT * FROM posts ORDER BY id DESC")
    fun getAllFlow(): Flow<List<PostEntity>>

    @Query("SELECT COUNT(*) FROM posts WHERE isNew = 1")
    fun newPostsCount(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(posts: List<PostEntity>)

    @Query("UPDATE posts SET isNew = 0 WHERE isNew = 1")
    suspend fun markAllRead()
}
