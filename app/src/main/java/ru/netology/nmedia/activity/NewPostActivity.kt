package ru.netology.nmedia.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import ru.netology.nmedia.databinding.ActivityNewPostBinding

class NewPostActivity : AppCompatActivity() {

    companion object {
        const val RESULT_KEY = "postContent"
        const val EXTRA_INITIAL_CONTENT = "initialContent"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Включаем edge-to-edge
        enableEdgeToEdge()

        val binding = ActivityNewPostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val initialContent = intent.getStringExtra(EXTRA_INITIAL_CONTENT)
        if (!initialContent.isNullOrBlank()) {
            binding.edit.setText(initialContent)
        }

        binding.edit.requestFocus()

        binding.ok.setOnClickListener {
            val content = binding.edit.text?.toString()
            if (content.isNullOrBlank()) {
                setResult(Activity.RESULT_CANCELED)
            } else {
                val result = Intent().apply {
                    putExtra(RESULT_KEY, content)
                }
                setResult(Activity.RESULT_OK, result)
            }
            finish()
        }
    }

    private fun enableEdgeToEdge() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
    }
}
