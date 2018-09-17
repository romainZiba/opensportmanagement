package com.zcorp.opensportmanagement.model

import com.zcorp.opensportmanagement.dto.TeamMemberDto
import javax.persistence.ElementCollection
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Column
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.Table
import javax.persistence.UniqueConstraint

@Entity
@Table(name = "team_member",
        uniqueConstraints = [(UniqueConstraint(columnNames = arrayOf("ACCOUNT_USERNAME", "TEAM_ID")))])
data class TeamMember(
    @ElementCollection @Enumerated(EnumType.STRING) val roles: MutableSet<Role>,
    @ManyToOne @JoinColumn(name = "TEAM_ID") val team: Team,
    @ManyToOne @JoinColumn(name = "ACCOUNT_USERNAME", nullable = false) val account: Account,
    var licenceNumber: String = "",
    @Id @GeneratedValue @Column(name = "member_id") val id: Int? = null
) {
    enum class Role {
        PLAYER, COACH, ADMIN
    }

    fun toDto(): TeamMemberDto {
        return TeamMemberDto(account.username, account.firstName, account.lastName, roles, licenceNumber, account.email,
                account.phoneNumber, team.id!!, account.confirmationId, id!!)
    }
}