package com.zcorp.opensportmanagement.service

import assertk.assert
import assertk.assertAll
import assertk.assertions.containsExactly
import assertk.assertions.hasSize
import assertk.assertions.isEmpty
import assertk.assertions.isEqualTo
import assertk.assertions.isNotEmpty
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import com.zcorp.opensportmanagement.model.Place
import com.zcorp.opensportmanagement.model.Team
import com.zcorp.opensportmanagement.repositories.OpponentRepository
import com.zcorp.opensportmanagement.repositories.SeasonRepository
import com.zcorp.opensportmanagement.repositories.TeamMemberRepository
import com.zcorp.opensportmanagement.repositories.TeamRepository
import com.zcorp.opensportmanagement.repositories.UserRepository
import org.junit.Test

class TeamServiceTest {
    private val teamRepoMock: TeamRepository = mock()
    private val userRepoMock: UserRepository = mock()
    private val seasonRepoMock: SeasonRepository = mock()
    private val opponentRepoMock: OpponentRepository = mock()
    private val teamMemberRepoMock: TeamMemberRepository = mock()
    private val teamService: TeamService = TeamService(teamRepoMock, teamMemberRepoMock, userRepoMock, seasonRepoMock, opponentRepoMock)
    private val teamIds = listOf(5, 8, 13, 25)
    private val mockTeams = teamIds.map {
        Team("SuperName $it", Team.Sport.BASKETBALL, Team.Gender.BOTH, Team.AgeGroup.ADULTS, "", it)
    }
    private val mockPlaces = listOf(Place("Place", "", "", mockTeams[0], 1))

    @Test
    fun `get teams should return the teams`() {
        whenever(teamRepoMock.findByIds(teamIds)).thenReturn(mockTeams)
        val teams = teamService.getTeams(teamIds)
        assert(teams).containsExactly(
                mockTeams[0].toDto(),
                mockTeams[1].toDto(),
                mockTeams[2].toDto(),
                mockTeams[3].toDto()
        )
    }

    @Test
    fun `places should be empty`() {
        whenever(teamRepoMock.getPlaces(any())).thenReturn(emptyList())
        val places = teamService.getPlaces(teamIds[0])
        assert(places).isEmpty()
    }

    @Test
    fun `places should not be empty`() {
        whenever(teamRepoMock.getPlaces(any())).thenReturn(mockPlaces)
        val places = teamService.getPlaces(teamIds[0])
        assertAll {
            assert(places).isNotEmpty()
            assert(places).hasSize(1)
            assert(places[0]).isEqualTo(mockPlaces[0].toDto())
        }
    }
}