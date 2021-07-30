package org.fnives.library.reloadable.module.hilt.data

import org.fnives.library.reloadable.module.hilt.di.LoggedInModuleInject

class ContentRepository @LoggedInModuleInject constructor(private val contentGenerator: ContentGenerator) {

    private var content: Int? = null

    fun getContent() = content ?: contentGenerator.getContent().also { content = it }
}