package com.zcorp.opensportmanagement.model

import javax.persistence.*

@Entity
@Table(name = "championship")
data class Championship(val name: String,
                        @ManyToOne val season: Season,
                        @Id @GeneratedValue val id: Int = -1)