package net.codinux.log.extensions


fun String.substringAfterLastOrNull(delimiter: Char): String? =
    if (this.contains(delimiter)) this.substringAfterLast(delimiter)
    else null