package net.codinux.log.platform

import net.codinux.log.classname.ClassInfo
import net.codinux.log.classname.ClassNameComponents
import net.codinux.log.classname.ClassNameResolver
import net.codinux.log.classname.ClassType
import kotlin.reflect.KClass

actual object LogFormatterPlatform {

    private val classNameResolver = ClassNameResolver.Default


    actual fun <T : Any> getClassComponents(forClass: KClass<T>): ClassNameComponents? {
        val javaClass = forClass.java

        val packageName = javaClass.`package`?.name
        var className = if (packageName != null) javaClass.name.substringAfter("$packageName.") else javaClass.name
        className = classNameResolver.removeAnonymousClassesNumberSuffixes(className)
        className = className.replace('$', '.')

        var declaringClass: Class<*>? = getDeclaringClass(javaClass)
        while (declaringClass != null && getDeclaringClass(declaringClass) != null) {
            declaringClass = getDeclaringClass(declaringClass)
        }

        val isCompanionObject = isCompanionObject(javaClass)
        val declaringClassName = declaringClass?.simpleName
        val companionOwnerClassName = if (isCompanionObject) className.substringBeforeLast(".Companion") else null

        return ClassNameComponents(className, packageName, determineType(forClass, javaClass, isCompanionObject), declaringClassName, companionOwnerClassName)
    }

    actual fun <T : Any> getClassInfo(forClass: KClass<T>) =
        ClassInfo(getQualifiedName(forClass), forClass.simpleName, determineType(forClass, forClass.java, isCompanionObject(forClass.java)))


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

    private fun <T : Any> isCompanionObject(javaClass: Class<T>) =
        // TODO: or add kotlin-reflect and use forClass.isCompanion as check (+ use above while on enclosing class to get owner class name)
        javaClass.simpleName == "Companion" && javaClass.enclosingClass != null

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