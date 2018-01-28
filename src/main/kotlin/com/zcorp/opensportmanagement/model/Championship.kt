package com.zcorp.opensportmanagement.model

import com.fasterxml.jackson.annotation.JsonBackReference
import com.fasterxml.jackson.annotation.JsonManagedReference
import com.zcorp.opensportmanagement.controllers.ChampionshipController
import com.zcorp.opensportmanagement.controllers.MatchController
import org.springframework.hateoas.ResourceSupport
import org.springframework.hateoas.mvc.ControllerLinkBuilder
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import javax.persistence.*

@Entity
@Table(
        name = "championship",
        uniqueConstraints = arrayOf(UniqueConstraint(columnNames = arrayOf("name", "season_id"))))
data class Championship(@Column(name = "name") val name: String,
                        @ManyToOne @JsonBackReference @JoinColumn(name = "season_id") val season: Season,
                        @Id @GeneratedValue val id: Int = -1) {

    @OneToMany(mappedBy = "championship")
    @JsonManagedReference
    val matches: MutableSet<Match> = mutableSetOf()


    override fun toString(): String {
        return "Championship(name='$name', id=$id)"
    }
}

// Resource with self links
class ChampionshipResource(val id: Int, val name: String, seasonId: Int) : ResourceSupport() {
    constructor(c: Championship) : this(c.id, c.name, c.season.id)

    init {
        add(ControllerLinkBuilder.linkTo(ControllerLinkBuilder.methodOn(ChampionshipController::class.java).getChampionships(seasonId, UsernamePasswordAuthenticationToken(null, null))).withSelfRel())
        add(ControllerLinkBuilder.linkTo(ControllerLinkBuilder.methodOn(MatchController::class.java).getMatches(id, UsernamePasswordAuthenticationToken(null, null))).withRel("championships"))
    }
}

class ChampionshipDto(val name: String)