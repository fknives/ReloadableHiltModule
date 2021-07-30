package org.fnives.library.reloadable.module.processor

import com.squareup.javapoet.AnnotationSpec
import com.squareup.javapoet.ClassName
import com.squareup.javapoet.CodeBlock
import com.squareup.javapoet.FieldSpec
import com.squareup.javapoet.JavaFile
import com.squareup.javapoet.MethodSpec
import com.squareup.javapoet.ParameterSpec
import com.squareup.javapoet.ParameterizedTypeName
import com.squareup.javapoet.TypeName
import com.squareup.javapoet.TypeSpec
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import org.fnives.library.reloadable.module.processor.GenerateReloadModuleUseCase.Companion.getModuleUseCaseName
import javax.annotation.processing.Filer
import javax.annotation.processing.RoundEnvironment
import javax.inject.Provider
import javax.lang.model.element.ExecutableElement
import javax.lang.model.element.Modifier
import javax.lang.model.element.TypeElement
import javax.lang.model.util.Elements

class GenerateReloadModule(private val elementUtils: Elements, private val filer: Filer) {

    fun createAndWrite(
        typeElement: TypeElement,
        roundEnvironment: RoundEnvironment
    ) {
        val annotatedElements = roundEnvironment.getElementsAnnotatedWith(typeElement)
            .filterIsInstance<ExecutableElement>()
        val annotatedTypes = annotatedElements.map { it.enclosingElement as TypeElement }

        val reloadFunctionSpec = createReloadFunctionSpec(annotatedTypes)
        val cachedProperties = annotatedTypes.map(::getPropertySpec)
        val provideMethods = annotatedElements.mapIndexed { index, it ->
            createProvideMethodForDependency(it, annotatedTypes[index])
        }
        val typeSpec = createTypeSpec(typeElement, reloadFunctionSpec, provideMethods, cachedProperties)

        val packageName = elementUtils.getPackageOf(typeElement).toString()
        writeToSourceFile(typeSpec, packageName, filer)
    }

    private fun writeToSourceFile(typeSpec: TypeSpec, packageName: String, filer: Filer) {
        JavaFile.builder(packageName, typeSpec).build().writeTo(filer)
    }

    private fun getPropertySpec(typeElement: TypeElement): FieldSpec =
        FieldSpec.builder(TypeName.get(typeElement.asType()), typeElement.cachedPropertyName())
            .initializer("null")
            .addModifiers(Modifier.PRIVATE)
            .build()

    private fun createProvideMethodForDependency(
        constructorElement: ExecutableElement,
        dependencyType: TypeElement
    ): MethodSpec {
        val constructorParameters = constructorElement.parameters.map { "${it.simpleName}.get()" }.joinToString(", ")
        val functionElement = MethodSpec.methodBuilder("provide${dependencyType.simpleName}")
            .addAnnotation(Provides::class.java)
            .returns(TypeName.get(dependencyType.asType()))
            .addModifiers(Modifier.PUBLIC)
            .addCode(
                CodeBlock.of(
                    "if (${dependencyType.cachedPropertyName()} == null) {\n" +
                            "    ${dependencyType.cachedPropertyName()} = new ${"$"}T ($constructorParameters);\n" +
                            "}\n" +
                            "return ${dependencyType.cachedPropertyName()};",
                    dependencyType
                )
            )

        return constructorElement.parameters.fold(functionElement) { methodSpecBuilder, variableElement ->
            val className = TypeName.get(variableElement.asType())
            val providerTypeSpec = ParameterizedTypeName.get(ClassName.get(Provider::class.java), className)

            methodSpecBuilder.addParameter(
                ParameterSpec.builder(providerTypeSpec, variableElement.simpleName.toString())
                    .addAnnotations(variableElement.annotationMirrors.map { AnnotationSpec.get(it) })
                    .build()
            )
        }.build()
    }


    private fun createProvideMethodForReloadModuleUseCase(typeElement: TypeElement): MethodSpec =
        MethodSpec.methodBuilder("provide${getModuleUseCaseName(typeElement)}")
            .addAnnotation(Provides::class.java)
            .addModifiers(Modifier.PUBLIC)
            .returns(GenerateReloadModuleUseCase.getTypeName(typeElement, elementUtils))
            .addCode("return this;")
            .build()

