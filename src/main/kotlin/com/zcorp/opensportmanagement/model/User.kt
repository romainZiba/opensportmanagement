package com.zcorp.opensportmanagement.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.zcorp.opensportmanagement.dto.UserDto
import javax.persistence.CascadeType
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.OneToMany
import javax.persistence.Table
import javax.validation.constraints.NotNull

@Entity
@Table(name = "app_user")
@JsonIgnoreProperties("memberOf")
data class User(
        @Id @NotNull val username: String,
        var firstName: String,
        var lastName: String,
        var password: String,
        @Column(unique = true) var email: String,
        var phoneNumber: String?
) {

    @OneToMany(mappedBy = "user", cascade = [CascadeType.ALL])
    private val memberOf: MutableSet<TeamMember> = mutableSetOf()

    fun addTeamMember(teamMember: TeamMember) {
        this.memberOf.add(teamMember)
        teamMember.user = this
    }

    fun getMemberOf(): Set<TeamMember> {
        return memberOf
    }

    override fun toString(): String {
        return "User(username='$username')"
    }

    fun toDto(): UserDto {
        return UserDto(firstName, lastName, username, email, phoneNumber)
    }
}
