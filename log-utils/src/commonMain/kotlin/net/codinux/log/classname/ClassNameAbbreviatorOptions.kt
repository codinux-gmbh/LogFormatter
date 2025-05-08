package net.codinux.log.classname

data class ClassNameAbbreviatorOptions(
    val packageAbbreviation: PackageAbbreviationStrategy = PackageAbbreviationStrategy.FillSegmentsEqually,
) {
    companion object {
        val Default = ClassNameAbbreviatorOptions()
    }
}