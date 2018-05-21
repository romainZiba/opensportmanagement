package com.zcorp.opensportmanagement.model

import com.zcorp.opensportmanagement.dto.ChampionshipDto
import javax.persistence.*

@Entity
@Table(
        name = "championship",
        uniqueConstraints = [(UniqueConstraint(columnNames = arrayOf("name", "season_id")))])
data class Championship(
    @Column(name = "name") val name: String,
    @ManyToOne @JoinColumn(name = "season_id") val season: Season,
    @Id @GeneratedValue val id: Int = -1
) {
    fun toDto(): ChampionshipDto {
        return ChampionshipDto(name, season.team.id, id)
    }
}