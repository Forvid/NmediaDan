package ru.netology.nmedia.dto

data class Post(
    val id: Int,
    val author: String,
    val content: String,
    val likedByMe: Boolean,
    val likes: Int,
    val shares: Int,
    val views: Int,
    val published: String,
) {
    fun like(): Post = copy(
        likedByMe = !likedByMe,
        likes = if (likedByMe) likes - 1 else likes + 1
    )

    fun share(): Post = copy(shares = shares + 1)
}
