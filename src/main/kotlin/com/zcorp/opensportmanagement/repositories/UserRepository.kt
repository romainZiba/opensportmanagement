package com.zcorp.opensportmanagement.repositories

import com.zcorp.opensportmanagement.model.User
import org.springframework.data.repository.CrudRepository

interface UserRepository : CrudRepository<User, Int> {
    fun findByUserName(userName: String): User?
}