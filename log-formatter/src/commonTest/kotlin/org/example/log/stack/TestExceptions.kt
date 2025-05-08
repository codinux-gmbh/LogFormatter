package org.example.log.stack

class RootCauseException(message: String): Throwable(message)

class ParentException(message: String, causedBy: Throwable): Throwable(message, causedBy)

class SuppressedException(message: String): Throwable(message)