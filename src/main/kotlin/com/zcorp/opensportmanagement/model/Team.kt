package com.zcorp.opensportmanagement.model

import com.fasterxml.jackson.annotation.JsonManagedReference
import com.zcorp.opensportmanagement.controllers.*
import org.springframework.hateoas.ResourceSupport
import org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo
import org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import javax.persistence.*

@Entity
@Table(name = "team")
data class Team(val name: String,
                val sport: Sport,
                val genderKind: Gender,
                val ageGroup: AgeGroup,
                @Id @GeneratedValue val id: Int = -1) {

    @OneToMany(mappedBy = "team", cascade = [(CascadeType.ALL)])
    @JsonManagedReference
    val members: MutableSet<TeamMember> = mutableSetOf()

    @OneToMany(mappedBy = "team")
    @JsonManagedReference
    val stadiums: MutableSet<Stadium> = mutableSetOf()

    @OneToMany(mappedBy = "team")
    @JsonManagedReference
    val seasons: MutableSet<Season> = mutableSetOf()

    @OneToMany(mappedBy = "team")
    @JsonManagedReference
    val events: MutableSet<OtherEvent> = mutableSetOf()

    @OneToMany(mappedBy = "team")
    @JsonManagedReference
    val opponents: MutableSet<Opponent> = mutableSetOf()

    override fun toString(): String {
        return "Team(name='$name', sport=$sport, genderKind=$genderKind, ageGroup=$ageGroup, id=$id)"
    }

    fun toDto(): TeamDto {
        return TeamDto(name, sport, genderKind, ageGroup)
    }
}

class TeamDto(val name: String, val sport: Sport, val genderKind: Gender, val ageGroup: AgeGroup)


// Resource with self links
class TeamResource(val id: Int, val name: String, val sport: Sport, val genderKind: Gender, val ageGroup: AgeGroup) : ResourceSupport() {
    constructor(t: Team) : this(t.id, t.name, t.sport, t.genderKind, t.ageGroup)

    init {
        add(linkTo(methodOn(TeamController::class.java).getTeam(id, UsernamePasswordAuthenticationToken(null, null))).withSelfRel())
        add(linkTo(methodOn(TeamMemberController::class.java).getTeamMembers(id, UsernamePasswordAuthenticationToken(null, null))).withRel("members"))
        add(linkTo(methodOn(StadiumController::class.java).getStadiums(id, UsernamePasswordAuthenticationToken(null, null))).withRel("stadiums"))
        add(linkTo(methodOn(SeasonController::class.java).getSeasons(id, UsernamePasswordAuthenticationToken(null, null))).withRel("seasons"))
        add(linkTo(methodOn(EventController::class.java).getEvents(id, UsernamePasswordAuthenticationToken(null, null))).withRel("events"))
        add(linkTo(methodOn(OpponentController::class.java).getOpponents(id, UsernamePasswordAuthenticationToken(null, null))).withRel("opponents"))
    }
}

enum class Sport {
    BASKETBALL, HANDBALL, FOOTBALL, OTHER
}

enum class Gender {
    MALE, FEMALE, BOTH
}

enum class AgeGroup {
    U5, U6, U7, ADULTS
}

enum class Level {
    COMPETITION, LEISURE
}