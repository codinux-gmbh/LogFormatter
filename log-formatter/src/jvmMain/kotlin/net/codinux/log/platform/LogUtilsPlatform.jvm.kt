package net.codinux.log.platform

import net.codinux.log.classname.ClassNameComponents
import net.codinux.log.classname.ClassNameResolver
import kotlin.reflect.KClass

actual object LogFormatterPlatform {

    actual val supportsPackageNames = true


    actual fun <T : Any> getQualifiedClassName(forClass: KClass<T>): String =
        ClassNameResolver.getQualifiedClassName(forClass, getDeclaringClass = false)

    actual fun <T : Any> getClassInfo(forClass: KClass<T>): PlatformClassInfo {
        val javaClass = forClass.java

        val packageName = javaClass.`package`?.name
        val className = (if (packageName != null) javaClass.name.substringAfter("$packageName.") else javaClass.name)
            .replace('$', '.')

        var declaringClass: Class<*>? = javaClass.declaringClass
        while (declaringClass?.declaringClass != null) {
            declaringClass = declaringClass.declaringClass
        }

        // TODO: or add kotlin-reflect and use forClass.isCompanion as check (+ use above while on enclosing class to get owner class name)
        val isCompanionObject = javaClass.simpleName == "Companion" && javaClass.enclosingClass != null
        // only set if class is nested in another class and javaClass.declaringClass is not a Companion object's owner
        val declaringClassName = if (isCompanionObject == false || declaringClass != javaClass.enclosingClass) declaringClass?.simpleName else null
        val companionOwnerClassName = if (isCompanionObject) className.substringBeforeLast(".Companion") else null

        return PlatformClassInfo(ClassNameComponents(className, packageName, declaringClassName, companionOwnerClassName))
    }

}