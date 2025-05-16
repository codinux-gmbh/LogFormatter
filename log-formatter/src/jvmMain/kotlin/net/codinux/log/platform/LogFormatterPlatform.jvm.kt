package net.codinux.log.platform

import net.codinux.log.classname.ClassInfo
import net.codinux.log.classname.ClassNameComponents
import net.codinux.log.classname.ClassNameResolver
import net.codinux.log.classname.ClassType
import kotlin.reflect.KClass

actual object LogFormatterPlatform {

    actual val supportsPackageNames = true


    actual fun <T : Any> getClassComponents(forClass: KClass<T>): ClassNameComponents? {
        val javaClass = forClass.java

        val packageName = javaClass.`package`?.name
        var className = if (packageName != null) javaClass.name.substringAfter("$packageName.") else javaClass.name
        className = ClassNameResolver.removeAnonymousClassesNumberSuffixes(className)
        className = className.replace('$', '.')

        var declaringClass: Class<*>? = getDeclaringClass(javaClass)
        while (declaringClass != null && getDeclaringClass(declaringClass) != null) {
            declaringClass = getDeclaringClass(declaringClass)
        }

        // TODO: or add kotlin-reflect and use forClass.isCompanion as check (+ use above while on enclosing class to get owner class name)
        val isCompanionObject = javaClass.simpleName == "Companion" && javaClass.enclosingClass != null
        // only set if class is nested in another class and javaClass.declaringClass is not a Companion object's owner
        val declaringClassName = if (isCompanionObject == false || declaringClass != javaClass.enclosingClass) declaringClass?.simpleName else null
        val companionOwnerClassName = if (isCompanionObject) className.substringBeforeLast(".Companion") else null

        return ClassNameComponents(className, packageName, determineType(forClass, javaClass, isCompanionObject), declaringClassName, companionOwnerClassName)
    }

    actual fun <T : Any> getClassInfo(forClass: KClass<T>) =
        ClassInfo(forClass.qualifiedName ?: forClass.toString(), forClass.simpleName)


    private fun getDeclaringClass(javaClass: Class<*>): Class<*>? {
        javaClass.enclosingMethod?.let { enclosingMethod ->
            return enclosingMethod.declaringClass
        }

        javaClass.enclosingConstructor?.let { enclosingConstructor ->
            return enclosingConstructor.declaringClass
        }

        return javaClass.declaringClass ?: javaClass.enclosingClass
    }

    private fun <T : Any> determineType(forClass: KClass<T>, javaClass: Class<T>, isCompanionObject: Boolean): ClassType =
        if (isCompanionObject) {
            ClassType.CompanionObject
        } else if (javaClass.isLocalClass) {
            ClassType.LocalClass
        } else if (javaClass.isAnnotation) {
            ClassType.AnonymousClass
        } else if (javaClass.declaringClass != null) { // TODO: or add kotlin-reflect and use forClass.isInner
            ClassType.InnerClass
        }
        // only a guess, without certainty // TODO: or add kotlin-reflect and use forClass.isFun
        else if (javaClass.enclosingMethod != null || javaClass.enclosingConstructor != null || javaClass.enclosingClass != null) {
            ClassType.Function
        } else {
            ClassType.Class
        }

}