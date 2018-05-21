package com.zcorp.opensportmanagement.model

import com.zcorp.opensportmanagement.dto.TeamMemberDto
import javax.persistence.ElementCollection
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.Table
import javax.persistence.UniqueConstraint

@Entity
@Table(name = "team_member",
        uniqueConstraints = [(UniqueConstraint(columnNames = arrayOf("USER_USERNAME", "TEAM_ID")))])
data class TeamMember(
    @ElementCollection val roles: MutableSet<Role>,
    @ManyToOne @JoinColumn(name = "TEAM_ID") val team: Team,
    var licenseNumber: String = "",
    @Id @GeneratedValue val id: Int = -1
) {

    @ManyToOne
    @JoinColumn(name = "USER_USERNAME", nullable = false)
    lateinit var user: User

    enum class Role {
        PLAYER, COACH, ADMIN
    }

    fun toDto(): TeamMemberDto {
        return TeamMemberDto(user.username, user.firstName, user.lastName, roles, licenseNumber, user.email,
                user.phoneNumber, team.id, id)
    }
}