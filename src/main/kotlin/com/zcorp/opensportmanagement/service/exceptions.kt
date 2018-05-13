package com.zcorp.opensportmanagement.service

class UserNotFoundException : Exception()
class MissingParameterException(missingParameter: String): Exception("Parameter $missingParameter is required")
class UnexpectedParameterException(parameter: String): Exception("Parameter $parameter must not be supplied")
