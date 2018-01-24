package com.zcorp.opensportmanagement.model

import javax.persistence.*

@Entity
@Table(name = "user")
data class User(val firstName: String,
                val lastName: String,
                @Column(unique = true) val username: String,
                var password: String,
                val email: String,
                val phoneNumber: String?,
                val admin: Boolean?,
                val role: Role,
                val licenseNumber: Number?,
                @Id @GeneratedValue val id: Int = -1) {

//    @ManyToMany
//    @JsonBackReference
//    val teams: MutableSet<Team> = mutableSetOf()

    override fun equals(other: Any?): Boolean {
        if (other != null) {
            return other is User && other.username.equals(username)
        }
        return false
    }

    override fun hashCode(): Int {
        return id
    }

    override fun toString(): String {
        return "User(username='$username')"
    }

    fun toDto(): UserDto {
        return UserDto(firstName, lastName, username, email, phoneNumber, admin, licenseNumber)
    }
}

class UserDto(firstName: String, lastName: String, username: String, email: String, phoneNumber: String?, admin: Boolean?, licenseNumber: Number?)

enum class Role {
    PLAYER, COACH, PLAYER_COACH
}