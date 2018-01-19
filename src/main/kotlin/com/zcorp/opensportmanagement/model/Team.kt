package com.zcorp.opensportmanagement.model

import com.fasterxml.jackson.annotation.JsonManagedReference
import javax.persistence.*

@Entity
@Table(name = "team")
data class Team(@Column(unique = true) val name: String,
                val sport: Sport,
                val genderKind: Gender,
                val ageGroup: AgeGroup,
                @ManyToOne val stadium: Stadium?,
                @OneToMany(mappedBy = "team") @JsonManagedReference val seasons: MutableSet<Season>,
                @OneToMany(mappedBy = "team") @JsonManagedReference val events: MutableSet<OtherEvent>,
                @Id @GeneratedValue val id: Int = -1) {

    override fun toString(): String {
        return "Team(name='$name', sport=$sport, genderKind=$genderKind, ageGroup=$ageGroup, stadium=$stadium, id=$id)"
    }

    override fun equals(other: Any?): Boolean {
        if (other != null) {
            if (other is Team) {
                return other.id == this.id
            }
        }
        return false
    }

    override fun hashCode(): Int {
        return id
    }

    //    fun addMatch(match: Match, championshipName: String) {
//        seasons.filter { it.status.equals(Status.CURRENT) }.first().championships
//                .filter { it.name.equals(championshipName) }.first().matches.add(match)
//    }
//
//    fun addEvent(event: Event) {
//        events.add(event)
//
//    }
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