    private fun createTypeSpec(typeElement: TypeElement, reloadFunctionSpec: MethodSpec, provideMethods: List<MethodSpec>, cacheProperties: List<FieldSpec>) =
        TypeSpec.classBuilder(getModuleUseCaseName(typeElement) + "Impl")
            .addModifiers(Modifier.PUBLIC)
            .addSuperinterface(GenerateReloadModuleUseCase.getTypeName(typeElement, elementUtils))
            .addAnnotation(Module::class.java)
            .addAnnotation(
                AnnotationSpec.builder(InstallIn::class.java)
                    .addMember("value", "\$T.class", SingletonComponent::class.java)
                    .build()
            )
            .addFields(cacheProperties)
            .addMethod(createProvideMethodForReloadModuleUseCase(typeElement))
            .addMethods(provideMethods)
            .addMethod(reloadFunctionSpec)
            .build()

    private fun createReloadFunctionSpec(annotatedElements: List<TypeElement>): MethodSpec {
        val reloadFunctionSpec = MethodSpec.methodBuilder(GenerateReloadModuleUseCase.getReloadMethodName())
            .addAnnotation(Override::class.java)
            .addModifiers(Modifier.PUBLIC)
        return annotatedElements.fold(reloadFunctionSpec) { methodSpecBuilder, typeElement ->
            methodSpecBuilder.addCode("${typeElement.cachedPropertyName()} = null;")
        }.build()
    }

//    private fun createImplementation(interfaceTypeSpec: TypeSpec, typeElement: TypeElement, roundEnvironment: RoundEnvironment): TypeSpec {
//        val annotatedElements = roundEnvironment.getElementsAnnotatedWith(typeElement)
//            .filterIsInstance<ExecutableElement>()
//
//        val reloadFunctionSpec = MethodSpec.methodBuilder("reload")
//            .addAnnotation(Override::class.java)
//            .addModifiers(Modifier.PUBLIC)
//        val moduleImplName = "Reloadable${typeElement.simpleName}Impl"
//        val typeSpecBuilder = TypeSpec.classBuilder(moduleImplName)
//            .addModifiers(Modifier.PUBLIC)
//            .addSuperinterface(interfaceTypeSpec.asTypeName(typeElement))
//            .addAnnotation(Module::class.java)
//            .addAnnotation(
//                AnnotationSpec.builder(InstallIn::class.java)
//                    .addMember("value", "\$T.class", SingletonComponent::class.java)
//                    .build()
//            )
//            .addMethod(
//                MethodSpec.methodBuilder("provideReloadable${typeElement.simpleName}")
//                    .addAnnotation(Provides::class.java)
//                    .addModifiers(Modifier.PUBLIC)
//                    .returns(interfaceTypeSpec.asTypeName(typeElement))
//                    .addCode("return this;")
//                    .build()
//            )
//
//        annotatedElements.forEach { constructorElement ->
//            val annotatedType = constructorElement.enclosingElement as TypeElement
//            val annotatedTypeClassName = ClassName.bestGuess(annotatedType.qualifiedName.toString())
//            val memberElement = FieldSpec.builder(
//                annotatedTypeClassName,
//                "cached${annotatedType.simpleName}"
//            )
//                .initializer("null")
//                .addModifiers(Modifier.PRIVATE)
//                .build()
//            reloadFunctionSpec.addCode("cached${annotatedType.simpleName} = null;")
//            val constructorParameters = constructorElement.parameters.map { "${it.simpleName}.get()" }.joinToString(", ")
//            val functionElement = MethodSpec.methodBuilder("provide${annotatedType.simpleName}")
//                .addAnnotation(Provides::class.java)
//                .returns(annotatedTypeClassName)
//                .addModifiers(Modifier.PUBLIC)
//                .addCode(
//                    CodeBlock.of(
//                        """if (cached${annotatedType.simpleName} == null) {
//                                cached${annotatedType.simpleName} = new ${"$"}T ($constructorParameters);
//                           }
//                           return cached${annotatedType.simpleName};
//                        """.trim(),
//                        annotatedType
//                    )
//                )
//
//            constructorElement.parameters.forEach { variableElement ->
//                val className = TypeName.get(variableElement.asType())
//                val providerTypeSpec = ParameterizedTypeName.get(ClassName.get(Provider::class.java), className)
//                functionElement.addParameter(
//                    ParameterSpec.builder(providerTypeSpec, variableElement.simpleName.toString())
//                        .addAnnotations(variableElement.annotationMirrors.map { AnnotationSpec.get(it) })
//                        .build()
//                )
//            }
//
//            typeSpecBuilder.addField(memberElement)
//            typeSpecBuilder.addMethod(functionElement.build())
//        }
//
//
//        typeSpecBuilder.addMethod(reloadFunctionSpec.build())
//
//        return typeSpecBuilder.build()
//    }
//
//    private fun TypeSpec.asTypeName(typeElement: TypeElement) =
//        ClassName.bestGuess(elementUtils.getPackageOf(typeElement).toString() + "." + name.orEmpty())

    companion object {
        private fun TypeElement.cachedPropertyName() = "cached${simpleName}"
    }
}