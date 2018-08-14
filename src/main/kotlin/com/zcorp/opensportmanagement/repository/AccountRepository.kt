package com.zcorp.opensportmanagement.repository

import com.zcorp.opensportmanagement.model.Account
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.rest.core.annotation.RepositoryRestResource

@RepositoryRestResource(exported = false)
interface AccountRepository : JpaRepository<Account, Int> {
    fun findByUsername(username: String): Account?
    fun findByEmailIgnoreCase(email: String): Account?
    fun findByConfirmationId(confirmationId: String): Account?
}