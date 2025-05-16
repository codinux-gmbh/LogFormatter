package net.codinux.log.platform

import net.codinux.log.classname.ClassNameComponents
import kotlin.reflect.KClass

actual object LogFormatterPlatform {

    actual val supportsPackageNames = false


    actual fun <T : Any> getClassComponents(forClass: KClass<T>): ClassNameComponents? = null // only senseful on JVM

    actual fun <T : Any> getClassInfo(forClass: KClass<T>): ClassInfo {

        // unwrapping companion objects is not possible on JS. There as class / logger name "Companion" will be used
        // do not use forClass.qualifiedName on JS, it will produce an error

        val simpleName = forClass.simpleName

        val className = if (simpleName == "<no name provided>") { // anonymous class
            "<anonymous class>"
        } else {
            simpleName
        }

        return ClassInfo(classNameWithoutPackageName = className)
    }

}