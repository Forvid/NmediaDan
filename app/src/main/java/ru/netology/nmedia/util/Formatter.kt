package ru.netology.nmedia.util

fun formatCount(count: Int): String {
    return when {
        count >= 1_000_000 -> "${count / 1_000_000}M"
        count >= 10_000 -> "${count / 1_000}K"
        else -> count.toString()
    }
}

