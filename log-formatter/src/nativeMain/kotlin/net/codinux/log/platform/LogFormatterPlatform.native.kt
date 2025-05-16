package net.codinux.log.platform

import net.codinux.log.classname.ClassNameComponents
import kotlin.reflect.KClass

actual object LogFormatterPlatform {

    actual val supportsPackageNames = true


    actual fun <T : Any> getClassComponents(forClass: KClass<T>): ClassNameComponents? = null // only senseful on JVM

    actual fun <T : Any> getClassInfo(forClass: KClass<T>) =
        ClassInfo(qualifiedClassName = forClass.qualifiedName)

}