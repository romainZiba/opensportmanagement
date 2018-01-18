package com.zcorp.opensportmanagement.model

import java.util.stream.Collectors
import javax.persistence.*

@Entity
@Table(name = "team")
data class Team(@Column(unique = true) val name: String,
                val sport: Sport,
                val genderKind: Gender,
                val ageGroup: AgeGroup,
                @ManyToOne val stadiums: Stadium,
                @OneToMany(fetch = FetchType.EAGER) val events: Set<Event>,
                @Id @GeneratedValue val id: Int = -1) {
    fun toDto(): TeamDto = TeamDto(
            id = this.id,
            name = this.name,
            sport = this.sport,
            genderKind = this.genderKind,
            ageGroup = this.ageGroup,
            events = this.events.map { it.toDto() }.toSet()
    )
}

class TeamDto(val id: Int,
              val name: String,
              val sport: Sport,
              val genderKind: Gender,
              val ageGroup: AgeGroup,
              val events: Set<EventDto>)

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