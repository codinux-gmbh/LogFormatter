package net.codinux.log.classname

import net.codinux.log.extensions.substringBeforeLastOrNull

class QualifiedClassNameParser {

    companion object {
        val Default = QualifiedClassNameParser()
    }


    fun extractClassAndPackageName(qualifiedClassName: String): ClassAndPackageName {
        var packageName = qualifiedClassName.substringBeforeLastOrNull('.')
        var className = qualifiedClassName.substringAfterLast('.')

        // for Companion objects including name of enclosing class in className
        if ((className == "Companion" || className.startsWith("Companion$")) && packageName != null) {
            val indexOfSecondLastDot = packageName.lastIndexOf('.')
            if (indexOfSecondLastDot >= 0) {
                packageName = packageName.substring(0, indexOfSecondLastDot)
                className = qualifiedClassName.substring(indexOfSecondLastDot + 1)
            }
        }

        return ClassAndPackageName(className, packageName)
    }

}