package com.zcorp.opensportmanagement.rest

import com.zcorp.opensportmanagement.dto.AccountConfirmationDto
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
@RequestMapping("/accounts")
open class AccountController @Autowired constructor(private val accountService: AccountService) {

    @PostMapping
    open fun createAccount(
        @Valid @RequestBody account: Account,
        authentication: Authentication
    ): ResponseEntity<AccountDto> {
        val loggedUser = accountService.findByUsername(authentication.name) ?: throw UserForbiddenException()
        if (loggedUser.globalAdmin) {
            if (accountService.findByEmail(account.email) == null) {
                val savedUser = accountService.createAccount(account)
                return ResponseEntity(savedUser, HttpStatus.CREATED)
            }
            throw EntityAlreadyExistsException("Account " + account.username + " already exists")
        }
        throw UserForbiddenException()
    }

    @GetMapping("/me")
    open fun whoAmi(authentication: Authentication): ResponseEntity<AccountDto> {
        return ResponseEntity.ok(accountService.findByUsername(authentication.name) ?: throw UserForbiddenException())
    }

    @PutMapping("/confirmation")
    open fun confirmAccount(@RequestBody(required = true) confirmationDto: AccountConfirmationDto): ResponseEntity<AccountDto> {
        val account = accountService.confirmAccount(confirmationDto)
        return ResponseEntity.ok(account)
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