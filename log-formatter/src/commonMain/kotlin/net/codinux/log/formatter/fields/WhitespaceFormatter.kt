package net.codinux.log.formatter.fields

object WhitespaceFormatter : LiteralFormatter(" ") {
    override fun toString() = "Whitespace: '$literal'"
}