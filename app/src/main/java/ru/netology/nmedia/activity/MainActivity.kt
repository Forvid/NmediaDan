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
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.nmedia.R
import ru.netology.nmedia.adapter.OnInteractionListener
import ru.netology.nmedia.adapter.PostAdapter
import ru.netology.nmedia.activity.AuthActivity
import ru.netology.nmedia.auth.AuthViewModel
import ru.netology.nmedia.databinding.ActivityMainBinding
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.viewmodel.PostViewModel
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    // ViewModel для постов
    private val vm: PostViewModel by viewModels()
    // ViewModel для авторизации (если используется)
    private val authVm: AuthViewModel by viewModels()


    @Inject lateinit var firebase: FirebaseMessaging

    private val newPostLauncher = registerForActivityResult(NewPostResultContract()) { result ->
        if (result == null) vm.cancelEditing()
        else vm.changeContentAndSave(result)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (!authVm.isLoggedIn()) {
            startActivity(Intent(this, AuthActivity::class.java))
        }

        firebase.token.addOnCompleteListener { task ->
            println("FCM Token: ${task.result}")
        }

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

        vm.data.observe(this) { posts ->
            adapter.submitList(posts)
        }

        vm.newCount.observe(this) { count ->
            binding.bannerNew.visibility = if (count > 0) VISIBLE else GONE
            binding.textNew.text = getString(R.string.new_posts_count, count)
        }
        binding.bannerNew.setOnClickListener {
            vm.markAllRead()
            binding.recyclerView.smoothScrollToPosition(0)
        }

        // Ошибки сети
        vm.error.observe(this) { msg ->
            msg?.let {
                Snackbar.make(binding.root, it, Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.retry) { vm.refresh() }
                    .show()
            }
        }


        vm.edited.observe(this) { post ->
            post?.let { newPostLauncher.launch(it.content) }
        }

        binding.fab.setOnClickListener {
            vm.createNewPost()
        }

        // Кнопка «Выйти»
        binding.logoutButton.setOnClickListener {
            authVm.logout()
            vm.refresh()
            startActivity(Intent(this, AuthActivity::class.java))
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
