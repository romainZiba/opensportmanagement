package com.zcorp.opensportmanagement.rest


import com.zcorp.opensportmanagement.EntityAlreadyExistsException
import com.zcorp.opensportmanagement.EntityNotFoundException
import com.zcorp.opensportmanagement.dto.UserDto
import com.zcorp.opensportmanagement.model.User
import com.zcorp.opensportmanagement.repositories.UserRepository
import org.springframework.data.rest.webmvc.RepositoryRestController
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.ResponseStatus
import javax.validation.Valid


@RepositoryRestController
open class UserController(private val userRepository: UserRepository,
                          private val bCryptPasswordEncoder: BCryptPasswordEncoder) {

    @PostMapping("/users/sign-up")
    open fun createUser(@Valid @RequestBody user: User): ResponseEntity<UserDto> {
        if (userRepository.findByUsername(user.username) == null) {
            user.password = bCryptPasswordEncoder.encode(user.password)
            val userSaved = userRepository.save(user)
            return ResponseEntity(userSaved.toDto(), HttpStatus.CREATED)
        }
        throw EntityAlreadyExistsException("User " + user.username + " already exists")
    }

    /** Handle the error */
    @ExceptionHandler(EntityNotFoundException::class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    fun handleError(e: EntityNotFoundException) = e.message

    @ExceptionHandler(EntityAlreadyExistsException::class)
    @ResponseStatus(HttpStatus.CONFLICT)
    fun handleError(e: EntityAlreadyExistsException) = e.message
}