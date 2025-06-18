package net.codinux.log.classname

/**
 * Strategies to specify what to do if min package name - that is only the first letter
 * of each segment like `o.c.p.m.s.f.s` for `org.company.project.module.submodule.feature.service` -
 * exceeds `maxLength` parameter.
 */
enum class MinPackageNameTooLongStrategy {

    /**
     * Keep the min package name even if it's longer than `maxLength` (independent of class name length).
     * The result will therefore exceed `maxLength` value.
     */
    KeepEvenIfLongerThanMaxLength,

    /**
     * Keep the min package name only even if `maxLength` is longer than class name length, but
     * omits it if `maxLength` is shorter than class name length.
     * In first case the result will therefore exceed `maxLength` value.
     *
     * This is the `Logback` default behavior.
     */
    KeepOnlyIfMaxLengthLongerThanClassName,

    /**
     * The min package name then gets omitted, only the class name is used.
     *
     * LogFormatter's default behavior.
     */
    Omit,

}