package com.zcorp.opensportmanagement.service

class NotFoundException(override val message: String) : Exception()
class MissingParameterException(missingParameter: String) : Exception("Parameter $missingParameter is required")
class UnexpectedParameterException(parameter: String) : Exception("Parameter $parameter must not be supplied")
class BadParameterException(override val message: String) : Exception()
class SubscriptionNotPermittedException(override val message: String) : Exception()
class NotPossibleException(override val message: String) : Exception()