package org.fnives.library.reloadable.module.processor

import com.squareup.javapoet.ClassName
import com.squareup.javapoet.JavaFile
import com.squareup.javapoet.MethodSpec
import com.squareup.javapoet.TypeSpec
import javax.annotation.processing.Filer
import javax.lang.model.element.Modifier
import javax.lang.model.element.TypeElement
import javax.lang.model.util.Elements

class GenerateReloadModuleUseCase(private val elementUtils: Elements, private val filer: Filer) {

    fun createAndWrite(typeElement: TypeElement) {
        val typeSpec = createTypeSpec(getModuleUseCaseName(typeElement))
        val packageName = elementUtils.getPackageOf(typeElement).toString()
        writeToSourceFile(typeSpec, packageName, filer)
    }

    private fun createTypeSpec(moduleUseCaseName: String) =
        TypeSpec.interfaceBuilder(moduleUseCaseName)
            .addModifiers(Modifier.PUBLIC)
            .addMethod(
                MethodSpec.methodBuilder(getReloadMethodName())
                    .addModifiers(Modifier.ABSTRACT)
                    .addModifiers(Modifier.PUBLIC)
                    .build()
            )
            .build()

    private fun writeToSourceFile(typeSpec: TypeSpec, packageName: String, filer: Filer) {
        JavaFile.builder(packageName, typeSpec).build().writeTo(filer)
    }

    companion object {
        fun getModuleUseCaseName(typeElement: TypeElement) = "Reload${typeElement.simpleName}Module"

        fun getReloadMethodName() = "reload"

        fun getTypeName(typeElement: TypeElement, elementUtils: Elements) =
            ClassName.bestGuess("${elementUtils.getPackageOf(typeElement)}.${getModuleUseCaseName(typeElement)}")
    }
}