package com.zcorp.opensportmanagement.service

import assertk.assert
import assertk.assertions.isEqualTo
import assertk.assertions.isFalse
import assertk.assertions.isInstanceOf
import assertk.assertions.isTrue
import assertk.assertions.isZero
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.doNothing
import com.nhaarman.mockito_kotlin.eq
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import com.zcorp.opensportmanagement.dto.MatchCreationDto
import com.zcorp.opensportmanagement.model.Championship
import com.zcorp.opensportmanagement.model.Match
import com.zcorp.opensportmanagement.model.Opponent
import com.zcorp.opensportmanagement.model.Place
import com.zcorp.opensportmanagement.model.Place.PlaceType
import com.zcorp.opensportmanagement.model.Season
import com.zcorp.opensportmanagement.model.Team
import com.zcorp.opensportmanagement.repositories.ChampionshipRepository
import com.zcorp.opensportmanagement.repositories.MatchRepository
import com.zcorp.opensportmanagement.repositories.OpponentRepository
import com.zcorp.opensportmanagement.repositories.PlaceRepository
import org.junit.Test
import org.mockito.AdditionalMatchers.not
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.Optional

class ChampionshipServiceTest {
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
    private val mockStadium = Place("The place", "", "Toulouse", PlaceType.STADIUM, mockTeam, placeId)
    private val mockOpponent = Opponent("TerribleOpponent", "", "", "", mockTeam, opponentId)

    @Test
    fun `get championship that exists should be possible`() {
        whenever(championshipRepoMock.findById(championshipId)).thenReturn(Optional.of(mockChampionship))
        val c = championshipService.getChampionship(championshipId)
        assert(c).isEqualTo(mockChampionship.toDto())
    }

    @Test
    fun `get championship that does not exist should not be possible`() {
        whenever(championshipRepoMock.findById(not(eq(championshipId)))).thenReturn(Optional.empty())
        assert {
            championshipService.getChampionship(championshipId + 1)
        }.thrownError { isInstanceOf(NotFoundException::class) }
    }

    @Test
    fun `delete championship that exists should be possible`() {
        whenever(matchRepoMock.deleteAllMatches(championshipId)).thenReturn(5)
        doNothing().whenever(championshipRepoMock).deleteById(championshipId)
        championshipService.deleteChampionship(championshipId)
        verify(matchRepoMock, times(1)).deleteAllMatches(championshipId)
        verify(championshipRepoMock, times(1)).deleteById(championshipId)
    }

    @Test
    fun `create match for a championship that does not exist should not be possible`() {
        val matchDto = MatchCreationDto("match", LocalDateTime.of(2018, 1, 1, 19, 50),
                matchType = Match.MatchType.CHAMPIONSHIP, placeId = placeId)
        whenever(championshipRepoMock.findById(championshipId)).thenReturn(Optional.empty())
        assert {
            championshipService.createMatch(matchDto, championshipId)
        }.thrownError { isInstanceOf(NotFoundException::class) }
    }

    @Test
    fun `create match with a place that does not exist should not be possible`() {
        val matchDto = MatchCreationDto("match", LocalDateTime.of(2018, 1, 1, 19, 50),
                matchType = Match.MatchType.CHAMPIONSHIP, placeId = placeId, opponentId = opponentId)
        whenever(championshipRepoMock.findById(championshipId)).thenReturn(Optional.of(mockChampionship))
        whenever(placeRepoMock.findById(placeId)).thenReturn(Optional.empty())
        assert {
            championshipService.createMatch(matchDto, championshipId)
        }.thrownError { isInstanceOf(NotFoundException::class) }
    }

    @Test
    fun `create match with an opponent that does not exist should not be possible`() {
        val matchDto = MatchCreationDto("match", LocalDateTime.of(2018, 1, 1, 19, 50),
                matchType = Match.MatchType.CHAMPIONSHIP, placeId = placeId, opponentId = opponentId)
        whenever(championshipRepoMock.findById(championshipId)).thenReturn(Optional.of(mockChampionship))
        whenever(placeRepoMock.findById(placeId)).thenReturn(Optional.of(mockStadium))
        whenever(opponentRepoMock.findById(opponentId)).thenReturn(Optional.empty())
        assert {
            championshipService.createMatch(matchDto, championshipId)
        }.thrownError { isInstanceOf(NotFoundException::class) }
    }

    @Test
    fun `create match in the past should not be possible`() {
        val dto = MatchCreationDto("match", LocalDateTime.of(2018, 1, 1, 19, 50),
                matchType = Match.MatchType.CHAMPIONSHIP, opponentId = opponentId, placeId = placeId)
        whenever(championshipRepoMock.findById(championshipId)).thenReturn(Optional.of(mockChampionship))
        whenever(placeRepoMock.findById(placeId)).thenReturn(Optional.of(mockStadium))
        whenever(opponentRepoMock.findById(opponentId)).thenReturn(Optional.of(mockOpponent))
        assert {
            championshipService.createMatch(dto, championshipId)
        }.thrownError { isInstanceOf(PastEventException::class) }
    }

    @Test
    fun `create match sometimes work`() {
        val dto = MatchCreationDto("match", LocalDateTime.now().plusDays(7),
                matchType = Match.MatchType.CHAMPIONSHIP, opponentId = opponentId, placeId = placeId)
        whenever(championshipRepoMock.findById(championshipId)).thenReturn(Optional.of(mockChampionship))
        whenever(placeRepoMock.findById(placeId)).thenReturn(Optional.of(mockStadium))
        whenever(opponentRepoMock.findById(opponentId)).thenReturn(Optional.of(mockOpponent))
        whenever(matchRepoMock.save<Match>(any())).thenAnswer { it.arguments[0] }
        val match = championshipService.createMatch(dto, championshipId)
        assert(match.championship).isEqualTo(mockChampionship)
        assert(match.isDone).isFalse()
        assert(match.isTeamLocal).isTrue()
        assert(match.team).isEqualTo(mockTeam)
        assert(match.opponent).isEqualTo(mockOpponent)
        assert(match.opponentScore).isZero()
        assert(match.teamScore).isZero()
        assert(match.type).isEqualTo(Match.MatchType.CHAMPIONSHIP)
    }
}