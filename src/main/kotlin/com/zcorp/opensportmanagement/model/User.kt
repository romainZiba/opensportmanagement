package com.zcorp.opensportmanagement.model

import javax.persistence.*

@Entity
@Table(name = "user")
data class User(val firstName: String,
                val lastName: String,
                @Column(unique = true) val userName: String,
                var password: String,
                val email: String,
                val phoneNumber: String?,
                val admin: Boolean?,
                val role: Role?,
                val licenseNumber: Number?,
                @Id @GeneratedValue val id: Int = -1)

enum class Role {
    PLAYER, COACH, PLAYER_COACH
}