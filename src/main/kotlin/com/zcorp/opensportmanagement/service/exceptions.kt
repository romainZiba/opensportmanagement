package com.zcorp.opensportmanagement.service

class NotFoundException(override var message: String) : Exception()
class MissingParameterException(missingParameter: String): Exception("Parameter $missingParameter is required")
class UnexpectedParameterException(parameter: String): Exception("Parameter $parameter must not be supplied")
class PastEventException: Exception {
    constructor() : super("Event date must be greater than current date")
    constructor(eventId: Int): super("Event $eventId has already taken place")
}
