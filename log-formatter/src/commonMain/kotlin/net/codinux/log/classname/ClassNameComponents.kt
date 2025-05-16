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
     * The outmost top level class that contains this (nested) class.
     *
     * Only differs from [className] if it's a inner, local, anonymous class
     * or lambda.
     *
     * Not available on `JS` and `WASM` and cannot be detected reliably on
     * `Native` (we can only make educated guesses that class names start
     * with an upper case letter and package names with a lower case letter).
     */
    val declaringClassName: String? = null,

    /**
     * If class is a companion object, the name of the class that contains
     * this companion.
     *
     * Not available on JavaScript and WASM.
     */
    val companionOwnerClassName: String? = null,
) {
    val packageNamePrefix: String = packageName?.let { "$it." } ?: ""

    override fun toString() = "$packageNamePrefix$className"
}
