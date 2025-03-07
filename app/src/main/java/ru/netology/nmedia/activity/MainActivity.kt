package ru.netology.nmedia.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import ru.netology.nmedia.R
import ru.netology.nmedia.adapter.OnInteractionListener
import ru.netology.nmedia.adapter.PostAdapter
import ru.netology.nmedia.databinding.ActivityMainBinding
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.viewmodel.PostViewModel

class MainActivity : AppCompatActivity() {

    private val viewModel: PostViewModel by viewModels()

    private val newPostLauncher = registerForActivityResult(NewPostResultContract()) { result ->
        if (result == null) {
            viewModel.cancelEditing()
            return@registerForActivityResult
        }
        viewModel.changeContent(result)
        viewModel.save()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val adapter = PostAdapter(object : OnInteractionListener {
            override fun onLike(post: Post) {
                viewModel.likeById(post.id)
            }

            override fun onRemove(post: Post) {
                viewModel.removeById(post.id)
            }

            override fun onEdit(post: Post) {
                viewModel.edit(post)
                newPostLauncher.launch(post.content)
            }

            override fun onShare(post: Post) {
                sharePost(post.content)
            }

            override fun onVideoOpen(url: String) {
                openVideo(url)
            }
        })

        binding.recyclerView.adapter = adapter

        viewModel.data.observe(this) { posts ->
            adapter.submitList(posts)
        }

        binding.fab.setOnClickListener {
            viewModel.createNewPost() // новый пост перед открытием редактора
            newPostLauncher.launch(null)
        }
    }

    private fun sharePost(content: String) {
        val shareIntent = Intent.createChooser(Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, content)
            type = "text/plain"
        }, getString(R.string.chooser_share_post))
        startActivity(shareIntent)
    }

    private fun openVideo(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(intent)
    }
}
