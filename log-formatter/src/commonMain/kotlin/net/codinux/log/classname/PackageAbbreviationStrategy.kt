package net.codinux.log.classname

/**
 * Strategies for abbreviating the package name if package name length exceeds `maxLength`.
 *
 * E.g. for `org.company.project.module.submodule.feature.service`:
 * - [FillSegmentsEqually]: `org.comp.proj.modu.subm.feat.serv`
 * - [FillSegmentsFromEnd]: `o.c.p.m.submodule.feature.service`
 * - [FillSegmentsFromStart]: `org.company.project.m.s.f.s`
 */
enum class PackageAbbreviationStrategy {

    /**
     * All package segments are filled equally till `maxLength` is reached.
     *
     * E.g. `org.comp.proj.modu.subm.feat.serv`.
     */
    FillSegmentsEqually,

    /**
     * Package segments are filled from right to left.
     *
     * Default behavior of Logback.
     *
     * E.g. `o.c.p.m.submodule.feature.service`.
     */
    FillSegmentsFromEnd,

    /**
     * Package segments are filled from left to right.
     *
     * E.g. `org.company.project.m.s.f.s`.
     */
    FillSegmentsFromStart

}