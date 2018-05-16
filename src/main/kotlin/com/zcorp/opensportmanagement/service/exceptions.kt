package com.zcorp.opensportmanagement.service

class NotFoundException(override var message: String) : Exception()
class MissingParameterException(missingParameter: String): Exception("Parameter $missingParameter is required")
class UnexpectedParameterException(parameter: String): Exception("Parameter $parameter must not be supplied")
class PastEventException(eventId: Int): Exception("Event $eventId has already taken place")
