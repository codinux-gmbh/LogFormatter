package net.codinux.log.extensions


fun String.substringBeforeLastOrNull(delimiter: Char): String? =
    if (this.contains(delimiter)) this.substringBeforeLast(delimiter)
    else null

fun String.substringAfterLastOrNull(delimiter: Char): String? =
    if (this.contains(delimiter)) this.substringAfterLast(delimiter)
    else null