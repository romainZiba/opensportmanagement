package com.zcorp.opensportmanagement.rest

import com.zcorp.opensportmanagement.dto.AccountDto
import com.zcorp.opensportmanagement.dto.AccountUpdateDto
import com.zcorp.opensportmanagement.model.Account
import com.zcorp.opensportmanagement.service.AccountService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.rest.webmvc.RepositoryRestController
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import javax.validation.Valid

@RepositoryRestController
@RequestMapping("/users")
open class AccountController @Autowired constructor(private val accountService: AccountService) {

    @PostMapping
    open fun createUser(@Valid @RequestBody account: Account): ResponseEntity<AccountDto> {
        if (accountService.findByUsername(account.username) == null) {
            val savedUser = accountService.createUser(account)
            return ResponseEntity(savedUser, HttpStatus.CREATED)
        }
        throw EntityAlreadyExistsException("Account " + account.username + " already exists")
    }

    @GetMapping("/me")
    open fun whoAmi(authentication: Authentication): ResponseEntity<AccountDto> {
        return ResponseEntity.ok(accountService.findByUsername(authentication.name) ?: throw UserForbiddenException())
    }

    @PutMapping("/me")
    open fun updateUserInformation(
        @RequestBody dto: AccountUpdateDto,
        authentication: Authentication
    ): ResponseEntity<AccountDto> {
        val userDto = accountService.updateUserProfile(dto, authentication.name)
        return ResponseEntity.ok(userDto)
    }
}