package net.codinux.log.platform

import net.codinux.log.classname.ClassInfo
import net.codinux.log.classname.ClassNameComponents
import net.codinux.log.classname.ClassType
import kotlin.reflect.KClass

actual object LogFormatterPlatform {

    actual val supportsPackageNames = true


    actual fun <T : Any> getClassComponents(forClass: KClass<T>): ClassNameComponents? = null // only senseful on JVM

    actual fun <T : Any> getClassInfo(forClass: KClass<T>) =
        ClassInfo(qualifiedClassName = getQualifiedName(forClass), type = getType(forClass))


    private fun <T : Any> getQualifiedName(forClass: KClass<T>) =
        if (forClass.qualifiedName != null) {
            forClass.qualifiedName
        } else { // for lambdas, anonymous and local classes qualified name is null
            val qualifiedName = forClass.toString()
            if (qualifiedName.startsWith("class ")) {
                qualifiedName.substring("class ".length)
            } else {
                qualifiedName
            }
        }

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