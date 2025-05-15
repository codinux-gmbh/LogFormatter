package net.codinux.log.classname

import net.codinux.log.extensions.substringAfterLastOrNull
import net.codinux.log.extensions.substringBeforeLastOrNull
import net.codinux.log.platform.LogUtilsPlatform
import kotlin.reflect.KClass

object ClassNameResolver {

    fun getClassNameComponents(forClass: KClass<*>, qualifiedName: String? = null): ClassNameComponents {
        println("qualifiedName: ${qualifiedName}, simpleName: ${forClass.simpleName}, toString(): ${forClass.toString()}") // TODO: remove again

        val classToString = forClass.toString()
        val cleanedClassToString = removeAnonymousClassesNumberSuffixes(clean(classToString))

        var packageName: String? = null
        var className: String
        if (LogUtilsPlatform.supportsPackageNames) {
            packageName = cleanedClassToString.substringBeforeLastOrNull('.')
            className = cleanedClassToString.substringAfterLast('.')

            // for Companion objects including name of enclosing class in className
            if ((className == "Companion" || className.startsWith("Companion$")) && packageName != null) {
                val indexOfSecondLastDot = packageName.lastIndexOf('.')
                if (indexOfSecondLastDot >= 0) {
                    packageName = packageName.substring(0, indexOfSecondLastDot)
                    className = cleanedClassToString.substring(indexOfSecondLastDot + 1)
                }
            }
        } else {
            className = qualifiedName ?: cleanedClassToString
        }

        // In Java, a $ in a class name represents nested (inner) or anonymous/local classes
        var declaringClass = if (className.contains('$')) className.substringBefore('$')
                                else if (className.endsWith(".Companion")) className.substring(0, className.length - ".Companion".length)
                                else null
        if (declaringClass?.endsWith(".Companion") == true) {
            declaringClass = declaringClass.substring(0, declaringClass.length - ".Companion".length)
        }

        return ClassNameComponents(className.replace('$', '.'), packageName, declaringClass)
    }

    private fun clean(classToString: String): String {
        var cleaned = classToString

        if (cleaned.startsWith("class ")) { // remove 'class ' from beginning to .toString() return value
            cleaned = cleaned.substring("class ".length)
        }

        if (cleaned.endsWith(" (Kotlin reflection is not available)")) { // on JVM KClass.toString() really ends with ' (Kotlin reflection is not available)'
            cleaned = cleaned.substringBefore(" (Kotlin reflection is not available)")
        }

        return cleaned
    }

    /**
     * Remove anonymous class number suffixes like '$1$2'.
     */
    private fun removeAnonymousClassesNumberSuffixes(name: String): String {
        var cleaned = name

        var stringAfterLastDollarSign = cleaned.substringAfterLastOrNull('$')
        while (stringAfterLastDollarSign != null) {
            if (stringAfterLastDollarSign.toIntOrNull() == null) {
                break
            }

            cleaned = cleaned.substringBeforeLast('$')

            stringAfterLastDollarSign = cleaned.substringAfterLastOrNull('$')
        }

        return cleaned
    }

}