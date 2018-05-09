package com.zcorp.opensportmanagement.service

import com.nhaarman.mockito_kotlin.whenever
import com.zcorp.opensportmanagement.model.Team
import com.zcorp.opensportmanagement.repositories.*
import junit.framework.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito


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
        teamRepoMock = Mockito.mock(TeamRepository::class.java)
        teamMemberRepoMock = Mockito.mock(TeamMemberRepository::class.java)
        userRepoMock = Mockito.mock(UserRepository::class.java)
        seasonRepoMock = Mockito.mock(SeasonRepository::class.java)
        opponentRepoMock = Mockito.mock(OpponentRepository::class.java)
        teamService = TeamService(teamRepoMock, teamMemberRepoMock, userRepoMock, seasonRepoMock, opponentRepoMock)
    }

    @Test
    fun getTeams() {
        whenever(teamRepoMock.findByIds(teamIds)).thenReturn(mockTeams)
        val teams = teamService.getTeams(teamIds)
        assertEquals(mockTeams.map { it.toDto() }, teams)
    }
}