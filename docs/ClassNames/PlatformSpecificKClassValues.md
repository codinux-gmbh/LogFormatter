# Platform specific KClass values


## Companion Object

| Platform | .simpleName | .qualifiedName / .js.name                     | .toString()                                         |
|----------|-------------|-----------------------------------------------|-----------------------------------------------------|
| JVM      | Companion   | net.codinux.log.test.DeclaringClass.Companion | class net.codinux.log.test.DeclaringClass$Companion |
| Native   | Companion   | net.codinux.log.test.DeclaringClass.Companion | class net.codinux.log.test.DeclaringClass.Companion |
| JS       | Companion   | Companion_2                                   | class Companion                                     |
| WASM     | Companion   | -                                             | class Companion                                     |


## Inner class

| Platform | .simpleName | .qualifiedName / .js.name                      | .toString()                                          |
|----------|-------------|------------------------------------------------|------------------------------------------------------|
| JVM      | InnerClass  | net.codinux.log.test.DeclaringClass.InnerClass | class net.codinux.log.test.DeclaringClass$InnerClass |
| Native   | InnerClass  | net.codinux.log.test.DeclaringClass.InnerClass | class net.codinux.log.test.DeclaringClass.InnerClass |
| JS       | InnerClass  | InnerClass                                     | class InnerClass                                     |
| WASM     | InnerClass  | -                                              | class InnerClass                                     |


## Inner class Companion

| Platform | .simpleName | .qualifiedName / .js.name                                | .toString()                                                    |
|----------|-------------|----------------------------------------------------------|----------------------------------------------------------------|
| JVM      | Companion   | net.codinux.log.test.DeclaringClass.InnerClass.Companion | class net.codinux.log.test.DeclaringClass$InnerClass$Companion |
| Native   | Companion   | net.codinux.log.test.DeclaringClass.InnerClass.Companion | class net.codinux.log.test.DeclaringClass.InnerClass.Companion |
| JS       | Companion   | Companion_1                                              | class Companion                                                |
| WASM     | Companion   | -                                                        | class Companion                                                |


## Local class

| Platform | .simpleName | .qualifiedName / .js.name                                          | .toString()      |
|----------|-------------|--------------------------------------------------------------------|------------------|
| JVM      | LocalClass  | null                                                               | class net.codinux.log.classname.ClassNameResolverTest$getClassNameComponents_LocalClass$LocalClass                 |
| Native   | LocalClass  | null                                                               | class net.codinux.log.classname.ClassNameResolverTest$getClassNameComponents_LocalClass$LocalClass                 |
| JS       | LocalClass  | ClassNameResolverTest$getClassNameComponents_LocalClass$LocalClass | class LocalClass |
| WASM     | LocalClass  | -                                                                  | class LocalClass |


## Anonymous class

| Platform | .simpleName                 | .qualifiedName / .js.name                        | .toString()                                            |
|----------|-----------------------------|--------------------------------------------------|--------------------------------------------------------|
| JVM      | TestObject$AnonymousClass$1 | net.codinux.log.test.TestObject$AnonymousClass$1 | class net.codinux.log.test.TestObject$AnonymousClass$1 |
| Native   | null                        | null                                             | class net.codinux.log.test.TestObject$AnonymousClass$1 |
| JS       | undefined                   | TestObject$AnonymousClass$1                      | class undefined                                        |
| WASM     | <no name provided>          | -                                                | class <no name provided>                               |


## Lambda

| Platform | .simpleName              | .qualifiedName / .js.name                | .toString()                                    |
|----------|--------------------------|------------------------------------------|------------------------------------------------|
| JVM      | TestObject$Lambda$1      | net.codinux.log.test.TestObject$Lambda$1 | class net.codinux.log.test.TestObject$Lambda$1 |
| Native   | null                     | null                                     | class net.codinux.log.test.TestObject$Lambda$1 |
| JS       | Function1                | Function                                 | class Function1                                |
| WASM     | TestObject$Lambda$lambda | -                                        | class TestObject$Lambda$lambda                 |
