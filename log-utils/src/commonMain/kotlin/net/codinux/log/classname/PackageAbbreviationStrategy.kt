package net.codinux.log.classname

/**
 * Strategies for abbreviating the package name if package name length exceeds maxLength.
 *
 * E.g. for `org.company.project.module.submodule.feature.service`:
 * - [FillSegmentsEqually]: `org.comp.proj.modu.subm.feat.serv`
 * - [FillSegmentsFromEnd]: `o.c.p.m.submodule.feature.service`
 * - [FillSegmentsFromStart]: `org.company.project.m.s.f.s`
 */
enum class PackageAbbreviationStrategy {

    FillSegmentsEqually,

    FillSegmentsFromEnd,

    FillSegmentsFromStart

}