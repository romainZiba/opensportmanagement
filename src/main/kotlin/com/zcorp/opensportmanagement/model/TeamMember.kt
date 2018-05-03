package com.zcorp.opensportmanagement.model

import com.zcorp.opensportmanagement.dto.TeamMemberDto
import javax.persistence.*

@Entity
@Table(name = "team_member",
        uniqueConstraints = [(UniqueConstraint(columnNames = arrayOf("USER_USERNAME", "TEAM_ID")))])
data class TeamMember(
        @ElementCollection val roles: MutableSet<Role>,
        @ManyToOne @JoinColumn(name = "TEAM_ID") val team: Team) {


    @ManyToOne
    @JoinColumn(name = "USER_USERNAME", nullable = false)
    lateinit var user: User

    @Id
    @GeneratedValue
    val id: Int = -1

    var licenseNumber: String? = null

    enum class Role {
        PLAYER, COACH, ADMIN
    }

    fun toDto(): TeamMemberDto {
        return TeamMemberDto(user.username, user.firstName, user.lastName, roles, team.id)
    }
}