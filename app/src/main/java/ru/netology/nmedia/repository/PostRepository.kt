package ru.netology.nmedia.repository

import ru.netology.nmedia.dto.Post

interface PostRepository {
    fun get(): Post
    fun like()
    fun share()
}
