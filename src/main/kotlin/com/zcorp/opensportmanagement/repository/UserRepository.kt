package com.zcorp.opensportmanagement.repository

import com.zcorp.opensportmanagement.model.User
import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository : JpaRepository<User, Int> {
    fun findByUsername(username: String): User?
}