package org.fnives.library.reloadable.module.hilt.usecase

import org.fnives.library.reloadable.module.hilt.data.ContentRepository
import org.fnives.library.reloadable.module.hilt.data.User
import org.fnives.library.reloadable.module.hilt.data.UserRepository
import javax.inject.Inject

class GetUser @Inject constructor(
    private val userRepository: UserRepository,
    private val contentRepository: ContentRepository
) {

    fun invoke() = User(userRepository.userName.orEmpty(), contentRepository.getContent())
}