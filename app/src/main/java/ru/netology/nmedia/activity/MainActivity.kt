package ru.netology.nmedia.activity

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import ru.netology.nmedia.R
import ru.netology.nmedia.adapter.OnInteractionListener
import ru.netology.nmedia.adapter.PostsAdapter
import ru.netology.nmedia.databinding.ActivityMainBinding
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.util.AndroidUtils
import ru.netology.nmedia.viewmodel.PostViewModel

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val viewModel: PostViewModel by viewModels()

        // Инициализация адаптера с реализацией OnInteractionListener
        val adapter = PostsAdapter(object : OnInteractionListener {
            override fun onLike(post: Post) {
                viewModel.likeById(post.id)
            }

            override fun onEdit(post: Post) {
                viewModel.edit(post)
            }

            override fun onRemove(post: Post) {
                viewModel.removeById(post.id)
            }

            override fun onShare(post: Post) {
                viewModel.share(post.id)
                Toast.makeText(
                    this@MainActivity,
                    getString(R.string.post_shared),
                    Toast.LENGTH_SHORT
                ).show()
            }
        })

        binding.list.adapter = adapter

        // Наблюдаем за данными в ViewModel
        viewModel.data.observe(this) { posts ->
            adapter.submitList(posts)
        }

        // Обработка редактирования поста
        viewModel.edited.observe(this) { post ->
            if (post.id == 0L) {
                binding.cancelEdit.visibility = View.GONE  // Скрыть крестик, если не редактируется
                return@observe
            }

            with(binding.content) {
                requestFocus()
                setText(post.content)
            }

            binding.cancelEdit.visibility = View.VISIBLE  // Показать крестик, если редактируется
        }

        // Обработчик клика на кнопку "Отменить редактирование"
        binding.cancelEdit.setOnClickListener {
            with(binding.content) {
                setText("")  // Очистить поле ввода
                clearFocus() // Снять фокус
            }
            binding.cancelEdit.visibility = View.GONE  // Скрыть крестик
            viewModel.cancelEdit()  // Сбросить состояние редактирования в ViewModel
        }

        // Сохранение нового или измененного поста
        binding.save.setOnClickListener {
            with(binding.content) {
                if (text.isNullOrBlank()) {
                    Toast.makeText(
                        this@MainActivity,
                        getString(R.string.error_empty_content),
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setOnClickListener
                }

                viewModel.changeContent(text.toString())
                viewModel.save()
                setText("")
                clearFocus()
                AndroidUtils.hideKeyboard(this)
            }
        }
    }
}

