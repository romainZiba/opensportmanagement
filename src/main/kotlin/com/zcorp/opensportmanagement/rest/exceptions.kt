package com.zcorp.opensportmanagement.rest

class EntityAlreadyExistsException(override var message: String) : Exception()
class UserForbiddenException : Exception("User not allowed")
class UserAlreadyMemberException : Exception("User already member")
