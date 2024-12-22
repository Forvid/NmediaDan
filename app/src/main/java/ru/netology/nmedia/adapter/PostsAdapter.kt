package ru.netology.nmedia.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.CardPostBinding
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.dto.formatCount

// Интерфейс для взаимодействий с элементами поста
interface OnInteractionListener {
    fun onLike(post: Post)
    fun onEdit(post: Post)
    fun onRemove(post: Post)
    fun onShare(post: Post)
}

// Адаптер для списка постов
class PostsAdapter(
    private val onInteractionListener: OnInteractionListener
) : ListAdapter<Post, PostsAdapter.PostViewHolder>(DiffCallback()) {

    // Создание ViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val binding = CardPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PostViewHolder(binding, onInteractionListener)
    }

    // Привязка данных к ViewHolder
    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    // ViewHolder для отображения поста
    class PostViewHolder(
        private val binding: CardPostBinding,
        private val onInteractionListener: OnInteractionListener
    ) : RecyclerView.ViewHolder(binding.root) {

        // Метод для привязки данных поста
        fun bind(post: Post) {
            binding.apply {
                // Заполнение полей данными из поста
                author.text = post.author
                published.text = post.published
                content.text = post.content

                // Форматируем количество лайков и делаем их красивыми
                likeCount.text = formatCount(post.likes)
                shareCount.text = formatCount(post.shares)

                // Установка состояния кнопки лайка в зависимости от того, лайкнул ли пост пользователь
                like.setImageResource(
                    if (post.likedByMe) R.drawable.ic_liked_24 else R.drawable.ic_like_24
                )

                // Обработчики кликов на кнопки
                like.setOnClickListener { onInteractionListener.onLike(post) }
                share.setOnClickListener { onInteractionListener.onShare(post) }

                // PopupMenu для редактирования и удаления
                menu.setOnClickListener {
                    PopupMenu(it.context, it).apply {
                        inflate(R.menu.options_post) // Разворачиваем меню с опциями
                        setOnMenuItemClickListener { item ->
                            when (item.itemId) {
                                R.id.remove -> {
                                    onInteractionListener.onRemove(post)
                                    true
                                }
                                R.id.edit -> {
                                    onInteractionListener.onEdit(post)
                                    true
                                }
                                else -> false
                            }
                        }
                    }.show()
                }
            }
        }
    }

    // DiffUtil для оптимизации обновлений списка
    class DiffCallback : DiffUtil.ItemCallback<Post>() {
        // Проверка идентичности объектов
        override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean =
            oldItem.id == newItem.id

        // Проверка содержимого объектов
        override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean =
            oldItem == newItem
    }
}
