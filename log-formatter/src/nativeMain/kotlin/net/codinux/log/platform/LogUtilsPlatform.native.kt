package net.codinux.log.platform

import net.codinux.log.classname.ClassNameComponents
import net.codinux.log.classname.ClassNameResolver
import kotlin.reflect.KClass

actual object LogUtilsPlatform {

    actual val supportsPackageNames = true


    actual fun <T : Any> getQualifiedClassName(forClass: KClass<T>): String =
        ClassNameResolver.getQualifiedClassName(forClass, getEnclosingClass = false)

    actual fun <T : Any> getClassNameComponents(forClass: KClass<T>): ClassNameComponents =
        ClassNameResolver.getClassNameComponents(forClass, forClass.qualifiedName)

}