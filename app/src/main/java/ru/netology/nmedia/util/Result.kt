package ru.netology.nmedia.util

sealed class Result<out T> {
    data class Success<out T>(val data: T)                 : Result<T>()
    data class Error(val code: Int, val errorBody: String?): Result<Nothing>()
    data class Exception(val exception: kotlin.Exception)  : Result<Nothing>()
}
