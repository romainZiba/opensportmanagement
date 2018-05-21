package com.zcorp.opensportmanagement.service

import com.nhaarman.mockito_kotlin.*
import com.zcorp.opensportmanagement.dto.MatchCreationDto
import com.zcorp.opensportmanagement.model.*
import com.zcorp.opensportmanagement.repositories.ChampionshipRepository
import com.zcorp.opensportmanagement.repositories.MatchRepository
import com.zcorp.opensportmanagement.repositories.OpponentRepository
import com.zcorp.opensportmanagement.repositories.PlaceRepository
import io.kotlintest.shouldBe
import io.kotlintest.shouldThrow
import io.kotlintest.specs.StringSpec
import org.mockito.AdditionalMatchers.not
import java.time.LocalDate
import java.time.LocalDateTime
import javax.persistence.EntityNotFoundException

class ChampionshipServiceTest : StringSpec() {
    private val championshipRepoMock: ChampionshipRepository = mock()
    private val placeRepoMock: PlaceRepository = mock()
    private val opponentRepoMock: OpponentRepository = mock()
    private val matchRepoMock: MatchRepository = mock()
    private val championshipService = ChampionshipService(championshipRepoMock, placeRepoMock, opponentRepoMock, matchRepoMock)

    private val championshipId = 17
    private val placeId = 5
    private val opponentId = 1
    private val teamId = 19
    private val seasonId = 230
    private val mockTeam = Team("SuperNam", Team.Sport.BASKETBALL, Team.Gender.BOTH, Team.AgeGroup.ADULTS, "", teamId)
    private val mockSeason = Season("Season", LocalDate.of(2018, 1, 1), LocalDate.of(2018, 9, 29), Season.Status.CURRENT, mockTeam, seasonId)
    private val mockChampionship = Championship("Champ", mockSeason, championshipId)
    private val mockStadium = Place("The place", "", "Toulouse", mockTeam, placeId)
    private val mockOpponent = Opponent("TerribleOpponent", "", "", "", mockTeam, opponentId)

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

        "create match for a championship that does not exist should not be possible" {
            val matchDto = MatchCreationDto("match", LocalDateTime.of(2018, 1, 1, 19, 50),
                    matchType = Match.MatchType.CHAMPIONSHIP, placeId = placeId)
            whenever(championshipRepoMock.getOne(championshipId)).thenThrow(EntityNotFoundException())
            shouldThrow<NotFoundException> {
                championshipService.createMatch(matchDto, championshipId)
            }
        }

        "create match with a place that does not exist should not be possible" {
            val matchDto = MatchCreationDto("match", LocalDateTime.of(2018, 1, 1, 19, 50),
                    matchType = Match.MatchType.CHAMPIONSHIP, placeId = placeId, opponentId = opponentId)
            whenever(championshipRepoMock.getOne(championshipId)).thenReturn(mockChampionship)
            whenever(placeRepoMock.getOne(placeId)).thenThrow(EntityNotFoundException())
            shouldThrow<NotFoundException> {
                championshipService.createMatch(matchDto, championshipId)
            }
        }

        "create match with an opponent that does not exist should not be possible" {
            val matchDto = MatchCreationDto("match", LocalDateTime.of(2018, 1, 1, 19, 50),
                    matchType = Match.MatchType.CHAMPIONSHIP, placeId = placeId, opponentId = opponentId)
            whenever(championshipRepoMock.getOne(championshipId)).thenReturn(mockChampionship)
            whenever(placeRepoMock.getOne(placeId)).thenReturn(mockStadium)
            whenever(opponentRepoMock.getOne(opponentId)).thenThrow(EntityNotFoundException())
            shouldThrow<NotFoundException> {
                championshipService.createMatch(matchDto, championshipId)
            }
        }

        "create match in the past should not be possible" {
            val dto = MatchCreationDto("match", LocalDateTime.of(2018, 1, 1, 19, 50),
                    matchType = Match.MatchType.CHAMPIONSHIP, opponentId = opponentId, placeId = placeId)
            whenever(championshipRepoMock.getOne(championshipId)).thenReturn(mockChampionship)
            whenever(placeRepoMock.getOne(placeId)).thenReturn(mockStadium)
            whenever(opponentRepoMock.getOne(opponentId)).thenReturn(mockOpponent)
            shouldThrow<PastEventException> {
                championshipService.createMatch(dto, championshipId)
            }
        }

        "create match sometimes work" {
            val dto = MatchCreationDto("match", LocalDateTime.now().plusDays(7),
                    matchType = Match.MatchType.CHAMPIONSHIP, opponentId = opponentId, placeId = placeId)
            whenever(championshipRepoMock.getOne(championshipId)).thenReturn(mockChampionship)
            whenever(placeRepoMock.getOne(placeId)).thenReturn(mockStadium)
            whenever(opponentRepoMock.getOne(opponentId)).thenReturn(mockOpponent)
            whenever(matchRepoMock.save<Match>(any())).thenAnswer { it.arguments[0] }
            val match = championshipService.createMatch(dto, championshipId)
            match.championship shouldBe mockChampionship
            match.isDone shouldBe false
            match.isTeamLocal shouldBe true
            match.team shouldBe mockTeam
            match.opponent shouldBe mockOpponent
            match.opponentScore shouldBe 0
            match.teamScore shouldBe 0
            match.type shouldBe Match.MatchType.CHAMPIONSHIP
        }
    }
}