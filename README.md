# Log Formatter for Kotlin (Multiplatform)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/net.codinux.log/log-formatter/badge.svg)](https://maven-badges.herokuapp.com/maven-central/net.codinux.log/log-formatter)
[![License](https://img.shields.io/badge/License-Apache_2.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)


**Log Formatter** brings the powerful `Logback` log formatting capabilities as a logging framework independent implementation to all Kotlin Multiplatform targets.

Use it anywhere Kotlin runs (JVM, JS, Native) to produce highly customizable, clean, consistent, and compact log output.


## Features

* **Class Name**

  * Shorten class names for compact log output
  * Extract class name, package, enclosing and declaring class from `KClass` or fully qualified name strings

* **Stack Trace**

  * Shorten stack traces: limit frames per throwable, count nested throwables
  * Format stack traces: root cause first, max string length, and many more


## Setup

### Gradle

```
implementation("net.codinux.log:log-formatter:1.0.0")
```

### Maven

```xml
<dependency>
   <groupId>net.codinux.log</groupId>
   <artifactId>log-formatter-jvm</artifactId>
   <version>1.0.0</version>
</dependency>
```


## Configuration

Each service class has a matching `<ServiceClassName>Options` class (e.g. `StackTraceFormatterOptions`). 
Each of these provides a `.Default` object whose values match Logback’s default behavior.

Each service class can be configured in two ways:

  1. **Per instance** – used by default for all method calls (defaults to `.Default`)
  2. **Per method call** – if provided overrides instance options for that specific call


## Stack trace

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
    stackTrace.stackTrace.forEach { println(it.line) }

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
println(formatter.format(throwable, StackTraceFormatterOptions(
    messageLineIndent = "",
    stackFrameIndent = "    ",
    causedByIndent = "",
    causedByMessagePrefix = "Caused by: ",
    suppressedExceptionIndent = "    ",
    suppressedExceptionMessagePrefix = "Suppressed: ",
    lineSeparator = LineSeparator.System // or Unix or Windows ...
)))
```


## Class name

### Get class name for `KClass`

```kotlin
// get package name (not available on JavaScript and WASM), class name and enclosing class name 
// (in case of a Companion class, inner class, local class, anonymous function, lambda, ...)
val classNameComponents = ClassNameResolver.getClassNameComponents(TestClasses.OuterClass.InnerClass::class)
```


## Used by

Used e.g. by [Kotlin logging facade](https://github.com/codinux-gmbh/klf) and [LokiLogAppender](https://github.com/codinux-gmbh/LokiLogAppender) for determining and shortening logger name from `KClass` or shortening and formatting stack trace.


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
