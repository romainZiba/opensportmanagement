package com.zcorp.opensportmanagement.model

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonManagedReference
import javax.persistence.*

@Entity
@Table(name = "team")
data class Team(@Column(unique = true) val name: String,
                val sport: Sport,
                val genderKind: Gender,
                val ageGroup: AgeGroup,
                @OneToMany(mappedBy = "team") @JsonIgnore val stadiums: MutableSet<Stadium>,
                @OneToMany(mappedBy = "team") @JsonIgnore val seasons: MutableSet<Season>,
                @OneToMany(mappedBy = "team") @JsonIgnore val events: MutableSet<OtherEvent>,
                @OneToMany(mappedBy = "team") @JsonIgnore val opponents: MutableSet<Opponent>,
                @ManyToMany val members: MutableSet<User>,
                @Id @GeneratedValue val id: Int = -1) {

    override fun toString(): String {
        return "Team(name='$name', sport=$sport, genderKind=$genderKind, ageGroup=$ageGroup, id=$id)"
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