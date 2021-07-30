package org.fnives.library.reloadable.module.hilt

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint
import org.fnives.library.reloadable.module.hilt.databinding.ActivityMainBinding
import org.fnives.library.reloadable.module.hilt.usecase.GetUser
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var getUser: GetUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)

        val user = getUser.invoke()
        binding.welcome.text = getString(R.string.hello_user, user.name, user.content)
        binding.settingsCta.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }
    }
}