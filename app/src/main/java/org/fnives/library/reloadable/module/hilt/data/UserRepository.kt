package org.fnives.library.reloadable.module.hilt.data

import org.fnives.library.reloadable.module.hilt.di.LoggedInModuleInject

class UserRepository @LoggedInModuleInject constructor() {

    var userName: String? = null
        private set

    fun login(name: String) {
        userName = name
    }

}