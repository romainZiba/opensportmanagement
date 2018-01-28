package com.zcorp.opensportmanagement.model

import com.fasterxml.jackson.annotation.JsonBackReference
import com.zcorp.opensportmanagement.controllers.TeamMemberController
import org.springframework.hateoas.ResourceSupport
import org.springframework.hateoas.mvc.ControllerLinkBuilder
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import javax.persistence.*

@Entity
@Table(name = "team_member",
        uniqueConstraints = [(UniqueConstraint(columnNames = arrayOf("USER_USERNAME", "TEAM_ID")))])
data class TeamMember(
        @ManyToOne
        @JsonBackReference(value = "memberOf")
        @JoinColumn(name = "USER_USERNAME", nullable = false)
        val user: User,
        @ElementCollection val roles: MutableSet<Role>,
        @ManyToOne @JsonBackReference @JoinColumn(name = "TEAM_ID") val team: Team) {

    @Id
    @GeneratedValue
    val id: Int = -1

    var licenseNumber: String? = null

    override fun toString(): String {
        return "TeamMember(user='$user', roles='$roles')"
    }
}

// Resource with self links
class TeamMemberResource(val username: String, val roles: Set<Role>, val teamId: Int) : ResourceSupport() {
    constructor(t: TeamMember) : this(t.user.username, t.roles, t.team.id)

    init {
        add(ControllerLinkBuilder.linkTo(ControllerLinkBuilder.methodOn(TeamMemberController::class.java).getTeamMembers(teamId, UsernamePasswordAuthenticationToken(null, null))).withSelfRel())
    }
}

enum class Role {
    PLAYER, COACH, ADMIN
}