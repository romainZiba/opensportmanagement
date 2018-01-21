package com.zcorp.opensportmanagement.model

import com.fasterxml.jackson.annotation.JsonBackReference
import com.fasterxml.jackson.annotation.JsonManagedReference
import javax.persistence.*

@Entity
@Table(
        name = "championship",
        uniqueConstraints = arrayOf(UniqueConstraint(columnNames = arrayOf("name", "season_id"))))
data class Championship(@Column(name = "name") val name: String,
                        @ManyToOne @JsonBackReference @JoinColumn(name = "season_id") val season: Season,
                        @OneToMany(mappedBy = "championship") @JsonManagedReference val matches: MutableSet<Match>,
                        @Id @GeneratedValue val id: Int = -1)

class ChampionshipDto(val name: String)