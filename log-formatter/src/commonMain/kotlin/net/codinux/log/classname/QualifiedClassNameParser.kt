package net.codinux.log.classname

import net.codinux.kotlin.platform.Platform
import net.codinux.kotlin.platform.PlatformType
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract
import kotlin.jvm.JvmOverloads

open class QualifiedClassNameParser {

    companion object {
        val Default by lazy { QualifiedClassNameParser() }
    }


    /**
     * Extracts the class name and package name from a full qualified name (FQN), that is a
     * dot separated string in form `<package_name>.<class_name>`, may including anonymous or
     * inner classes separated by `$` from enclosing class.
     *
     * The parameter `guessClassHierarchy` is only relevant when dealing with nested / inner classes,
     * e.g. `OuterClass.InnerClass.NextInnerClass`, and you need to retrieve the full class hierarchy,
     * for example to identify the outermost top-level (declaring) class.
     *
     * The class hierarchy detection is based on heuristics and **not guaranteed to be accurate**.
     * It assumes that:
     * - Segments starting with an uppercase letter are class names
     * - Segments starting with a lowercase letter are package names
     * - All trailing segments with an uppercase start are treated as the class hierarchy
     * - The first segment from the end starting with a lowercase letter marks the package boundary
     *
     * @param qualifiedClassName The full qualified name (FQN) in form `<package_name>.<class_name>`.
     * @param guessClassHierarchy Enables heuristic-based detection of the class hierarchy for nested or inner classes.
     */
    @JvmOverloads
    open fun extractClassAndPackageName(qualifiedClassName: String, guessClassHierarchy: Boolean = false): ClassAndPackageName {
        val segments = qualifiedClassName.split('.')
        val lastSegment = segments.last()
        val secondLastSegment = if (segments.size > 1) segments[segments.size - 2] else null

        var enclosingClassName: String? = null
        var category = ClassTypeCategory.TopLevel

        val reversedClassNameSegments = mutableListOf<String>()
        reversedClassNameSegments.add(segments.last())

        if (guessClassHierarchy == false) {
            if (isCompanionObject(lastSegment, secondLastSegment)) {
                reversedClassNameSegments.add(secondLastSegment)

                category = ClassTypeCategory.Nested
            }
        } else {
            var segmentToCheck = segments.size - 2
            while (segmentToCheck >= 0 && isProbablyClassName(segments[segmentToCheck])) {
                reversedClassNameSegments.add(segments[segmentToCheck])

                category = ClassTypeCategory.Nested

                segmentToCheck--
            }
        }


        if (isLocalClassAnonymousClassOrFunction(lastSegment)) {
            enclosingClassName = lastSegment.substringBefore('$')
            category = ClassTypeCategory.LocalClassAnonymousClassOrFunction
        } else {
            detectCategoryForJS(qualifiedClassName)?.let {
                category = it
            }
        }


        val className = reversedClassNameSegments.reversed().joinToString(".")
        val packageName = qualifiedClassName.substring(0, qualifiedClassName.length - className.length - 1)
        if (reversedClassNameSegments.size > 1) {
            enclosingClassName = (if (enclosingClassName == null) "" else "$enclosingClassName.") +
                    reversedClassNameSegments.drop(1).reversed().joinToString(".")
        }

        return ClassAndPackageName(className, packageName, category, enclosingClassName)
    }


    @OptIn(ExperimentalContracts::class)
    protected fun isCompanionObject(lastSegment: String, secondLastSegment: String?): Boolean {
        contract {
            returns(true) implies (secondLastSegment != null)
        }

        return lastSegment == "Companion" && secondLastSegment != null && isProbablyClassName(secondLastSegment) &&
                isLocalClassAnonymousClassOrFunction(secondLastSegment) == false
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