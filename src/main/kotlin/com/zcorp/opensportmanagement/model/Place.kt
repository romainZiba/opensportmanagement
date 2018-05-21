package com.zcorp.opensportmanagement.model

import com.zcorp.opensportmanagement.dto.PlaceDto
import javax.persistence.*
import javax.validation.constraints.NotNull

@Entity
@Table(name = "place", uniqueConstraints = [(UniqueConstraint(columnNames = arrayOf("name", "team_id")))])
data class Place(
    @Column(name = "name") @NotNull val name: String,
    private val address: String,
    private val city: String,
    @ManyToOne @JoinColumn(name = "team_id") val team: Team,
    @Id @GeneratedValue val id: Int = -1
) {

    fun toDto(): PlaceDto {
        return PlaceDto(name, address, city, team.id, id)
    }
}