package com.zcorp.opensportmanagement.model

import com.zcorp.opensportmanagement.dto.PlaceDto
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.Table
import javax.persistence.UniqueConstraint
import javax.validation.constraints.NotNull

@Entity
@Table(name = "place", uniqueConstraints = [(UniqueConstraint(columnNames = arrayOf("name", "team_id")))])
data class Place(
    @Column(name = "name") @NotNull val name: String,
    private val address: String,
    private val city: String,
    @Enumerated(EnumType.STRING) private val type: PlaceType,
    @ManyToOne @JoinColumn(name = "team_id") val team: Team,
    @Id @GeneratedValue val id: Int? = null
) {

    enum class PlaceType {
        STADIUM, BAR, OTHER
    }

    fun toDto(): PlaceDto {
        return PlaceDto(name, address, city, type, team.id, id)
    }
}