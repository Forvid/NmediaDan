package ru.netology.nmedia.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.nmedia.R
import ru.netology.nmedia.activity.AuthActivity
import ru.netology.nmedia.adapter.OnInteractionListener
import ru.netology.nmedia.adapter.PostAdapter
import ru.netology.nmedia.databinding.ActivityMainBinding
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.auth.AuthViewModel
import ru.netology.nmedia.viewmodel.PostViewModel

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private val postViewModel: PostViewModel by viewModels()
    private val authViewModel: AuthViewModel by viewModels()

    private val newPostLauncher =
        registerForActivityResult(NewPostResultContract()) { result ->
            if (result == null) postViewModel.cancelEditing()
            else                postViewModel.changeContentAndSave(result)
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Если не залогинены — запускаем AuthActivity и закрываем MainActivity
        if (!authViewModel.isLoggedIn()) {
            startActivity(Intent(this, AuthActivity::class.java))
            finish()
        }

        // RecyclerView + Adapter
        val adapter = PostAdapter(object : OnInteractionListener {
            override fun onLike(post: Post) {
                postViewModel.likeById(post.id)
            }
            override fun onRemove(post: Post) {
                postViewModel.removeById(post.id)
            }
            override fun onShare(post: Post) {
                sharePost(post.content)
            }
            override fun onVideoOpen(url: String) {
                openVideo(url)
            }
            override fun onEdit(post: Post) {
                postViewModel.edit(post)
            }
        })

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            this.adapter  = adapter
        }

        //Подписываемся на поток данных
        postViewModel.data.observe(this) { posts ->
            adapter.submitList(posts)
        }

        // Баннер новых постов
        postViewModel.newCount.observe(this) { count ->
            binding.bannerNew.visibility = if (count > 0) VISIBLE else GONE
            binding.textNew.text = getString(R.string.new_posts_count, count)
        }
        binding.bannerNew.setOnClickListener {
            postViewModel.markAllRead()
            binding.recyclerView.smoothScrollToPosition(0)
        }

        // Ошибки сети
        postViewModel.error.observe(this) { msg ->
            msg?.let {
                Snackbar.make(binding.root, it, Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.retry) { postViewModel.refresh() }
                    .show()
            }
        }

        //Редактирование и создание нового
        postViewModel.edited.observe(this) { post ->
            post?.let { newPostLauncher.launch(it.content) }
        }
        binding.fab.setOnClickListener {
            postViewModel.createNewPost()
        }

        //  Кнопка «Выйти»
        binding.logoutButton.setOnClickListener {
            authViewModel.logout()
            postViewModel.refresh()
            startActivity(Intent(this, AuthActivity::class.java))
            finish()
        }
    }

    private fun sharePost(content: String) {
        startActivity(
            Intent.createChooser(
                Intent(Intent.ACTION_SEND)
                    .putExtra(Intent.EXTRA_TEXT, content)
                    .setType("text/plain"),
                getString(R.string.chooser_share_post)
            )
        )
    }

    private fun openVideo(url: String) {
        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
    }
}
