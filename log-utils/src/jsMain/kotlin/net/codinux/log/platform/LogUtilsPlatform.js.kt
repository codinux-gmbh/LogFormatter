package net.codinux.log.platform

import kotlin.reflect.KClass

actual object LogUtilsPlatform {

    actual fun <T : Any> getQualifiedClassName(forClass: KClass<T>): String =
        // unwrapping companion objects is not possible on JS. There as class / logger name "Companion" will be used
        // do not use forClass.qualifiedName on JS, it will produce an error
        forClass.simpleName ?: forClass.js.name

}