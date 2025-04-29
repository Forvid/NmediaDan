package ru.netology.nmedia.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.launch
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import ru.netology.nmedia.R
import ru.netology.nmedia.adapter.OnInteractionListener
import ru.netology.nmedia.adapter.PostAdapter
import ru.netology.nmedia.databinding.ActivityMainBinding
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.viewmodel.PostViewModel

class MainActivity : AppCompatActivity() {

    // Используем фабрику, т.к. ViewModel — AndroidViewModel
    private val viewModel: PostViewModel by viewModels {
        PostViewModel.provideFactory(application)
    }

    private val newPostLauncher = registerForActivityResult(NewPostResultContract()) { result ->
        if (result == null) {
            viewModel.cancelEditing()
        } else {
            viewModel.changeContent(result)
            viewModel.save()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val adapter = PostAdapter(object : OnInteractionListener {
            override fun onLike(post: Post)    = viewModel.likeById(post.id)
            override fun onRemove(post: Post)  = viewModel.removeById(post.id)
            override fun onShare(post: Post)   = sharePost(post.content)
            override fun onVideoOpen(url: String) = openVideo(url)
            override fun onEdit(post: Post)    = viewModel.edit(post)
        })
        binding.recyclerView.adapter = adapter

        // Подписываемся на список постов
        viewModel.data.observe(this) { posts ->
            adapter.submitList(posts)
        }

        // При смене edited открываем редактор
        viewModel.edited.observe(this) { post ->
            post?.let {
                newPostLauncher.launch(it.content)
            }
        }

        binding.fab.setOnClickListener {
            viewModel.createNewPost()
        }
    }

    private fun sharePost(content: String) {
        val intent = Intent.createChooser(
            Intent(Intent.ACTION_SEND)
                .putExtra(Intent.EXTRA_TEXT, content)
                .setType("text/plain"),
            getString(R.string.chooser_share_post)
        )
        startActivity(intent)
    }

    private fun openVideo(url: String) {
        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
    }
}
