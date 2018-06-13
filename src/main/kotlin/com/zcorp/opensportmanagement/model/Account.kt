package com.zcorp.opensportmanagement.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.zcorp.opensportmanagement.dto.AccountDto
import java.util.UUID
import javax.persistence.CascadeType
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.OneToMany
import javax.persistence.Table

@Entity
@Table(name = "account")
@JsonIgnoreProperties("memberOf")
data class Account(
    @Column(name = "first_name") var firstName: String,
    @Column(name = "last_name") var lastName: String,
    @Column(name = "password") var password: String,
    @Column(name = "email", unique = true) var email: String,
    @Column(name = "phone_number") var phoneNumber: String = "",
    @Column(name = "temporary") var temporary: Boolean = true,
    @Column(name = "global_admin") val globalAdmin: Boolean = false
) {

    @Id
    @Column(name = "username")
    val username: String = UUID.randomUUID().toString()

    @Column(name = "confirmation_id", unique = true)
    val confirmationId: String = UUID.randomUUID().toString()

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
        return AccountDto(firstName, lastName, username, email, phoneNumber, globalAdmin)
    }
}
