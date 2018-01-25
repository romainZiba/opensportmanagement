package com.zcorp.opensportmanagement.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonManagedReference
import javax.persistence.*

@Entity
@Table(name = "user")
@JsonIgnoreProperties("memberOf")
data class User(val firstName: String,
                val lastName: String,
                @Column(unique = true) val username: String,
                var password: String,
                val email: String,
                val phoneNumber: String?,
                @Id @GeneratedValue val id: Int = -1) {

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
        return id
    }

    override fun toString(): String {
        return "User(username='$username')"
    }

    fun toDto(): UserDto {
        return UserDto(firstName, lastName, username, email, phoneNumber, memberOf.map { it.team.toDto() }.toSet())
    }
}

class UserDto(firstName: String, lastName: String, username: String, email: String, phoneNumber: String?,
              teamsDto: Set<TeamDto>)

