package com.zcorp.opensportmanagement.resources


import com.zcorp.opensportmanagement.EntityAlreadyExistsException
import com.zcorp.opensportmanagement.EntityNotFoundException
import com.zcorp.opensportmanagement.model.ApplicationUser
import com.zcorp.opensportmanagement.repositories.UserRepository
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.web.bind.annotation.*
import java.security.Principal
import javax.validation.Valid


@RestController
class UserController(private val userRepository: UserRepository,
                     private val bCryptPasswordEncoder: BCryptPasswordEncoder) {

    @GetMapping("/users")
    fun findAll() = userRepository.findAll()

    @GetMapping("/user/")
    fun test(principal: Principal?) = principal?.name ?: "You are not logged in"

    @PostMapping("/users/sign-up")
    fun createTeam(@Valid @RequestBody applicationUser: ApplicationUser): ResponseEntity<ApplicationUser> {
        if (userRepository.findByUserName(applicationUser.userName) == null) {
            applicationUser.password = bCryptPasswordEncoder.encode(applicationUser.password)
            val userSaved = userRepository.save(applicationUser)
            return ResponseEntity(userSaved, HttpStatus.CREATED)
        }
        throw EntityAlreadyExistsException("ApplicationUser " + applicationUser.userName + " already exists")
    }

    /** Handle the error */
    @ExceptionHandler(EntityNotFoundException::class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    fun handleError(e: EntityNotFoundException) = e.message

    @ExceptionHandler(EntityAlreadyExistsException::class)
    @ResponseStatus(HttpStatus.CONFLICT)
    fun handleError(e: EntityAlreadyExistsException) = e.message
}