package org.fnives.library.reloadable.module.processor

import org.fnives.library.reloadable.module.annotation.ReloadableModule
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.Filer
import javax.annotation.processing.Messager
import javax.annotation.processing.ProcessingEnvironment
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.SourceVersion
import javax.lang.model.element.TypeElement
import javax.lang.model.util.Elements
import javax.lang.model.util.Types

class ReloadableModuleProcessor : AbstractProcessor() {

    private lateinit var filer: Filer
    private lateinit var messager: Messager
    private lateinit var elementUtils: Elements
    private lateinit var typeUtils: Types
    private lateinit var generateReloadModuleUseCase: GenerateReloadModuleUseCase
    private lateinit var generateReloadModule: GenerateReloadModule
    private val supportedAnnotations = setOf(ReloadableModule::class.java)

    @Synchronized
    override fun init(processingEnvironment: ProcessingEnvironment) {
        super.init(processingEnvironment)
        filer = processingEnvironment.filer
        messager = processingEnvironment.messager
        elementUtils = processingEnvironment.elementUtils
        typeUtils = processingEnvironment.typeUtils
        generateReloadModuleUseCase = GenerateReloadModuleUseCase(elementUtils, filer)
        generateReloadModule = GenerateReloadModule(elementUtils, filer)
    }

    override fun getSupportedAnnotationTypes(): Set<String> =
        supportedAnnotations.map(Class<*>::getCanonicalName).toSet()

    override fun getSupportedSourceVersion(): SourceVersion = SourceVersion.latestSupported()

    override fun process(set: Set<TypeElement>, roundEnvironment: RoundEnvironment): Boolean {
        if (roundEnvironment.processingOver()) return false
        val annotatedElements = supportedAnnotations.flatMap(roundEnvironment::getElementsAnnotatedWith).filterIsInstance<TypeElement>()

        annotatedElements.forEach {
            generateReloadModuleUseCase.createAndWrite(it)
            generateReloadModule.createAndWrite(it, roundEnvironment)
        }

        return false
    }

}