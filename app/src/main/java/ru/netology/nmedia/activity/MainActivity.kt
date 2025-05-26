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
import ru.netology.nmedia.R
import ru.netology.nmedia.adapter.OnInteractionListener
import ru.netology.nmedia.adapter.PostAdapter
import ru.netology.nmedia.databinding.ActivityMainBinding
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.viewmodel.PostViewModel

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val vm: PostViewModel by viewModels { PostViewModel.provideFactory(application) }

    private val newPostLauncher = registerForActivityResult(NewPostResultContract()) { result ->
        if (result == null) vm.cancelEditing()
        else                vm.changeContentAndSave(result)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val adapter = PostAdapter(object : OnInteractionListener {
            override fun onLike(post: Post) {
                vm.likeById(post.id)
            }
            override fun onRemove(post: Post) {
                vm.removeById(post.id)
            }
            override fun onShare(post: Post) {
                sharePost(post.content)
            }
            override fun onVideoOpen(url: String) {
                openVideo(url)
            }
            override fun onEdit(post: Post) {
                vm.edit(post)
            }
        })

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter

        // 1) поток данных
        vm.data.observe(this) { posts ->
            adapter.submitList(posts)
        }

        // 2) баннер новых постов
        vm.newCount.observe(this) { count ->
            binding.bannerNew.visibility = if (count > 0) VISIBLE else GONE
            binding.textNew.text = getString(R.string.new_posts_count, count)
        }
        binding.bannerNew.setOnClickListener {
            vm.markAllRead()
            binding.recyclerView.smoothScrollToPosition(0)
        }

        // 3) ошибки
        vm.error.observe(this) { msg ->
            msg?.let {
                Snackbar.make(binding.root, it, Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.retry) { vm.refresh() }
                    .show()
            }
        }

        // 4) редактирование
        vm.edited.observe(this) { post ->
            post?.let { newPostLauncher.launch(it.content) }
        }

        // 5) FAB
        binding.fab.setOnClickListener {
            vm.createNewPost()
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
