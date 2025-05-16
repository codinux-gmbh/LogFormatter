package net.codinux.log.platform

import kotlin.reflect.KClass

actual object LogFormatterPlatform {

    actual val supportsPackageNames = false


    actual fun <T : Any> getQualifiedClassName(forClass: KClass<T>): String =
        // unwrapping companion objects is not possible on JS. There as class / logger name "Companion" will be used
        // do not use forClass.qualifiedName on JS, it will produce an error
        forClass.simpleName ?: forClass.js.name

    actual fun <T : Any> getClassInfo(forClass: KClass<T>) =
        PlatformClassInfo(classNameWithoutPackageName = forClass.simpleName ?: forClass.js.name)

}