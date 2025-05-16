package net.codinux.log.classname

import net.codinux.kotlin.platform.Platform
import net.codinux.kotlin.platform.PlatformType

open class QualifiedClassNameParser {

    companion object {
        val Default = QualifiedClassNameParser()
    }


    open fun extractClassAndPackageName(qualifiedClassName: String): ClassAndPackageName {
        val segments = qualifiedClassName.split('.')
        val lastSegment = segments.last()
        val secondLastSegment = if (segments.size > 1) segments[segments.size - 2] else null

        var enclosingClassName: String? = null
        var category = ClassTypeCategory.TopLevel

        val classNameSegments = mutableListOf<String>()
        classNameSegments.add(segments.last())

        if (lastSegment == "Companion" && secondLastSegment != null && isProbablyClassName(secondLastSegment) &&
            isLocalClassAnonymousClassOrFunction(secondLastSegment) == false) {
            classNameSegments.add(secondLastSegment)

            enclosingClassName = secondLastSegment
            category = ClassTypeCategory.Nested
        }


        if (isLocalClassAnonymousClassOrFunction(lastSegment)) {
            enclosingClassName = lastSegment.substringBefore('$')
            category = ClassTypeCategory.LocalClassAnonymousClassOrFunction
        } else {
            detectCategoryForJS(qualifiedClassName)?.let {
                category = it
            }
        }


        val className = classNameSegments.reversed().joinToString(".")
        val packageName = qualifiedClassName.substring(0, qualifiedClassName.length - className.length - 1)

        return ClassAndPackageName(className.replace('$', '.'), packageName, category, enclosingClassName)
    }


    protected open fun isProbablyClassName(segment: String): Boolean =
        // If it contains '$' then it's for sure a class name.
        // If it starts with an upper case letter it's only a convention that it's a class name then
        segment.first().isUpperCase() || isLocalClassAnonymousClassOrFunction(segment)

    /**
     * Local classes, anonymous classes and functions are separated by '$' from their enclosing class.
     */
    protected open fun isLocalClassAnonymousClassOrFunction(segment: String): Boolean =
        segment.contains('$')


    protected open fun detectCategoryForJS(lastSegment: String): ClassTypeCategory? =
        if (Platform.type in listOf(PlatformType.JsBrowser, PlatformType.JsNodeJs)) {
            if (lastSegment.startsWith("Companion_") && lastSegment.substringAfter("Companion_").toIntOrNull() != null) {
                ClassTypeCategory.Nested
            } else if (lastSegment.startsWith("Function") && lastSegment.substringAfter("Function").toIntOrNull() != null) {
                ClassTypeCategory.LocalClassAnonymousClassOrFunction
            } else {
                null
            }
        } else if (Platform.type == PlatformType.WasmJs) {
            if (lastSegment == "Companion") {
                ClassTypeCategory.Nested
            } else if (lastSegment == "<anonymous class>") {
                ClassTypeCategory.LocalClassAnonymousClassOrFunction
            } else {
                null
            }
        } else {
            null
        }

}