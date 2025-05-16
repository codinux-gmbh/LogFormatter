package net.codinux.log.platform

import kotlin.reflect.KClass

actual object LogFormatterPlatform {

    actual val supportsPackageNames = true


    actual fun <T : Any> getClassInfo(forClass: KClass<T>) =
        PlatformClassInfo(qualifiedClassName = forClass.qualifiedName)

}