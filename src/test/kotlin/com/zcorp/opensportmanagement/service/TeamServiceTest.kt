package com.zcorp.opensportmanagement.service

import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import com.zcorp.opensportmanagement.model.Team
import com.zcorp.opensportmanagement.repositories.*
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test


class TeamServiceTest {
    private lateinit var teamService: TeamService
    private lateinit var teamRepoMock: TeamRepository
    private lateinit var userRepoMock: UserRepository
    private lateinit var seasonRepoMock: SeasonRepository
    private lateinit var opponentRepoMock: OpponentRepository
    private lateinit var teamMemberRepoMock: TeamMemberRepository

    private val teamIds = listOf(5, 8, 13 , 25)
    private lateinit var mockTeams: List<Team>

    @Before
    fun setUp() {
        mockTeams = teamIds.map {
            Team("SuperName $it", Team.Sport.BASKETBALL, Team.Gender.BOTH, Team.AgeGroup.ADULTS, "", it)
        }
        teamRepoMock = mock()
        teamMemberRepoMock = mock()
        userRepoMock = mock()
        seasonRepoMock = mock()
        opponentRepoMock = mock()
        teamService = TeamService(teamRepoMock, teamMemberRepoMock, userRepoMock, seasonRepoMock, opponentRepoMock)
    }

    @Test
    fun getTeams() {
        whenever(teamRepoMock.findByIds(teamIds)).thenReturn(mockTeams)
        val teams = teamService.getTeams(teamIds)
        assertEquals(mockTeams.map { it.toDto() }, teams)
    }
}