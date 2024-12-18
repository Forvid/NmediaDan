package ru.netology.nmedia

import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    private lateinit var post: Post

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Создаем пост
        post = Post(
            id = 1,
            author = "Нетология. Университет интернет-профессий будущего",
            content = "Привет, это новая Нетология! Когда-то Нетология начиналась с интенсивов по онлайн-маркетингу. Затем появились курсы по дизайну, разработке, аналитике и управлению. Мы растём сами и помогаем расти студентам: от новичков до уверенных профессионалов. Но самое важное остаётся с нами: мы верим, что в каждом уже есть сила, которая заставляет хотеть больше, целиться выше, бежать быстрее. Наша миссия — помочь встать на путь роста и начать цепочку перемен → http://netolo.gy/fyb",
            published = "21 мая в 18:36",
            likedByMe = false
        )

        // Привязываем элементы интерфейса
        val likeButton = findViewById<ImageButton>(R.id.like)
        val likeCount = findViewById<TextView>(R.id.likeCount)
        val shareButton = findViewById<ImageButton>(R.id.share)
        val shareCount = findViewById<TextView>(R.id.shareCount)
        val viewCount = findViewById<TextView>(R.id.viewsCount)

        // Устанавливаем начальные значения
        likeCount.text = formatCount(post.likes)
        shareCount.text = formatCount(post.shares)
        viewCount.text = formatCount(post.views)

        // Логика кнопки Like
        likeButton.setOnClickListener {
            post.likedByMe = !post.likedByMe
            if (post.likedByMe) {
                post.likes++
                likeButton.setImageResource(R.drawable.ic_liked_24)
            } else {
                post.likes--
                likeButton.setImageResource(R.drawable.ic_like_24)
            }
            likeCount.text = formatCount(post.likes)
        }

        // Логика кнопки Share
        shareButton.setOnClickListener {
            post.shares++
            shareCount.text = formatCount(post.shares)
        }
    }
}
