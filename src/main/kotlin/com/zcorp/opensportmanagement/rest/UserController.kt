package com.zcorp.opensportmanagement.rest


import com.zcorp.opensportmanagement.dto.UserDto
import com.zcorp.opensportmanagement.dto.UserUpdateDto
import com.zcorp.opensportmanagement.model.User
import com.zcorp.opensportmanagement.service.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.rest.webmvc.RepositoryRestController
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*
import javax.validation.Valid


@RepositoryRestController
@RequestMapping("/users")
open class UserController @Autowired constructor(private val userService: UserService) {

    @PostMapping("/sign-up")
    open fun createUser(@Valid @RequestBody user: User): ResponseEntity<UserDto> {
        if (userService.findByUsername(user.username) == null) {
            val user = userService.createUser(user)
            return ResponseEntity(user, HttpStatus.CREATED)
        }
        throw EntityAlreadyExistsException("User " + user.username + " already exists")
    }

    @GetMapping("/me")
    open fun whoAmi(authentication: Authentication): ResponseEntity<UserDto> {
        return ResponseEntity.ok(userService.findByUsername(authentication.name) ?: throw UserForbiddenException())
    }

    @PutMapping("/me")
    open fun updateUserInformation(@RequestBody dto: UserUpdateDto,
                                   authentication: Authentication): ResponseEntity<UserDto> {
        val userDto = userService.updateUserProfile(dto, authentication.name)
        return ResponseEntity.ok(userDto)
    }

    /** Handle the error */
    @ExceptionHandler(NotFoundException::class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    fun handleError(e: NotFoundException) = e.message

    @ExceptionHandler(EntityAlreadyExistsException::class)
    @ResponseStatus(HttpStatus.CONFLICT)
    fun handleError(e: EntityAlreadyExistsException) = e.message
}