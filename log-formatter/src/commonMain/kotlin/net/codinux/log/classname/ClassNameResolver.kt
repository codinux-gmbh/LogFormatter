package net.codinux.log.classname

import net.codinux.log.extensions.substringAfterLastOrNull
import net.codinux.log.extensions.substringBeforeLastOrNull
import net.codinux.log.platform.LogFormatterPlatform
import kotlin.reflect.KClass

open class ClassNameResolver {

    companion object {
        val Default = ClassNameResolver()
    }


    open fun getClassNameComponents(forClass: KClass<*>): ClassNameComponents {
        LogFormatterPlatform.getClassComponents(forClass)?.let {
            return it
        }

        val classInfo = LogFormatterPlatform.getClassInfo(forClass)

        return getClassNameComponents(forClass, classInfo)
    }

    protected open fun getClassNameComponents(forClass: KClass<*>, classInfo: ClassInfo): ClassNameComponents {
        var (className, packageName) = if (classInfo.qualifiedClassName != null) {
            extractClassAndPackageNameFromQualifiedClassName(classInfo.qualifiedClassName).let {
                it.className to it.packageName
            }
        } else {
            val simpleName = classInfo.classNameWithoutPackageName ?: forClass.toString()
            removeAnonymousClassesNumberSuffixes(clean(simpleName)) to null
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

        return ClassNameComponents(className, packageName, classInfo.type ?: ClassType.Class, declaringClassName, companionOwnerClassName)
    }

    protected open fun extractClassAndPackageNameFromQualifiedClassName(qualifiedClassName: String): ClassAndPackageName {
        val qualifiedName = removeAnonymousClassesNumberSuffixes(clean(qualifiedClassName))

        var packageName = qualifiedName.substringBeforeLastOrNull('.')
        var className = qualifiedName.substringAfterLast('.')

        // for Companion objects including name of enclosing class in className
        if ((className == "Companion" || className.startsWith("Companion$")) && packageName != null) {
            val indexOfSecondLastDot = packageName.lastIndexOf('.')
            if (indexOfSecondLastDot >= 0) {
                packageName = packageName.substring(0, indexOfSecondLastDot)
                className = qualifiedName.substring(indexOfSecondLastDot + 1)
            }
        }

        return ClassAndPackageName(className, packageName)
    }

    protected open fun clean(classToString: String): String {
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
    open fun removeAnonymousClassesNumberSuffixes(name: String): String {
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