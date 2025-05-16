package net.codinux.log.platform

import kotlin.reflect.KClass

expect object LogFormatterPlatform {

    val supportsPackageNames: Boolean


    /**
     * Uses platform specific methods to get details about class name.
     * Especially useful on JVM where we can get detailed and correct class info data via reflection.
     */
    fun <T : Any> getClassInfo(forClass: KClass<T>): PlatformClassInfo

}