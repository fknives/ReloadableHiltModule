package org.fnives.library.reloadable.module.hilt.di

import org.fnives.library.reloadable.module.annotation.ReloadableModule

@ReloadableModule
@Target(AnnotationTarget.CONSTRUCTOR)
@Retention(AnnotationRetention.SOURCE)
annotation class LoggedInModuleInject