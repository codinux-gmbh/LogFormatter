package net.codinux.log.platform

import kotlin.reflect.KClass

actual object LogFormatterPlatform {

    actual val supportsPackageNames = false


    actual fun <T : Any> getClassInfo(forClass: KClass<T>): PlatformClassInfo {

        // unwrapping companion objects is not possible on JS. There as class / logger name "Companion" will be used
        // do not use forClass.qualifiedName on JS, it will produce an error

        val jsName = forClass.js.name
        val simpleName = forClass.simpleName

        val className = if (simpleName == null) { // anonymous class
            jsName
        } else if (jsName.contains('$')) { // anonymous or local classes
            jsName // details about the containing method, ... are separated by '$' from class name then
        } else if (simpleName == "Companion") { // Companion object
            jsName // js.name then provides a numeric suffix with the index of the Companion in the declaring class file
        } else if (jsName == "Function") { // Lambda
            simpleName // simpleName then provides a numeric suffix with the index of the function in the declaring class file
        } else {
            simpleName ?: jsName
        }

        return PlatformClassInfo(classNameWithoutPackageName = className)
    }

}