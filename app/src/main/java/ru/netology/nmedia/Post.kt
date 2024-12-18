package ru.netology.nmedia

data class Post(
    val id: Int,
    val author: String,
    val content: String,
    var likedByMe: Boolean = false,
    var likes: Int = 0,
    var shares: Int = 0,
    var views: Int = 0,
    val published: String,
)
