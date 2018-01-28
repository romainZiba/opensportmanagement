package com.zcorp.opensportmanagement.model

import com.fasterxml.jackson.annotation.JsonBackReference
import com.fasterxml.jackson.annotation.JsonManagedReference
import com.zcorp.opensportmanagement.controllers.ChampionshipController
import com.zcorp.opensportmanagement.controllers.SeasonController
import org.springframework.hateoas.ResourceSupport
import org.springframework.hateoas.mvc.ControllerLinkBuilder
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import java.time.LocalDate
import javax.persistence.*

@Entity
@Table(
        name = "season",
        uniqueConstraints = arrayOf(UniqueConstraint(columnNames = arrayOf("name", "team_id"))))
data class Season(@Column(name = "name") val name: String,
                  @Convert(converter = LocalDateAttributeConverter::class) val fromDate: LocalDate,
                  @Convert(converter = LocalDateAttributeConverter::class) val toDate: LocalDate,
                  @Enumerated(EnumType.STRING) val status: Status,
                  @OneToMany(mappedBy = "season") @JsonManagedReference val championships: MutableSet<Championship>,
                  @ManyToOne @JsonBackReference @JoinColumn(name = "team_id") val team: Team,
                  @Id @GeneratedValue val id: Int = -1) {

    override fun toString(): String {
        return "Season(name='$name')"
    }

    override fun equals(other: Any?): Boolean {
        if (other != null) {
            return other is Season && other.name.equals(name) && other.team.equals(team)
        }
        return false
    }

    override fun hashCode(): Int {
        return id
    }
}

// Resource with self links
class SeasonResource(val id: Int, val name: String, val fromDate: LocalDate, val toDate: LocalDate, val status: Status,
                     val teamId: Int) : ResourceSupport() {
    constructor(s: Season) : this(s.id, s.name, s.fromDate, s.toDate, s.status, s.team.id)

    init {
        add(ControllerLinkBuilder.linkTo(ControllerLinkBuilder.methodOn(SeasonController::class.java).getSeasons(teamId, UsernamePasswordAuthenticationToken(null, null))).withSelfRel())
        add(ControllerLinkBuilder.linkTo(ControllerLinkBuilder.methodOn(ChampionshipController::class.java).getChampionships(id, UsernamePasswordAuthenticationToken(null, null))).withRel("championships"))
    }
}

class SeasonDto(val name: String, val fromDate: LocalDate, val toDate: LocalDate, val status: Status)

enum class Status {
    CURRENT, PREVIOUS, NEXT
}