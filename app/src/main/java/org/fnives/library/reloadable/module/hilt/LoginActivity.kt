package org.fnives.library.reloadable.module.hilt

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import dagger.hilt.android.AndroidEntryPoint
import org.fnives.library.reloadable.module.hilt.usecase.LoginUseCase
import org.fnives.library.reloadable.module.hilt.databinding.ActivityLoginBinding
import javax.inject.Inject

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    @Inject
    lateinit var loginUseCase: LoginUseCase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityLoginBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)

        binding.loginCta.setOnClickListener {
            try {
                loginUseCase.login(binding.nameEditText.text?.toString().orEmpty())
            } catch (illegalArgumentException: IllegalArgumentException) {
                binding.nameInput.error = getString(R.string.please_fill_username)
                return@setOnClickListener
            }
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        binding.nameEditText.doAfterTextChanged {
            binding.nameInput.isErrorEnabled = false
        }
    }
}