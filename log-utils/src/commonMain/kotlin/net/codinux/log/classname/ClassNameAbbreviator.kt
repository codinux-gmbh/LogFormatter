package net.codinux.log.classname

open class ClassNameAbbreviator {

    open fun abbreviate(qualifiedClassName: String, maxLength: Int, options: ClassNameAbbreviatorOptions = ClassNameAbbreviatorOptions.Default): String {
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
            return combine(packageParts.map { it.first().toString() }, className)
        }

        return fillPackageSegments(className, packageParts, maxLength, minClassNameWithPackageSegmentsLength, options)
    }

    protected open fun fillPackageSegments(className: String, packageParts: List<String>, maxLength: Int,
        minClassNameWithPackageSegmentsLength: Int, options: ClassNameAbbreviatorOptions): String {
        val remainingLength = maxLength - minClassNameWithPackageSegmentsLength

        return when (options.packageAbbreviation) {
            PackageAbbreviationStrategy.FillSegmentsEqually -> {
                val charsPerSegment = (remainingLength / packageParts.size) + 1 // + 1 because one char per segment has already been included in min length

                combine(packageParts.map { it.take(charsPerSegment) }, className)
            }
            PackageAbbreviationStrategy.FillSegmentsFromStart ->
                fillPackageSegments(className, packageParts, maxLength, packageParts.indices.toList())
            PackageAbbreviationStrategy.FillSegmentsFromEnd ->
                fillPackageSegments(className, packageParts, maxLength, packageParts.indices.reversed().toList())
        }
    }

    protected open fun fillPackageSegments(
        className: String,
        packageParts: List<String>,
        maxLength: Int,
        packageSegmentIndicesInWhichOrderSegmentsShouldBeFilled: List<Int>
    ): String {
        val abbreviatedParts = packageParts.map { it.first().toString() }.toMutableList()

        // expand package segments till maxLength is reached
        for (i in packageSegmentIndicesInWhichOrderSegmentsShouldBeFilled) {
            abbreviatedParts[i] = packageParts[i]

            val candidate = combine(abbreviatedParts, className)
            if (candidate.length > maxLength) {
                // Revert the last expansion since it caused overflow
                abbreviatedParts[i] = packageParts[i].first().toString()
                break
            }
        }

        return combine(abbreviatedParts, className)
    }

    protected open fun combine(packageSegments: List<String>, className: String): String =
        packageSegments.joinToString(".") + "." + className

}