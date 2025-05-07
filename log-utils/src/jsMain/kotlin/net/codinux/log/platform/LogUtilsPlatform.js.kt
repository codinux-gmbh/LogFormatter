package net.codinux.log.platform

import net.codinux.log.classname.ClassNameComponents
import net.codinux.log.classname.ClassNameResolver
import kotlin.reflect.KClass

actual object LogUtilsPlatform {

    actual val supportsPackageNames = false


    actual fun <T : Any> getQualifiedClassName(forClass: KClass<T>): String =
        // unwrapping companion objects is not possible on JS. There as class / logger name "Companion" will be used
        // do not use forClass.qualifiedName on JS, it will produce an error
        forClass.simpleName ?: forClass.js.name

    actual fun <T : Any> getClassNameComponents(forClass: KClass<T>): ClassNameComponents =
        ClassNameResolver.getClassNameComponents(forClass, forClass.js.name)

}