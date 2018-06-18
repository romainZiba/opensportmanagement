package com.zcorp.opensportmanagement.rest

import com.zcorp.opensportmanagement.service.BadParameterException
import com.zcorp.opensportmanagement.service.MissingParameterException
import com.zcorp.opensportmanagement.service.NotFoundException
import com.zcorp.opensportmanagement.service.NotPossibleException
import com.zcorp.opensportmanagement.service.SubscriptionNotPermittedException
import com.zcorp.opensportmanagement.service.UnexpectedParameterException
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler

@ControllerAdvice
@RestController
open class OpenExceptionHandler : ResponseEntityExceptionHandler() {
    @ExceptionHandler(NotFoundException::class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    fun handleError(e: NotFoundException) = e.message

    @ExceptionHandler(EntityAlreadyExistsException::class)
    @ResponseStatus(HttpStatus.CONFLICT)
    fun handleError(e: EntityAlreadyExistsException) = e.message

    @ExceptionHandler(UserForbiddenException::class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    fun handleError(e: UserForbiddenException) = e.message

    @ExceptionHandler(MissingParameterException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleError(e: MissingParameterException) = e.message

    @ExceptionHandler(UnexpectedParameterException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleError(e: UnexpectedParameterException) = e.message

    @ExceptionHandler(SubscriptionNotPermittedException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleError(e: SubscriptionNotPermittedException) = e.message

    @ExceptionHandler(BadParameterException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleError(e: BadParameterException) = e.message

    @ExceptionHandler(NotPossibleException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleError(e: NotPossibleException) = e.message
}