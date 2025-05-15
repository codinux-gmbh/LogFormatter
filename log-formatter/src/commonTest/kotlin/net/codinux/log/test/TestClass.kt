package net.codinux.log.test

import kotlin.jvm.JvmInline

object TestObject

class DeclaringClass {

    companion object {
        const val packageName = "net.codinux.log.test"
    }

    class InnerClass {
        companion object
    }

}

@JvmInline
value class TestInlineClass(val value: String)