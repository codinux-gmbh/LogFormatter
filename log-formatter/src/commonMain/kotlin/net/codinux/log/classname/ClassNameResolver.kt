package net.codinux.log.classname

import net.codinux.log.extensions.substringAfterLastOrNull
import net.codinux.log.platform.LogFormatterPlatform
import kotlin.jvm.JvmOverloads
import kotlin.reflect.KClass

open class ClassNameResolver(
    protected open val qualifiedClassNameParser: QualifiedClassNameParser = QualifiedClassNameParser()
) {

    companion object {
        val Default = ClassNameResolver()
    }


    /**
     * Get the class name and may package name, enclosing and top-level class from a [KClass].
     *
     * Only accurate on `JVM`.
     *
     * On `Native` accurate for top-level, local and anonymous classes and functions. If for nested
     * classes class hierarchy should be guessed, set `guessClassHierarchy` to true, but see
     * [QualifiedClassNameParser.extractClassAndPackageName] for details about the parameter.
     *
     * On `JavaScript including WASM` only the direct class name is returned, no package names,
     * enclosing or top-level classes. E.g. for Companion objects only `"Companion"` is returned.
     * On `JS` returns for functions `"Function"`, on `WASM` for anonymous classes `"<anonymous class>"`.
     */
    @JvmOverloads
    open fun getClassNameComponents(forClass: KClass<*>, guessClassHierarchy: Boolean = false): ClassNameComponents {
        LogFormatterPlatform.getClassComponents(forClass)?.let { // only on JVM class components can be determined directly via reflection
            return it
        }

        val classInfo = LogFormatterPlatform.getClassInfo(forClass)

        return getClassNameComponents(forClass, classInfo, guessClassHierarchy)
    }

    protected open fun getClassNameComponents(forClass: KClass<*>, classInfo: ClassInfo, guessClassHierarchy: Boolean = false): ClassNameComponents {
        var (className, packageName) = if (classInfo.qualifiedClassName != null) {
            val classAndPackageName = qualifiedClassNameParser.extractClassAndPackageName(
                removeAnonymousClassesNumberSuffixes(clean(classInfo.qualifiedClassName)), guessClassHierarchy
            )
            classAndPackageName.className to classAndPackageName.packageName
        } else {
            val simpleName = classInfo.classNameWithoutPackageName ?: forClass.toString()
            removeAnonymousClassesNumberSuffixes(clean(simpleName)) to null
        }

        // In Java, a $ in a class name represents nested (inner) or anonymous/local classes
        var declaringClassName = if (className.contains('$')) className.substringBefore('$')
                                else null
        if (declaringClassName?.contains(".") == true) {
            declaringClassName = declaringClassName.substringBefore('.')
        } else if (declaringClassName == null && className.contains('.')) {
            declaringClassName = className.substringBefore('.')
        }

        className = className.replace('$', '.')

        val companionOwnerClassName = if (className.endsWith(".Companion")) className.substring(0, className.length - ".Companion".length)
                                    else null

        return ClassNameComponents(className, packageName, classInfo.type ?: ClassType.Class, declaringClassName, companionOwnerClassName)
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