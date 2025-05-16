package net.codinux.log.platform

import net.codinux.log.classname.ClassNameComponents
import kotlin.reflect.KClass

expect object LogFormatterPlatform {

    val supportsPackageNames: Boolean


    fun <T : Any> getQualifiedClassName(forClass: KClass<T>): String

    fun <T : Any> getClassNameComponents(forClass: KClass<T>): ClassNameComponents

    /**
     * Uses platform specific methods to get details about class name.
     * Especially useful on JVM where we can get detailed and correct class info data via reflection.
     */
    fun <T : Any> getClassInfo(forClass: KClass<T>): PlatformClassInfo

}