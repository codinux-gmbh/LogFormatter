package net.codinux.log.classname

class ClassNameAbbreviator {

    fun abbreviate(qualifiedClassName: String, maxLength: Int): String {
        if (qualifiedClassName.length <= maxLength) {
            return qualifiedClassName
        }

        val parts = qualifiedClassName.split('.')

        val className = parts.last()
        val packageParts = parts.dropLast(1)

        // class name alone exceeds already maxLength
        if (className.length >= maxLength) {
            return className // for now we don't shorten class name
        }

        val minClassNameWithPackageSegmentsLength = className.length + packageParts.size * 2
        if (minClassNameWithPackageSegmentsLength >= maxLength) {
            // for now also return min abbreviated package names even though it exceeds maxLength
            return packageParts.joinToString(".") { it.first().toString() } + "." + className
        }

        // fill each package segment equally
        val remainingLength = maxLength - minClassNameWithPackageSegmentsLength

        // fill path segments equally
        val charsPerSegment = (remainingLength / packageParts.size) + 1 // + 1 because one char per segment has already been included in min length

        return packageParts.joinToString(".") { it.take(charsPerSegment) } + "." + className
    }

}