package net.codinux.log.platform

import net.codinux.log.classname.ClassInfo
import net.codinux.log.classname.ClassNameComponents
import net.codinux.log.classname.ClassType
import kotlin.reflect.KClass

actual object LogFormatterPlatform {

    actual val supportsPackageNames = false


    actual fun <T : Any> getClassComponents(forClass: KClass<T>): ClassNameComponents? = null // only senseful on JVM

    actual fun <T : Any> getClassInfo(forClass: KClass<T>): ClassInfo {

        // unwrapping companion objects is not possible on JS. There as class / logger name "Companion" will be used
        // do not use forClass.qualifiedName on JS, it will produce an error

        val simpleName = forClass.simpleName

        // on WASM we cannot detect Objects, inner and local classes
        val (type, className) = if (simpleName == "<no name provided>") { // anonymous class
            ClassType.AnonymousClass to "<anonymous class>"
        } else if (simpleName?.contains('$') == true) { // Lambda
            ClassType.Function to simpleName
        } else if (simpleName == "Companion") {
            ClassType.CompanionObject to simpleName
        } else {
            ClassType.Class to simpleName
        }

        return ClassInfo(classNameWithoutPackageName = className, type = type)
    }

}