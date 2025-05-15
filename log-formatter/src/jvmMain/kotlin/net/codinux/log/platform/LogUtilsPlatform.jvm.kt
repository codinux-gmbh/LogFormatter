package net.codinux.log.platform

import net.codinux.log.classname.ClassNameComponents
import net.codinux.log.classname.ClassNameResolver
import kotlin.reflect.KClass

actual object LogFormatterPlatform {

    actual val supportsPackageNames = true


    actual fun <T : Any> getQualifiedClassName(forClass: KClass<T>): String =
        ClassNameResolver.getQualifiedClassName(forClass, getDeclaringClass = false)

    // TODO: when supporting Java 9 use forClass.java.packageName and forClass.java.enclosingClass?.simpleName
    actual fun <T : Any> getClassNameComponents(forClass: KClass<T>): ClassNameComponents =
        ClassNameResolver.getClassNameComponents(forClass, forClass.qualifiedName)

}