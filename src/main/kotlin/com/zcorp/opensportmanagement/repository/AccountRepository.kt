package com.zcorp.opensportmanagement.repository

import com.zcorp.opensportmanagement.model.Account
import org.springframework.data.jpa.repository.JpaRepository

interface AccountRepository : JpaRepository<Account, Int> {
    fun findByUsername(username: String): Account?
    fun findByEmail(email: String): Account?
    fun findByConfirmationId(confirmationId: String): Account?
}