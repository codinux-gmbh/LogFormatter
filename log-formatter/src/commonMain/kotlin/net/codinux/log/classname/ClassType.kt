package net.codinux.log.classname

enum class ClassType {

    Class,

    Object,

    CompanionObject,

    InnerClass,

    LocalClass,

    AnonymousClass,

    Function
    ;


    val isClassOrObject by lazy {  this == Class || this == Object }

    val isLocalClassAnonymousClassOrFunction by lazy {  this == LocalClass || this == AnonymousClass || this == Function }

    // it could also be a top level function / lambda, but then compiler creates a class for it
    val isNestedClass by lazy {  this == InnerClass || this == CompanionObject || isLocalClassAnonymousClassOrFunction }

}