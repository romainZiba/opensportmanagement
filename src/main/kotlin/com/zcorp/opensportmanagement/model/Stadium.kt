package com.zcorp.opensportmanagement.model

import com.zcorp.opensportmanagement.dto.StadiumDto
import javax.persistence.*
import javax.validation.constraints.NotNull

@Entity
@Table(
        name = "stadiumId",
        uniqueConstraints = [(UniqueConstraint(columnNames = arrayOf("name", "team_id")))])
data class Stadium(@Column(name = "name") @NotNull val name: String,
                   val address: String,
                   val city: String,
                   @ManyToOne @JoinColumn(name = "team_id") val team: Team,
                   @Id @GeneratedValue val id: Int = -1) {

    fun toDto(): StadiumDto {
        return StadiumDto(name, address, city)
    }
}