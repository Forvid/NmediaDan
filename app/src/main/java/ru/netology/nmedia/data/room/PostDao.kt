package ru.netology.nmedia.data.room

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface PostDao {
    @Query("SELECT * FROM posts WHERE isNew = 0 ORDER BY id DESC")
    fun getAllFlow(): Flow<List<PostEntity>>

    @Query("SELECT COUNT(*) FROM posts WHERE isNew = 1")
    fun newPostsCount(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(posts: List<PostEntity>): List<Long>

    @Query("UPDATE posts SET isNew = 0 WHERE isNew = 1")
    fun markAllRead(): Int

    @Query("DELETE FROM posts WHERE id = :id")
    fun removeById(id: Long): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun save(post: PostEntity): Long

    @Query("""
    UPDATE posts
    SET likedByMe = CASE WHEN likedByMe THEN 0 ELSE 1 END,
        likes = likes + CASE WHEN likedByMe THEN -1 ELSE 1 END
    WHERE id = :id
  """)
    fun likeById(id: Long): Int
}
