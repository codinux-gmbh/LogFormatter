package net.codinux.log.test

import kotlin.jvm.JvmInline

object TestClasses {

    class OuterClass {

        companion object { }

        class InnerClass {
            companion object { }
        }
    }

    @JvmInline
    value class InlineClass(val value: String)

}