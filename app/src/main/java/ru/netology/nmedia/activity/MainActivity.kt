package ru.netology.nmedia.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import ru.netology.nmedia.R
import ru.netology.nmedia.adapter.OnInteractionListener
import ru.netology.nmedia.adapter.PostAdapter
import ru.netology.nmedia.databinding.ActivityMainBinding
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.viewmodel.PostViewModel

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private val viewModel: PostViewModel by viewModels {
        PostViewModel.provideFactory(application)
    }

    private var lastAction: (() -> Unit)? = null

    private val newPostLauncher = registerForActivityResult(NewPostResultContract()) { result ->
        if (result == null) {
            viewModel.cancelEditing()
        } else {
            viewModel.changeContentAndSave(result)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val adapter = PostAdapter(object : OnInteractionListener {
            override fun onLike(post: Post) {
                lastAction = { viewModel.likeById(post.id) }
                viewModel.likeById(post.id)
            }

            override fun onRemove(post: Post) {
                lastAction = { viewModel.removeById(post.id) }
                viewModel.removeById(post.id)
            }

            override fun onShare(post: Post) = sharePost(post.content)

            override fun onVideoOpen(url: String) = openVideo(url)

            override fun onEdit(post: Post) = viewModel.edit(post)
        })

        binding.recyclerView.adapter = adapter

        viewModel.data.observe(this) { list ->
            adapter.submitList(list)
        }

        viewModel.edited.observe(this) { post ->
            post?.let {
                newPostLauncher.launch(it.content)
            }
        }

        viewModel.error.observe(this) { err ->
            Snackbar.make(
                binding.root,
                "Ошибка ${err.code}: ${err.errorBody ?: "нет тела"}",
                Snackbar.LENGTH_INDEFINITE
            )
                .setAction("Повторить") {
                    lastAction?.invoke() ?: viewModel.loadAll()
                }
                .show()
        }

        binding.fab.setOnClickListener {
            viewModel.createNewPost()
        }

        // Первая загрузка — сохранить как последнее действие
        lastAction = { viewModel.loadAll() }
        viewModel.loadAll()
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
