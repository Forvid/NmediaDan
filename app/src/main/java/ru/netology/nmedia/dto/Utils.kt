package ru.netology.nmedia.dto

fun formatCount(count: Int): String {
    return when {
        count >= 1_000_000 -> String.format("%.1fM", count / 1_000_000.0)
        count >= 10_000 -> "${count / 1_000}K"
        count >= 1_000 -> "${count / 1_000}K"
        else -> count.toString()
    }
}