package net.codinux.log.test

import net.codinux.kotlin.platform.Platform
import net.codinux.kotlin.platform.PlatformType
import net.codinux.kotlin.platform.isJavaScript
import net.codinux.kotlin.platform.isJvmOrAndroid

object TestPlatform {

    val SupportsDetailedClassName = Platform.isJavaScript == false

    val SupportsDeterminingDeclaringClassName = Platform.isJvmOrAndroid

    val SupportsPackageNames = Platform.isJavaScript == false

}


val Platform.isJsBrowserOrNodeJs
    get() = type == PlatformType.JsBrowser || type == PlatformType.JsNodeJs