package net.codinux.log.classname

import net.codinux.log.extensions.substringAfterLastOrNull
import net.codinux.log.extensions.substringBeforeLastOrNull
import kotlin.reflect.KClass

object ClassNameResolver {

    fun getClassNameComponents(forClass: KClass<*>, qualifiedName: String? = null, supportsPackageName: Boolean = true): ClassNameComponents {
        println("qualifiedName: ${qualifiedName}, simpleName: ${forClass.simpleName}, toString(): ${forClass.toString()}") // TODO: remove again

        val classToString = forClass.toString()
        val cleanedClassToString = removeAnonymousClassesNumberSuffixes(clean(classToString))

        var packageName: String? = null
        var className: String
        if (supportsPackageName) {
            packageName = cleanedClassToString.substringBeforeLastOrNull('.')
            className = cleanedClassToString.substringAfterLast('.')

//            if (qualifiedName != null) {
//                packageName = qualifiedName.substringBeforeLastOrNull('.')
//                className = qualifiedName.substringAfterLast('.')
//                if ((className == "Companion" || className.startsWith("Companion$")) && packageName != null) {
//                    val indexOfSecondLastDot = packageName.lastIndexOf('.')
//                    if (indexOfSecondLastDot >= 0) {
//                        packageName = packageName.substring(0, indexOfSecondLastDot)
//                        className = qualifiedName.substring(indexOfSecondLastDot + 1)
//                    }
//                }
//            } else {
//            }
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

    private fun getClassName(name: String, getEnclosingClass: Boolean): String {
        var cleaned = clean(name)

        // remove anonymous class suffixes like '$1$2'
        var stringAfterLastDollarSign = cleaned.substringAfterLastOrNull('$')
        while (stringAfterLastDollarSign != null) {
            if (stringAfterLastDollarSign.toIntOrNull() == null) {
                break
            }

            cleaned = cleaned.substringBeforeLast('$')

            stringAfterLastDollarSign = cleaned.substringAfterLastOrNull('$')
        }

        return if (getEnclosingClass) {
            if (cleaned.endsWith(".Companion") || cleaned.endsWith("\$Companion")) { // ok, someone could name a class 'Companion', but in this case i have no pity that his/her logger name is wrong then
                cleaned = cleaned.substring(0, cleaned.length - ".Companion".length)
            }

            cleaned.substringBefore('$')
        } else {
            cleaned.replace('$', '.')
        }
    }

    fun removeCompanionAndInnerClassSeparatorFromName(loggerName: String): String {
        // unwrap companion object
        return if (loggerName.endsWith(".Companion")) { // ok, someone could name a class 'Companion', but in this case i have no pity that his/her logger name is wrong then
            loggerName.substring(0, loggerName.length - ".Companion".length)
        } else {
            loggerName
        }
            .replace('$', '.') // os opposed to jvmName qualifiedName for inner classes already replaces '$' with '.'
    }


//    fun getLoggerNameFromMethod(packageAndClassName: String, methodName: String): String {
//        var className = packageAndClassName
//        var method = methodName
//
//        // remove inner class suffixes like "$2$1$2" from "App$2$1$2"
//        while (className.length > 2 && className[className.length - 1].isDigit() && className[className.length - 2] == '$') {
//            className = className.substring(0, className.length - 2)
//        }
//
//        if (method == "invoke" || method == "invokeSuspend") { // log statement has then been called from a coroutine function
//            val indexOfDollarSign = className.lastIndexOfOrNull('$')
//            val indexOfDot = className.lastIndexOfOrNull('.')
//            if (indexOfDollarSign != null && (indexOfDot == null || indexOfDollarSign > indexOfDot)) {
//                method = className.substring(indexOfDollarSign + 1) // -> className is something like <className>$<methodName>
//                className = className.substring(0, indexOfDollarSign)
//            }
//        }
//
//        if (className.endsWith("Kt")) {
//            className = className.substring(0, className.length - "Kt".length)
//        }
//
//        val index = className.lastIndexOfOrNull('.')
//        if (index != null && className.substring(index + 1) == method) {
//            return className // class name equals method name, e.g. in Composables -> leave away redundant method name
//        }
//
//        return className + "." + method
//    }

}