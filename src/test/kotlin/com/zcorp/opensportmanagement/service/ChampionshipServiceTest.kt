package com.zcorp.opensportmanagement.service

import com.nhaarman.mockito_kotlin.*
import com.zcorp.opensportmanagement.model.Championship
import com.zcorp.opensportmanagement.model.Season
import com.zcorp.opensportmanagement.model.Team
import com.zcorp.opensportmanagement.repositories.ChampionshipRepository
import com.zcorp.opensportmanagement.repositories.MatchRepository
import com.zcorp.opensportmanagement.repositories.OpponentRepository
import com.zcorp.opensportmanagement.repositories.StadiumRepository
import io.kotlintest.shouldBe
import io.kotlintest.shouldThrow
import io.kotlintest.specs.StringSpec
import org.mockito.AdditionalMatchers.not
import java.time.LocalDate
import javax.persistence.EntityNotFoundException

class ChampionshipServiceTest : StringSpec() {
    private val championshipRepoMock: ChampionshipRepository = mock()
    private val stadiumRepoMock: StadiumRepository = mock()
    private val opponentRepoMock: OpponentRepository = mock()
    private val matchRepoMock: MatchRepository = mock()
    private val championshipService = ChampionshipService(championshipRepoMock, stadiumRepoMock, opponentRepoMock, matchRepoMock)

    private val championshipId = 17
    private val teamId = 19
    private val seasonId = 230
    private val mockTeam = Team("SuperNam", Team.Sport.BASKETBALL, Team.Gender.BOTH, Team.AgeGroup.ADULTS, "", teamId)
    private val mockSeason = Season("Season", LocalDate.of(2018, 1, 1), LocalDate.of(2018, 9, 29), Season.Status.CURRENT, mockTeam, seasonId)
    private val mockChampionship = Championship("Champ", mockSeason, championshipId)

    override fun isInstancePerTest() = true

    init {
        "get championship that exists should be possible" {
            whenever(championshipRepoMock.getOne(championshipId)).thenReturn(mockChampionship)
            val c = championshipService.getChampionship(championshipId)
            c shouldBe mockChampionship.toDto()
        }

        "get championship that does not exist should not be possible" {
            whenever(championshipRepoMock.getOne(not(eq(championshipId)))).thenThrow(EntityNotFoundException())
            shouldThrow<NotFoundException> {
                championshipService.getChampionship(championshipId + 1)
            }
        }

        "delete championship that exists should be possible" {
            whenever(matchRepoMock.deleteAllMatches(championshipId)).thenReturn(5)
            doNothing().whenever(championshipRepoMock).deleteById(championshipId)
            championshipService.deleteChampionship(championshipId)
            verify(matchRepoMock, times(1)).deleteAllMatches(championshipId)
            verify(championshipRepoMock, times(1)).deleteById(championshipId)
        }
    }
}