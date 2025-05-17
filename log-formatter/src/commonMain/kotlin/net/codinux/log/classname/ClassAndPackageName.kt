package net.codinux.log.classname

data class ClassAndPackageName(
    /**
     * Functions, local and anonymous classes parts are separated by `$ in class name.
     * If you like to have a dot separated representation instead, call
     * [classNameWithDotSeparatedAnonymousParts].
     */
    val className: String,
    val packageName: String? = null,

    val category: ClassTypeCategory,

    /**
     * In case of Companion objects, local classes, anonymous classes and functions,
     * the class that encloses it / the parent class / the class one level up in hierarchy.
     */
    val enclosingClassName: String? = null,
) {
    /**
     * Separates functions, local and anonymous classes parts with `.` instead of `$`
     * as [className] does.
     */
    val classNameWithDotSeparatedAnonymousParts by lazy { className.replace('$', '.') }
}