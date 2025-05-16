package net.codinux.log.platform

import net.codinux.log.classname.ClassInfo
import net.codinux.log.classname.ClassNameComponents
import net.codinux.log.classname.ClassType
import kotlin.reflect.KClass

actual object LogFormatterPlatform {

    actual val supportsPackageNames = true


    actual fun <T : Any> getClassComponents(forClass: KClass<T>): ClassNameComponents? = null // only senseful on JVM

    actual fun <T : Any> getClassInfo(forClass: KClass<T>) =
        ClassInfo(qualifiedClassName = forClass.qualifiedName, type = getType(forClass))


    /**
     * On native we cannot detect objects and (reliably) inner classes, and we cannot
     * differentiate between anonymous classes and lambdas.
     */
    private fun <T : Any> getType(forClass: KClass<T>): ClassType {
        val simpleName = forClass.simpleName
        val qualifiedName = forClass.qualifiedName

        return if (simpleName == "Companion") {
            ClassType.CompanionObject
        } else if (simpleName == null && qualifiedName == null) { // then it's either an anonymous class or a lambda
            ClassType.AnonymousClass // it's not possible to differentiate between an anonymous class and a lambda
        } else if (qualifiedName == null) {
            ClassType.LocalClass
        } else {
            ClassType.Class
        }
    }

}