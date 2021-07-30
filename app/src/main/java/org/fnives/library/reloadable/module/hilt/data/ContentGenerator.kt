package org.fnives.library.reloadable.module.hilt.data

import javax.inject.Inject
import kotlin.random.Random

class ContentGenerator @Inject constructor(){

    fun getContent() = Random.nextInt(100)
}