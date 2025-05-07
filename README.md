# Log Utils
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/net.codinux.log/log-utils/badge.svg)](https://maven-badges.herokuapp.com/maven-central/net.codinux.log/log-utils)
[![License](https://img.shields.io/badge/License-Apache_2.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

Utilities common to loggers like [klf](https://github.com/codinux-gmbh/klf) or [LokiLogAppender](https://github.com/codinux-gmbh/LokiLogAppender) like determining and shortening logger name from `KClass` or shortening and formatting stack trace.


## Setup

### Gradle

```
implementation("net.codinux.log:log-utils:0.5.0")
```

### Maven

```xml
<dependency>
   <groupId>net.codinux.log</groupId>
   <artifactId>log-utils-jvm</artifactId>
   <version>0.5.0</version>
</dependency>
```


## Usage

### Get class name for `KClass`

```kotlin
// get package name (not available on JavaScript and WASM), class name and enclosing class name 
// (in case of a Companion class, inner class, local class, anonymous function, lambda, ...)
val classNameComponents = ClassNameResolver.getClassNameComponents(TestClasses.OuterClass.InnerClass::class)
```


### Extract stack trace from Throwable

```kotlin
fun extractStackTrace() {
    val throwable = Throwable("Something went wrong", Throwable("Root cause"))
        .apply { addSuppressed(Throwable("Suppressed exception")) }

    val stackTrace = StackTraceExtractor().extractStackTrace(throwable)

    printStackTrace(stackTrace)
}

private fun printStackTrace(stackTrace: StackTrace) {
    println("Message line: ${stackTrace.messageLine}")

    println("Stack frames:")
    stackTrace.frames.forEach { println(it.line) }

    println("Count skipped common frames: ${stackTrace.countSkippedCommonFrames}")

    stackTrace.suppressed.forEach { suppressed ->
        println("Suppressed:")
        printStackTrace(suppressed)
    }

    if (stackTrace.causedBy != null) {
        println("Caused by:")
        printStackTrace(stackTrace.causedBy!!)
    }
}
```


### Shorten stack trace

```kotlin
val throwable = Throwable("Something went wrong", Throwable("Root cause"))
    .apply { addSuppressed(Throwable("Suppressed exception")) }

val shortener = StackTraceShortener()

// keeps at maximum 3 frames per Throwable making it a quite compact and good readable stack trace
val max3FramesPerThrowable = shortener.shorten(throwable, maxFramesPerThrowable = 3)
```


### Format stack trace

```kotlin
val throwable = Throwable("Something went wrong")

val formatter = StackTraceFormatter()

println(formatter.format(throwable))

// shows how much better readable stack trace then is
println(formatter.format(StackTraceShortener().shorten(throwable, maxFramesPerThrowable = 3)))

// a lot of config options to format stack trace
println(formatter.format(throwable, StackTraceFormatterConfig(
    messageLineIndent = "",
    stackFrameIndent = "    ",
    causedByIndent = "",
    causedByMessagePrefix = "Caused by: ",
    suppressedExceptionIndent = "    ",
    suppressedExceptionMessagePrefix = "Suppressed: ",
    lineSeparator = LineSeparator.System // or Unix or Windows ...
)))
```


## License
```
Copyright 2025 codinux GmbH & Co. KG

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   https://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
