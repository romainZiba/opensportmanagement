package com.zcorp.opensportmanagement.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonManagedReference
import javax.persistence.*
import javax.validation.constraints.NotNull

@Entity
@Table(name = "user")
@JsonIgnoreProperties("memberOf")
data class User(@Id @NotNull val username: String,
                val firstName: String,
                val lastName: String,
                var password: String,
                val email: String,
                val phoneNumber: String?) {

    @OneToMany(mappedBy = "user")
    @JsonManagedReference(value = "memberOf")
    val memberOf: MutableSet<TeamMember> = mutableSetOf()

    override fun equals(other: Any?): Boolean {
        if (other != null) {
            return other is User && other.username.equals(username)
        }
        return false
    }

    override fun hashCode(): Int {
        return username.hashCode()
    }

    override fun toString(): String {
        return "User(username='$username')"
    }

    fun toDto(): UserDto {
        return UserDto(firstName, lastName, username, email, phoneNumber, memberOf.map { it.team.toDto() }.toSet())
    }
}

class UserDto(val firstName: String, val lastName: String, val username: String, val email: String, val phoneNumber: String?,
              val teamsDto: Set<TeamDto>)

