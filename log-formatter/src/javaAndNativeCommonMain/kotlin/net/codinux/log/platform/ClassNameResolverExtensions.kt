package net.codinux.log.platform

import net.codinux.log.classname.ClassNameResolver
import kotlin.reflect.KClass

fun ClassNameResolver.getQualifiedClassName(forClass: KClass<*>, getDeclaringClass: Boolean = false): String {
    val components = ClassNameResolver.getClassNameComponents(forClass, forClass.qualifiedName)

    val packageNamePrefix = components.packageNamePrefix

    return if (getDeclaringClass) {
        packageNamePrefix + (components.declaringClassName ?: components.className)
    }
    else {
        packageNamePrefix + components.className
    }
}