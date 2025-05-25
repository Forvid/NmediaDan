package ru.netology.nmedia.data.room

import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.netology.nmedia.dto.Post

@Entity(tableName = "posts")
data class PostEntity(
    @PrimaryKey val id: Long,
    val author: String,
    val content: String,
    val published: String,
    val likedByMe: Boolean,
    val likes: Int,
    val shares: Int,
    val views: Int,
    val video: String?
) {

    fun toDto(): Post =
        Post(
            id        = id,
            author    = author,
            content   = content,
            published = published,
            likes     = likes,
            shares    = shares,
            views     = views,
            likedByMe = likedByMe,
            video     = video
        )
}


fun Post.toEntity(): PostEntity =
    PostEntity(
        id        = id,
        author    = author,
        content   = content,
        published = published,
        likedByMe = likedByMe,
        likes     = likes,
        shares    = shares,
        views     = views,
        video     = video
    )
