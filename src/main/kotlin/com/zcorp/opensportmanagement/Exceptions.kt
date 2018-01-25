package com.zcorp.opensportmanagement

class EntityNotFoundException(override var message: String) : Exception()
class EntityAlreadyExistsException(override var message: String) : Exception()
class UserForbiddenException(override var message: String) : Exception()