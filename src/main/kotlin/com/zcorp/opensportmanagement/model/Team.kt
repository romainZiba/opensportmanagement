package com.zcorp.opensportmanagement.model

import com.fasterxml.jackson.annotation.JsonManagedReference
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