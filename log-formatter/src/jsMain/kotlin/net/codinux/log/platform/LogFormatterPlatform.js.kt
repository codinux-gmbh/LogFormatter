package net.codinux.log.platform

import net.codinux.log.classname.ClassInfo
import net.codinux.log.classname.ClassNameComponents
import net.codinux.log.classname.ClassType
import kotlin.reflect.KClass

actual object LogFormatterPlatform {

    actual fun <T : Any> getClassComponents(forClass: KClass<T>): ClassNameComponents? = null // only senseful on JVM

    actual fun <T : Any> getClassInfo(forClass: KClass<T>): ClassInfo {

        // unwrapping companion objects is not possible on JS. There as class / logger name "Companion" will be used
        // do not use forClass.qualifiedName on JS, it will produce an error

        val jsName = forClass.js.name
        val simpleName = forClass.simpleName

        // on JS we cannot detect objects and inner classes
        val (type, className) = if (simpleName == null) { // anonymous class
            ClassType.AnonymousClass to jsName
        } else if (jsName.contains('$')) { // anonymous or local classes
            ClassType.LocalClass to jsName // details about the containing method, ... are separated by '$' from class name then
        } else if (simpleName == "Companion") { // Companion object
            ClassType.CompanionObject to jsName // js.name then provides a numeric suffix with the index of the Companion in the declaring class file
        } else if (jsName == "Function") { // Lambda
            ClassType.Function to simpleName // simpleName then provides a numeric suffix with the index of the function in the declaring class file
        } else {
            ClassType.Class to (simpleName ?: jsName)
        }

        return ClassInfo(classNameWithoutPackageName = className, type = type)
    }

}