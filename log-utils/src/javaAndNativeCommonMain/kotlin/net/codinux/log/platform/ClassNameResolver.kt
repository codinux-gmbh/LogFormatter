package net.codinux.log.platform

import net.codinux.log.extensions.substringAfterLastOrNull
import kotlin.reflect.KClass

object ClassNameResolver {

    fun getQualifiedClassName(forClass: KClass<*>): String {
        forClass.qualifiedName?.let { qualifiedName ->
            return getClassName(qualifiedName)
        }

        return getClassName(forClass.toString())
    }

    private fun getClassName(name: String): String {
        var cleaned = name

        if (cleaned.startsWith("class ")) { // remove 'class ' from beginning to .toString() return value
            cleaned = cleaned.substring("class ".length)
        }

        if (cleaned.endsWith(" (Kotlin reflection is not available)")) { // on JVM KClass.toString() really ends with ' (Kotlin reflection is not available)'
            cleaned = cleaned.substringBefore(" (Kotlin reflection is not available)")
        }

        // remove anonymous class suffixes like '$1$2'
        var stringAfterLastDollarSign = cleaned.substringAfterLastOrNull('$')
        while (stringAfterLastDollarSign != null) {
            if (stringAfterLastDollarSign.toIntOrNull() == null) {
                break
            }

            cleaned = cleaned.substringBeforeLast('$')

            stringAfterLastDollarSign = cleaned.substringAfterLastOrNull('$')
        }

        if (cleaned.endsWith(".Companion")) { // ok, someone could name a class 'Companion', but in this case i have no pity that his/her logger name is wrong then
            cleaned = cleaned.substring(0, cleaned.length - ".Companion".length)
        }

        return cleaned
            .replace('$', '.')
    }

}