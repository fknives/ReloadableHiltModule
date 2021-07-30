package org.fnives.library.reloadable.module.hilt.usecase

import org.fnives.library.reloadable.module.hilt.data.UserRepository
import javax.inject.Inject

class LoginUseCase @Inject constructor(private val userRepository: UserRepository) {

    @Throws(IllegalArgumentException::class)
    fun login(name: String) {
        if (name.isEmpty()) throw IllegalArgumentException("")
        userRepository.login(name)
    }
}