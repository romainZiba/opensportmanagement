package com.zcorp.opensportmanagement.repositories

import com.zcorp.opensportmanagement.model.ApplicationUser
import org.springframework.data.repository.CrudRepository

interface UserRepository : CrudRepository<ApplicationUser, Int> {
    fun findByUserName(userName: String): ApplicationUser?
}