package com.zcorp.opensportmanagement.model

import com.fasterxml.jackson.annotation.JsonBackReference
import com.zcorp.opensportmanagement.controllers.OpponentController
import com.zcorp.opensportmanagement.controllers.StadiumController
import org.springframework.hateoas.ResourceSupport
import org.springframework.hateoas.mvc.ControllerLinkBuilder
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import javax.persistence.*

@Entity
@Table(
        name = "opponent",
        uniqueConstraints = arrayOf(UniqueConstraint(columnNames = arrayOf("name", "team_id"))))
data class Opponent(@Column(name = "name") val name: String,
                    val phoneNumber: String,
                    val email: String,
                    @ManyToOne @JsonBackReference @JoinColumn(name = "team_id") val team: Team,
                    @Id @GeneratedValue val id: Int = -1) {

    override fun toString(): String {
        return "Opponent(name='$name', phoneNumber='$phoneNumber', email='$email', id=$id)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Opponent

        if (name != other.name) return false
        if (team != other.team) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + team.hashCode()
        return result
    }
}

// Resource with self links
class OpponentResource(val name: String, val phoneNumber: String, val email: String, val teamId: Int) : ResourceSupport() {
    constructor(o: Opponent) : this(o.name, o.phoneNumber, o.email, o.team.id)

    init {
        add(ControllerLinkBuilder.linkTo(ControllerLinkBuilder.methodOn(OpponentController::class.java).getOpponents(teamId, UsernamePasswordAuthenticationToken(null, null))).withSelfRel())
    }
}

class OpponentDto(val name: String, val phoneNumber: String, val email: String)