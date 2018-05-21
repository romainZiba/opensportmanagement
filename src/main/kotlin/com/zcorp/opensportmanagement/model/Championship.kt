package com.zcorp.opensportmanagement.model

import com.zcorp.opensportmanagement.dto.ChampionshipDto
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.Table
import javax.persistence.UniqueConstraint

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