package ru.netology.nmedia.dto

fun formatCount(count: Int): String {
    return when {
        count >= 1_000_000 -> String.format("%.1fM", count / 1_000_000.0).replace(",", ".")
        count >= 10_000 -> "${count / 1_000}K"
        count >= 1_000 -> {
            val result = count / 1_000.0
            "${Math.floor(result * 10) / 10}K" // Округление вниз для диапазона от 1,000 до 10,000
        }
        else -> count.toString()
    }
}
