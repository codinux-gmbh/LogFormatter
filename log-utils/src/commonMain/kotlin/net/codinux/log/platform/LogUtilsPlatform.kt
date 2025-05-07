package net.codinux.log.platform

import kotlin.reflect.KClass

expect object LogUtilsPlatform {

    fun <T : Any> getQualifiedClassName(forClass: KClass<T>): String

}