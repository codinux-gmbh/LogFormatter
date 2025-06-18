package net.codinux.log.classname

import kotlin.jvm.JvmOverloads
import kotlin.math.max

open class ClassNameAbbreviator @JvmOverloads constructor(
    protected val options: ClassNameAbbreviatorOptions = ClassNameAbbreviatorOptions.Default
) {

    companion object {
        val Default by lazy { ClassNameAbbreviator() }
    }


    open fun abbreviate(qualifiedClassName: String, maxLength: Int, options: ClassNameAbbreviatorOptions = this.options): String {
        if (qualifiedClassName.length <= maxLength) {
            return qualifiedClassName
        }

        val parts = qualifiedClassName.split('.')

        val className = parts.last()

        val abbreviatedClassName = if (className.length > maxLength) { // class name alone exceeds already maxLength
            abbreviateClassName(className, maxLength, options)
        } else {
            className
        }

        val packageParts = parts.dropLast(1)

        // class name with only one char per segment exceeds maxLength
        val minPackageNameLength = packageParts.size * 2
        val minClassNameWithPackageSegmentsLength = abbreviatedClassName.length + minPackageNameLength
        if (minClassNameWithPackageSegmentsLength >= maxLength) {
            return if (options.minPackageNameTooLongStrategy == MinPackageNameTooLongStrategy.KeepEvenIfLongerThanMaxLength) {
                combine(firstCharOfEachPackageSegment(packageParts), abbreviatedClassName)
            } else {
                abbreviatedClassName
            }
        }

        return fillPackageSegments(abbreviatedClassName, packageParts, maxLength, options)
    }


    protected open fun abbreviateClassName(className: String, maxLength: Int, options: ClassNameAbbreviatorOptions): String =
        when (options.classNameAbbreviation) {
            ClassNameAbbreviationStrategy.KeepClassNameEvenIfLonger -> className
            ClassNameAbbreviationStrategy.ClipStart -> className.takeLast(maxLength)
            ClassNameAbbreviationStrategy.ClipEnd -> className.take(maxLength)
            ClassNameAbbreviationStrategy.EllipsisStart -> options.classNameAbbreviationEllipsis +
                    className.takeLast(maxLength - options.classNameAbbreviationEllipsis.length)
            ClassNameAbbreviationStrategy.EllipsisMiddle -> {
                val lengthPerPart = (maxLength - options.classNameAbbreviationEllipsis.length) / 2
                val remainingChars = (maxLength - options.classNameAbbreviationEllipsis.length) % 2
                // in case of maxLength - options.classNameAbbreviationEllipsis.length is an odd number, give that additional char to the start part
                className.take(lengthPerPart + remainingChars) + options.classNameAbbreviationEllipsis + className.takeLast(lengthPerPart)
            }
            ClassNameAbbreviationStrategy.EllipsisEnd -> className.take(maxLength - options.classNameAbbreviationEllipsis.length) +
                    options.classNameAbbreviationEllipsis
        }


    protected open fun fillPackageSegments(className: String, packageParts: List<String>, maxLength: Int, options: ClassNameAbbreviatorOptions): String {
        return when (options.packageAbbreviation) {
            PackageAbbreviationStrategy.FillSegmentsEqually ->
                fillPathSegmentsEqually(maxLength, className, packageParts)
            PackageAbbreviationStrategy.FillSegmentsFromStart ->
                fillPackageSegments(className, packageParts, maxLength, packageParts.indices.toList())
            PackageAbbreviationStrategy.FillSegmentsFromEnd ->
                fillPackageSegments(className, packageParts, maxLength, packageParts.indices.reversed().toList())
        }
    }

    protected open fun fillPathSegmentsEqually(maxLength: Int, className: String, packageParts: List<String>): String {
        val remainingLength = maxLength - className.length - packageParts.size
        val charsPerSegment = max(1, (remainingLength / packageParts.size)) // use at least one char per segment

        return combine(packageParts.map { it.take(charsPerSegment) }, className)
    }

    protected open fun fillPackageSegments(
        className: String,
        packageParts: List<String>,
        maxLength: Int,
        packageSegmentIndicesInWhichOrderSegmentsShouldBeFilled: List<Int>
    ): String {
        val abbreviatedParts = firstCharOfEachPackageSegment(packageParts).toMutableList()

        // expand package segments till maxLength is reached
        for (i in packageSegmentIndicesInWhichOrderSegmentsShouldBeFilled) {
            abbreviatedParts[i] = packageParts[i]

            val candidate = combine(abbreviatedParts, className)
            if (candidate.length > maxLength) {
                // the last expansion caused an overflow, truncate it to optimal length
                val countTooManyChars = candidate.length - maxLength
                if (abbreviatedParts[i].length - countTooManyChars <= 0) {
                    abbreviatedParts[i] = packageParts[i].first().toString() // keep at least one character
                } else {
                    abbreviatedParts[i] = abbreviatedParts[i].dropLast(countTooManyChars)
                }

                break
            }
        }

        return combine(abbreviatedParts, className)
    }

    protected open fun firstCharOfEachPackageSegment(packageParts: List<String>): List<String> =
        packageParts.map { it.first().toString() }

    protected open fun combine(packageSegments: List<String>, className: String): String =
        packageSegments.joinToString(".") + "." + className

}