package net.codinux.log.classname

class QualifiedClassNameParser {

    companion object {
        val Default = QualifiedClassNameParser()
    }


    fun extractClassAndPackageName(qualifiedClassName: String): ClassAndPackageName {
        val segments = qualifiedClassName.split('.')

        val classNameSegments = mutableListOf<String>()
        classNameSegments.add(segments.last())

        val secondLastSegment = if (segments.size > 1) segments[segments.size - 2] else null
        if (segments.last() == "Companion" && secondLastSegment != null && isProbablyClassName(secondLastSegment) &&
            isLocalClassAnonymousClassOrFunction(secondLastSegment) == false) {
            classNameSegments.add(segments[segments.size - 2])
        }


        val className = classNameSegments.reversed().joinToString(".")
        val packageName = qualifiedClassName.substring(0, qualifiedClassName.length - className.length - 1)

        return ClassAndPackageName(className, packageName)
    }


    private fun isProbablyClassName(segment: String): Boolean =
        // If it contains '$' then it's for sure a class name.
        // If it starts with an upper case letter it's only a convention that it's a class name then
        segment.first().isUpperCase() || isLocalClassAnonymousClassOrFunction(segment)

    /**
     * Local classes, anonymous classes and functions are separated by '$' from their enclosing class.
     */
    private fun isLocalClassAnonymousClassOrFunction(segment: String): Boolean =
        segment.contains('$')

}