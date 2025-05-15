package net.codinux.log.classname

data class ClassNameComponents(
    val className: String,
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
