package com.zcorp.opensportmanagement.model

import com.fasterxml.jackson.annotation.JsonBackReference
import com.fasterxml.jackson.annotation.JsonManagedReference
import javax.persistence.*

@Entity
@Table(name = "championship")
data class Championship(@Column(unique = true) val name: String,
                        @ManyToOne @JsonBackReference val season: Season,
                        @OneToMany(mappedBy = "championship") @JsonManagedReference val matches: MutableSet<Match>,
                        @Id @GeneratedValue val id: Int = -1)

class ChampionshipDto(val name: String)