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
implementation("net.codinux.log:log-formatter:1.7.2")
```

### Maven

```xml
<dependency>
   <groupId>net.codinux.log</groupId>
   <artifactId>log-formatter-jvm</artifactId>
   <version>1.7.2</version>
</dependency>
```


## Configuration

Each service class has a matching `<ServiceClassName>Options` class (e.g. `StackTraceFormatterOptions`). 
Each of these provides a `.Default` object whose values match Logback’s default behavior.

Each service class can be configured in two ways:

  1. **Per instance** – used by default for all method calls (defaults to `.Default`)
  2. **Per method call** – if provided overrides instance options for that specific call


## Stack trace

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


## Message formatter

We provide different implementations of the `LogEventFormatter` interface to format a `LogEvent`, e.g. to output it on the console.

### DefaultLogEventFormatter

The one to take if you don't know which one to take. Provides a sensible default log pattern (explanation see below): 
```
"%-5level %logger [%thread] - %message%n%exception"
```

### FieldsLogEventFormatter

You can specify which fields to log directly in code. E.g. to get the same pattern as above:

```kotlin
val formatter = FieldsLogEventFormatter(listOf(
  LogLevelFormatter(FieldFormat(minWidth = 5, pad = FieldFormat.Padding.End)),
  LiteralFormatter.Whitespace,
  LoggerNameFormatter(),
  LiteralFormatter(" ["),
  ThreadNameFormatter(),
  LiteralFormatter("] "),
  MessageFormatter(),
  LineSeparatorFormatter(),
  ThrowableFormatter()
))
```


### PatternLogEventFormatter

Provides the ability to specify the log output as a string pattern like `"%-5level [%logger] (%thread) %message%n%exception"`.
It follows the same pattern format of Logback and JBoss Logging except that we do not support all of their fields.

Each conversion specifier starts with a percent sign `%` and is followed by optional _format modifiers_, a _conversion word_ and optional parameters between braces. 

The conversion word controls the data field to convert, e.g. logger name, level, date or thread name. 

The format modifiers control field width, padding, and left or right justification.

#### Format modifiers

E.g. `%-10.-20logger` specifies:

- Min field width = `10`
- Padded with white spaces at end (first `-`, default is padding at start) if field value is shorter than `10`
- Max field width = `20`
- Truncated at end (second `-`, default is truncation at start) if field value is longer than `20`

For a detailed explanation see:
https://logback.qos.ch/manual/layouts.html#formatModifiers

#### Conversion words

Implemented conversion words are:

| Conversion word                         | Description                                                                                                                                                                                                                                                                                                                                                                                  |
|-----------------------------------------|----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `%level`, `%le`, `%l`                   | Log level (like `Info`, `Error`, ...).                                                                                                                                                                                                                                                                                                                                                       |
| `%logger`, `%lo`, `%c`                  | Logger (category) name that issued this log event.                                                                                                                                                                                                                                                                                                                                           |
| `%message`, `%msg`, `%m`                | The message of this log event.                                                                                                                                                                                                                                                                                                                                                               |
| `%exception`, `%throwable`, `%ex`, `%e` | The exception message and stack trace, if any.<br/>First option is the number of stack trace lines per Throwable.<br/>Second option is max nested throwables (causes).                                                                                                                                                                                                                       |
| `%rootException`, `%rEx`                | The same as `%exception`, but the root cause first = inverts the order of the nested exceptions, as the root cause is in most cases the most meaningful one. Options are the same as for `%exception`, see above.                                                                                                                                                                            |
| `%date`, `%d`                           | The date time at which the log event occurred.<br/>First option is the format pattern. If not set defaults to `"yyyy-MM-dd HH:mm:ss,SSS"`. Understands most of the ISO format patterns, only available localization is English (e.g. for day name), see [KMP DateTime](https://github.com/dankito/KmpDateTime).<br/>Second option is the time zone. Only supported value for now is `"UTC"`. |
| `%thread`, `%th`, `%t`                  | Name of the thread that generated the event. May not available on all systems.                                                                                                                                                                                                                                                                                                               |
| `$n`                                    | Platform dependent line separator, e.g. `\r\n` on Windows or `\n` on Unix systems.                                                                                                                                                                                                                                                                                                           |


As they follow Logback's and JBoss Logging's implementation, for more information see:
https://logback.qos.ch/manual/layouts.html#conversionWord


## Class name

### Get class name for `KClass`

To get the package name, name of the class, enclosing class (if any) and declaring class, call:

```kotlin
// get package name (not available on JavaScript and WASM), class name and enclosing class name 
// (in case of a Companion class, inner class, local class, anonymous function, lambda, ...)
val classNameComponents = ClassNameResolver.getClassNameComponents(TestClasses.OuterClass.InnerClass::class)
```

Be aware:
- On `native` we can only extract the direct class name with certainty, but not the enclosing and declaring class name, as there we only have the full qualified name (that is a string containing both, the package name and the class name). So we can only make educated guesses which parts belong to the class and with to the package name.
- On `JS/Browser`, `JS/Node` and `WASM` the package is not available / known and only the direct / last class name component is available. For Companion objects e.g. this is `"Companion"`.


## Class components result

| Class type            | Platform | Class name                     | Declaring class name | Companion owner class Name |
|-----------------------|----------|--------------------------------|----------------------|----------------------------|
| Companion             | JVM      | TestClass.Companion            | TestClass            | TestClass                  |
| Companion             | Native   | TestClass.Companion            | null                 | TestClass                  |
| Companion             | JS       | Companion                      | null                 | null                       |
| Inner class           | JVM      | TestClass.InnerClass           | TestClass            | null                       |
| Inner class           | Native   | InnerClass                     | null                 | null                       |
| Inner class           | JS       | InnerClass                     | null                 | null                       |
| Inner class Companion | JVM      | TestClass.InnerClass.Companion | TestClass            | null                       |
| Inner class Companion | Native   | InnerClass.Companion           | null                 | InnerClass                 |
| Inner class Companion | JS       | Companion                      | null                 | null                       |
|                       |          |                                |                      |                            |


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
