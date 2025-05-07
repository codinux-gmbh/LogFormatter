package net.codinux.log.classname

data class ClassNameComponents(
    val className: String,
    val packageName: String? = null,
    val enclosingClassName: String? = null
) {
    val packageNamePrefix: String = packageName?.let { "$it." } ?: ""

    override fun toString() = "$packageNamePrefix$className"
}
