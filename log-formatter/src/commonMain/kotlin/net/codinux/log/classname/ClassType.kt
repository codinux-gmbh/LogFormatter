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


    val category: ClassTypeCategory by lazy { when (this) {
        Class, Object -> ClassTypeCategory.TopLevel
        CompanionObject, InnerClass -> ClassTypeCategory.Nested
        LocalClass, AnonymousClass, Function -> ClassTypeCategory.LocalClassAnonymousClassOrFunction
    } }

}