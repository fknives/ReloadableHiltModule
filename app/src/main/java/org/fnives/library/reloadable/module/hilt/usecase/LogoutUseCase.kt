package org.fnives.library.reloadable.module.hilt.usecase

import org.fnives.library.reloadable.module.hilt.di.ReloadLoggedInModuleInjectModule
import javax.inject.Inject

class LogoutUseCase @Inject constructor(
    private val reloadLoggedInModuleInjectModule: ReloadLoggedInModuleInjectModule
) {

    fun invoke() {
        reloadLoggedInModuleInjectModule.reload()
    }
}