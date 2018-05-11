package com.zcorp.opensportmanagement.rest

class NotFoundException(override var message: String) : Exception()
class EntityAlreadyExistsException(override var message: String) : Exception()
class UserForbiddenException : Exception("User not allowed")
class UserAlreadyMemberException : Exception("User already member")
class BadInputException(override var message: String) : Exception()
class ConversationIdMissingException : Exception("Conversation Id missing")