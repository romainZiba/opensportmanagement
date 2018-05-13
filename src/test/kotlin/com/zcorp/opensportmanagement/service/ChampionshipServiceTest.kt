package com.zcorp.opensportmanagement.service

import com.nhaarman.mockito_kotlin.*
import com.zcorp.opensportmanagement.model.Championship
import com.zcorp.opensportmanagement.model.Season
import com.zcorp.opensportmanagement.model.Team
import com.zcorp.opensportmanagement.repositories.ChampionshipRepository
import com.zcorp.opensportmanagement.repositories.MatchRepository
import com.zcorp.opensportmanagement.repositories.OpponentRepository
import com.zcorp.opensportmanagement.repositories.StadiumRepository
import com.zcorp.opensportmanagement.rest.NotFoundException
import junit.framework.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.AdditionalMatchers.not
import java.time.LocalDate
import javax.persistence.EntityNotFoundException


class ChampionshipServiceTest {
    private lateinit var championshipService: ChampionshipService
    private lateinit var championshipRepoMock: ChampionshipRepository
    private lateinit var stadiumRepoMock: StadiumRepository
    private lateinit var opponentRepoMock: OpponentRepository
    private lateinit var matchRepoMock: MatchRepository

    private val championshipId = 17
    private val stadiumId = 1
    private val teamId = 19
    private val seasonId = 230
    private val mockTeam = Team("SuperNam", Team.Sport.BASKETBALL, Team.Gender.BOTH, Team.AgeGroup.ADULTS, "", teamId)
    private val mockSeason = Season("Season", LocalDate.of(2018, 1, 1), LocalDate.of(2018, 9, 29), Season.Status.CURRENT, mockTeam, seasonId)
    private val mockChampionship = Championship("Champ", mockSeason, championshipId)

    @Before
    fun setUp() {
        championshipRepoMock = mock()
        stadiumRepoMock = mock()
        opponentRepoMock = mock()
        matchRepoMock = mock()
        championshipService = ChampionshipService(championshipRepoMock, stadiumRepoMock, opponentRepoMock, matchRepoMock)
    }

    @Test
    fun getExistingChampionship() {
        whenever(championshipRepoMock.getOne(championshipId)).thenReturn(mockChampionship)
        val c = championshipService.getChampionship(championshipId)
        assertEquals(mockChampionship.toDto(), c)
    }

    @Test(expected = NotFoundException::class)
    fun getNotExistingChampionship() {
        whenever(championshipRepoMock.getOne(not(eq(championshipId)))).thenThrow(EntityNotFoundException())
        championshipService.getChampionship(championshipId + 1)
    }

    @Test
    fun deleteChampionship() {
        whenever(matchRepoMock.deleteAllMatches(championshipId)).thenReturn(5)
        doNothing().whenever(championshipRepoMock).deleteById(championshipId)
        championshipService.deleteChampionship(championshipId)
        verify(matchRepoMock, times(1)).deleteAllMatches(championshipId)
        verify(championshipRepoMock, times(1)).deleteById(championshipId)
    }
}