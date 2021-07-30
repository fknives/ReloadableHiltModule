package org.fnives.library.reloadable.module.hilt

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint
import org.fnives.library.reloadable.module.hilt.databinding.ActivityLogoutBinding
import org.fnives.library.reloadable.module.hilt.usecase.GetUser
import org.fnives.library.reloadable.module.hilt.usecase.LogoutUseCase
import javax.inject.Inject

@AndroidEntryPoint
class SettingsActivity : AppCompatActivity() {

    @Inject
    lateinit var logoutUseCase: LogoutUseCase

    @Inject
    lateinit var getUserUseCase: GetUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityLogoutBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)
        val user = getUserUseCase.invoke()
        binding.bye.text = getString(R.string.bye_user, user.name, user.content)

        binding.logoutCta.setOnClickListener {
            logoutUseCase.invoke()
            startActivity(Intent(this, LoginActivity::class.java))
            finishAffinity()
        }
    }
}