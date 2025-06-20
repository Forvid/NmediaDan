package ru.netology.nmedia.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.nmedia.R
import ru.netology.nmedia.auth.AuthViewModel
import ru.netology.nmedia.databinding.ActivityAuthBinding
import ru.netology.nmedia.viewmodel.PostViewModel

@AndroidEntryPoint
class AuthActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAuthBinding
    private val authViewModel: AuthViewModel by viewModels()
    private val postViewModel: PostViewModel by viewModels() // Hilt

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.loginButton.setOnClickListener {
            val user = binding.loginEdit.text.toString()
            val pass = binding.passEdit.text.toString()
            authViewModel.login(user, pass)
        }

        authViewModel.loginResult.observe(this) { success ->
            if (success) {

                postViewModel.refresh()

                finish()
            } else {
                Toast.makeText(this, "Неверные данные", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
