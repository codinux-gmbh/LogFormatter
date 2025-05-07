package net.codinux.log.platform

import kotlin.reflect.KClass

actual object LogUtilsPlatform {

    actual fun <T : Any> getQualifiedClassName(forClass: KClass<T>): String =
        ClassNameResolver.getQualifiedClassName(forClass, getEnclosingClass = false)

}