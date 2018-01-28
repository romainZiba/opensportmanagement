package com.zcorp.opensportmanagement.model

import com.fasterxml.jackson.annotation.JsonBackReference
import com.zcorp.opensportmanagement.controllers.StadiumController
import com.zcorp.opensportmanagement.controllers.TeamMemberController
import org.springframework.hateoas.ResourceSupport
import org.springframework.hateoas.mvc.ControllerLinkBuilder
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import javax.persistence.*
import javax.validation.constraints.NotNull

@Entity
@Table(
        name = "stadium",
        uniqueConstraints = arrayOf(UniqueConstraint(columnNames = arrayOf("name", "team_id"))))
data class Stadium(@Column(name = "name") @NotNull val name: String,
                   val address: String,
                   val city: String,
                   @ManyToOne @JoinColumn(name = "team_id") @JsonBackReference val team: Team,
                   @Id @GeneratedValue val id: Int = -1) {

    override fun toString(): String {
        return "Stadium(name='$name', address='$address', city='$city', id=$id)"
    }

    override fun equals(other: Any?): Boolean {
        if (other != null) {
            return other is Stadium && other.name.equals(name) && other.team.equals(team)
        }
        return false
    }

    override fun hashCode(): Int {
        return id
    }
}

// Resource with self links
class StadiumResource(val name: String, val address: String, val city: String, val teamId: Int) : ResourceSupport() {
    constructor(s: Stadium) : this(s.name, s.address, s.city, s.team.id)

    init {
        add(ControllerLinkBuilder.linkTo(ControllerLinkBuilder.methodOn(StadiumController::class.java).getStadiums(teamId, UsernamePasswordAuthenticationToken(null, null))).withSelfRel())
    }
}