package ru.netology.nmedia.dto

fun formatCount(count: Int): String {
    return when {
        count >= 1_000_000 -> String.format("%.1fM", count / 1_000_000.0).replace(",", ".")
        count >= 10_000 -> {

            val result = (count / 1_000.0)
            "${Math.floor(result * 10) / 10}K"
        }
        count >= 1_000 -> String.format("%.1fK", count / 1_000.0).replace(",", ".")
        else -> count.toString()
    }
}
