package org.fnives.library.reloadable.module.annotation

/**
 * Annotate your custom Annotation with this to apply the annotation processor.
 *
 * The annotation processor will generate 2 classes for you.
 *
 * First will be an interface Reload<YourAnnotation>Module with only one method, reload.
 * This Reload<YourAnnotation>Module can be injected anywhere where you want to reload the module.
 *
 * Second will be a class Reload<YourAnnotation>ModuleImpl which is the actual Module implementation for Hilt.
 * This provides every class which constructor you annotate with YourAnnotation.
 *
 * Reload in this context means, every instance will be cleared and the next time Hilt accesses it, a new will be created.
 * This newly created instance is reused until the next reload call.
 */
@Target(AnnotationTarget.ANNOTATION_CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class ReloadableModule