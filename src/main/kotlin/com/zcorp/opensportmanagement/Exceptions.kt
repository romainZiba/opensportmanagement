package com.zcorp.opensportmanagement

class EntityNotFoundException(override var message: String) : Exception()
class EntityAlreadyExistsException(override var message: String) : Exception()
class UserForbiddenException : Exception("User not allowed")
class UserAlreadyMemberException : Exception("User already member")