package net.codinux.log.classname

import net.codinux.log.extensions.substringAfterLastOrNull
import net.codinux.log.extensions.substringBeforeLastOrNull
import net.codinux.log.platform.LogFormatterPlatform
import kotlin.reflect.KClass

object ClassNameResolver {

    fun getClassNameComponents(forClass: KClass<*>): ClassNameComponents {
        val classInfo = LogFormatterPlatform.getClassInfo(forClass)
        if (classInfo.classNameComponents != null) {
            return classInfo.classNameComponents
        }

        return getClassNameComponentsFromString(forClass, classInfo.qualifiedClassName ?: classInfo.classNameWithoutPackageName)
    }

    private fun getClassNameComponentsFromString(forClass: KClass<*>, qualifiedName: String? = null): ClassNameComponents {
        val classToString = forClass.toString()
        val cleanedClassToString = removeAnonymousClassesNumberSuffixes(clean(classToString))

        var packageName: String? = null
        var className: String
        if (LogFormatterPlatform.supportsPackageNames) {
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
            className = qualifiedName?.let { removeAnonymousClassesNumberSuffixes(clean(it)) }
                ?: cleanedClassToString
        }

        if (className.endsWith("\$Companion")) {
            className = className.substringBeforeLast("\$Companion") + ".Companion"
        }

        // In Java, a $ in a class name represents nested (inner) or anonymous/local classes
        var declaringClassName = if (className.contains('$')) className.substringBefore('$')
                                else null
        if (declaringClassName?.endsWith(".Companion") == true) {
            declaringClassName = declaringClassName.substring(0, declaringClassName.length - ".Companion".length)
        }

        className = className.replace('$', '.')

        val companionOwnerClassName = if (className.endsWith(".Companion")) className.substring(0, className.length - ".Companion".length)
                                    else null

        return ClassNameComponents(className, packageName, declaringClassName, companionOwnerClassName)
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
    fun removeAnonymousClassesNumberSuffixes(name: String): String {
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