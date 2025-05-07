package net.codinux.log.classname

import net.codinux.log.extensions.substringAfterLastOrNull
import net.codinux.log.extensions.substringBeforeLastOrNull
import net.codinux.log.platform.LogUtilsPlatform
import kotlin.reflect.KClass

object ClassNameResolver {

    fun getClassNameComponents(forClass: KClass<*>, qualifiedName: String? = null): ClassNameComponents {
        val classToString = forClass.toString()
        val cleanedClassToString = removeAnonymousClassesNumberSuffixes(clean(classToString))

        var packageName: String? = null
        var className: String
        if (LogUtilsPlatform.supportsPackageNames) {
            packageName = cleanedClassToString.substringBeforeLastOrNull('.')
            className = cleanedClassToString.substringAfterLast('.')
        } else {
            className = qualifiedName ?: cleanedClassToString
        }

        val enclosingClassName = if (className.contains('$')) className.substringBefore('$')
                                else null

        return ClassNameComponents(className.replace('$', '.'), packageName, enclosingClassName)
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