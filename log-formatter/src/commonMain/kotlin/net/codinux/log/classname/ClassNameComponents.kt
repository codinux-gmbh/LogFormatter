package net.codinux.log.classname

data class ClassNameComponents(
    val className: String,

    /**
     * The package in which this class is declared in.
     *
     * Not available in JavaScript and WASM and only a guess in Native.
     */
    val packageName: String? = null,

    /**
     * For inner, local, anonymous classes and lambdas: The outmost top level class
     * that contains this class (if this information is available).
     */
    val declaringClass: String? = null

) {
    val packageNamePrefix: String = packageName?.let { "$it." } ?: ""

    override fun toString() = "$packageNamePrefix$className"
}
