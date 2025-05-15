package net.codinux.log.platform

import net.codinux.log.classname.ClassNameComponents
import kotlin.reflect.KClass

expect object LogFormatterPlatform {

    val supportsPackageNames: Boolean


    fun <T : Any> getQualifiedClassName(forClass: KClass<T>): String

    fun <T : Any> getClassNameComponents(forClass: KClass<T>): ClassNameComponents

}