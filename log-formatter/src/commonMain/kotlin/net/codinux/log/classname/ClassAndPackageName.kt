package net.codinux.log.classname

data class ClassAndPackageName(
    val className: String,
    val packageName: String? = null,

    val category: ClassTypeCategory,

    /**
     * In case of Companion objects, local classes, anonymous classes and functions,
     * the class that encloses it / the parent class / the class one level up in hierarchy.
     */
    val enclosingClassName: String? = null,
)