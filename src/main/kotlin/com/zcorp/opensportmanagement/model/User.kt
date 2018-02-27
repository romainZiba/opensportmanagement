package com.zcorp.opensportmanagement.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonManagedReference
import com.zcorp.opensportmanagement.dto.UserDto
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.OneToMany
import javax.persistence.Table
import javax.validation.constraints.NotNull

@Entity
@Table(name = "app_user")
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

    override fun toString(): String {
        return "User(username='$username')"
    }

    fun toDto(): UserDto {
        return UserDto(firstName, lastName, username, email, phoneNumber, memberOf.map { it.team.toDto() }.toSet())
    }
}

