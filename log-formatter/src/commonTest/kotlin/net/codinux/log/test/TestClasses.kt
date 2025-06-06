package net.codinux.log.test

import kotlin.jvm.JvmInline

object TestObject {
    const val packageName = "net.codinux.log.test"

    val AnonymousClass = object : Throwable() {}

    val Lambda = { x: Int -> x * 2 }
}

class DeclaringClass {

    companion object {
        const val JsName = "Companion_7"
    }

    class InnerClass {
        companion object {
            const val JsName = "Companion_6"
        }

        class InnerClassInInnerClass
    }

}

@JvmInline
value class TestInlineClass(val value: String)