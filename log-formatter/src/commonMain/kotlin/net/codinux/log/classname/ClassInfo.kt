package net.codinux.log.classname

data class ClassInfo(
    /**
     * On Native and JVM: the full qualified class name including package name.
     *
     * The challenge here is to detect what is the class name and what is the package name.
     *
     * If qualified name (or simple name) is null there, it's most likely an anonymous class or a lambda.
     */
    val qualifiedClassName: String? = null,

    /**
     * On JS and WASM: The class name without package name.
     */
    val classNameWithoutPackageName: String? = null,

    /**
     * The type of the class.
     *
     * Be aware that it's not fully reliable.
     */
    val type: ClassType? = null
)
