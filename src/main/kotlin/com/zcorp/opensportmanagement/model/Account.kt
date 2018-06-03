package com.zcorp.opensportmanagement.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.zcorp.opensportmanagement.dto.AccountDto
import javax.persistence.CascadeType
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.OneToMany
import javax.persistence.Table
import javax.validation.constraints.NotNull

@Entity
@Table(name = "account")
@JsonIgnoreProperties("memberOf")
data class Account(
    @Id @NotNull val username: String,
    var firstName: String,
    var lastName: String,
    var password: String,
    @Column(unique = true) var email: String,
    var phoneNumber: String?
) {

    @OneToMany(mappedBy = "account", cascade = [CascadeType.ALL])
    private val memberOf: MutableSet<TeamMember> = mutableSetOf()

    fun addTeamMember(teamMember: TeamMember) {
        this.memberOf.add(teamMember)
        teamMember.account = this
    }

    fun getMemberOf(): Set<TeamMember> {
        return memberOf
    }

    fun toDto(): AccountDto {
        return AccountDto(firstName, lastName, username, email, phoneNumber)
    }
}
