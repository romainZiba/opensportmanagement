package com.zcorp.opensportmanagement.model

import com.zcorp.opensportmanagement.dto.TeamDto
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "team")
data class Team(
    val name: String,
    val sport: Sport,
    val genderKind: Gender,
    val ageGroup: AgeGroup,
    var imgUrl: String = "",
    @Id @GeneratedValue val id: Int = -1
) {

    fun toDto(): TeamDto {
        return TeamDto(name, sport, genderKind, ageGroup, imgUrl, id)
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
}