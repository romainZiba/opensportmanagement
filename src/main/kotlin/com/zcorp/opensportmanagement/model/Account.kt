package com.zcorp.opensportmanagement.model

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "account")
data class Account(val firstName: String,
                   val lastName: String,
                   val email: String,
                   val phoneNumber: String,
                   val admin: Boolean,
                   val role: Role,
                   @Id @GeneratedValue val id: Int = -1)

enum class Role {
    PLAYER, COACH, PLAYER_COACH
}