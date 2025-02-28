package ru.netology.nmedia.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract

class NewPostResultContract : ActivityResultContract<String?, String?>() {

    override fun createIntent(context: Context, input: String?): Intent {
        val intent = Intent(context, NewPostActivity::class.java)
        // Если есть input, значит это редактирование
        input?.let {
            intent.putExtra(NewPostActivity.EXTRA_INITIAL_CONTENT, it)
        }
        return intent
    }

    override fun parseResult(resultCode: Int, intent: Intent?): String? {
        if (resultCode != Activity.RESULT_OK) return null
        return intent?.getStringExtra(NewPostActivity.RESULT_KEY)
    }
}
