package com.zcorp.opensportmanagement.rest

class EntityAlreadyExistsException(override var message: String) : Exception()
class UserForbiddenException : Exception("Account not allowed")