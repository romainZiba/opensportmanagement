package com.zcorp.opensportmanagement.service

import assertk.assert
import assertk.assertions.hasSize
import assertk.assertions.isEmpty
import assertk.assertions.isEqualTo
import assertk.assertions.isFalse
import assertk.assertions.isInstanceOf
import assertk.assertions.isNotEmpty
import assertk.assertions.isNotNull
import assertk.assertions.isTrue
import assertk.assertions.isZero
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.doNothing
import com.nhaarman.mockito_kotlin.eq
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import com.zcorp.opensportmanagement.dto.ChampionshipMatchCreationDto
import com.zcorp.opensportmanagement.model.Championship
import com.zcorp.opensportmanagement.model.Match
import com.zcorp.opensportmanagement.model.Opponent
import com.zcorp.opensportmanagement.model.Place
import com.zcorp.opensportmanagement.model.Place.PlaceType
import com.zcorp.opensportmanagement.model.Season
import com.zcorp.opensportmanagement.model.Team
import com.zcorp.opensportmanagement.repository.ChampionshipRepository
import com.zcorp.opensportmanagement.repository.MatchRepository
import com.zcorp.opensportmanagement.repository.OpponentRepository
import com.zcorp.opensportmanagement.repository.PlaceRepository
import org.junit.jupiter.api.Test
import org.mockito.AdditionalMatchers.not
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
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
    private val mockSeason = Season("Season", LocalDate.of(2018, 1, 1), LocalDate.of(2018, 9, 29), mockTeam, seasonId)
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
        val matchDto = ChampionshipMatchCreationDto(
                fromDate = LocalDate.of(2018, 1, 1),
                toDate = LocalDate.of(2018, 1, 1),
                fromTime = LocalTime.of(19, 50),
                toTime = LocalTime.of(21, 50),
                matchType = Match.MatchType.CHAMPIONSHIP,
                placeId = placeId,
                opponentId = opponentId,
                championshipId = championshipId)
        whenever(championshipRepoMock.findById(championshipId)).thenReturn(Optional.empty())
        assert {
            championshipService.createMatch(matchDto, championshipId, LocalDateTime.of(2017, 1, 1, 10, 0))
        }.thrownError { isInstanceOf(NotFoundException::class) }
    }

    @Test
    fun `create match with a place that does not exist should not be possible`() {
        val matchDto = ChampionshipMatchCreationDto(
                fromDate = LocalDate.of(2018, 1, 1),
                toDate = LocalDate.of(2018, 1, 1),
                fromTime = LocalTime.of(19, 50),
                toTime = LocalTime.of(21, 50),
                matchType = Match.MatchType.CHAMPIONSHIP,
                placeId = placeId,
                opponentId = opponentId,
                championshipId = championshipId)
        whenever(championshipRepoMock.findById(championshipId)).thenReturn(Optional.of(mockChampionship))
        whenever(placeRepoMock.findById(placeId)).thenReturn(Optional.empty())
        assert {
            championshipService.createMatch(matchDto, championshipId, LocalDateTime.of(2017, 1, 1, 10, 0))
        }.thrownError { isInstanceOf(NotFoundException::class) }
    }

    @Test
    fun `create match with an opponent that does not exist should not be possible`() {
        val matchDto = ChampionshipMatchCreationDto(
                fromDate = LocalDate.of(2018, 1, 1),
                toDate = LocalDate.of(2018, 1, 1),
                fromTime = LocalTime.of(19, 50),
                toTime = LocalTime.of(21, 50),
                matchType = Match.MatchType.CHAMPIONSHIP,
                placeId = placeId,
                opponentId = opponentId,
                championshipId = championshipId)
        whenever(championshipRepoMock.findById(championshipId)).thenReturn(Optional.of(mockChampionship))
        whenever(placeRepoMock.findById(placeId)).thenReturn(Optional.of(mockStadium))
        whenever(opponentRepoMock.findById(opponentId)).thenReturn(Optional.empty())
        assert {
            championshipService.createMatch(matchDto, championshipId, LocalDateTime.of(2017, 1, 1, 10, 0))
        }.thrownError { isInstanceOf(NotFoundException::class) }
    }

    @Test
    fun `create match in the past should not be possible`() {
        val dto = ChampionshipMatchCreationDto(
                fromDate = LocalDate.of(2018, 1, 1),
                toDate = LocalDate.of(2018, 1, 1),
                fromTime = LocalTime.of(19, 50),
                toTime = LocalTime.of(21, 50),
                matchType = Match.MatchType.CHAMPIONSHIP,
                opponentId = opponentId,
                placeId = placeId,
                championshipId = championshipId)
        whenever(championshipRepoMock.findById(championshipId)).thenReturn(Optional.of(mockChampionship))
        whenever(placeRepoMock.findById(placeId)).thenReturn(Optional.of(mockStadium))
        whenever(opponentRepoMock.findById(opponentId)).thenReturn(Optional.of(mockOpponent))
        assert {
            championshipService.createMatch(dto, championshipId, LocalDateTime.of(2018, 1, 1, 19, 51))
        }.thrownError { isInstanceOf(NotPossibleException::class) }
    }

    @Test
    fun `create match sometimes work`() {
        val dto = ChampionshipMatchCreationDto(
                fromDate = LocalDate.now().plusDays(7),
                toDate = LocalDate.now().plusDays(7),
                fromTime = LocalTime.of(19, 50),
                toTime = LocalTime.of(21, 50),
                matchType = Match.MatchType.CHAMPIONSHIP,
                opponentId = opponentId,
                placeId = placeId,
                championshipId = championshipId)
        whenever(championshipRepoMock.findById(championshipId)).thenReturn(Optional.of(mockChampionship))
        whenever(placeRepoMock.findById(placeId)).thenReturn(Optional.of(mockStadium))
        whenever(opponentRepoMock.findById(opponentId)).thenReturn(Optional.of(mockOpponent))
        whenever(matchRepoMock.save<Match>(any())).thenAnswer { it.arguments[0] }
        val match = championshipService.createMatch(dto, championshipId, LocalDateTime.of(2017, 1, 1, 10, 0))
        assert(match.championship).isEqualTo(mockChampionship)
        assert(match.isDone).isFalse()
        assert(match.isTeamLocal).isTrue()
        assert(match.team).isEqualTo(mockTeam)
        assert(match.opponent).isEqualTo(mockOpponent)
        assert(match.opponentScore).isZero()
        assert(match.teamScore).isZero()
        assert(match.type).isEqualTo(Match.MatchType.CHAMPIONSHIP)
    }

    @Test
    fun `championship list when season does not exist should be empty`() {
        whenever(championshipRepoMock.findBySeasonId(any())).thenReturn(emptyList())
        val championships = championshipService.getChampionships(1)
        assert(championships).isNotNull()
        assert(championships).isEmpty()
    }

    @Test
    fun `get championships of a season that exists`() {
        whenever(championshipRepoMock.findBySeasonId(any())).thenReturn(listOf(mockChampionship))
        val championships = championshipService.getChampionships(1)
        assert(championships).isNotNull()
        assert(championships).isNotEmpty()
        assert(championships).hasSize(1)
        assert(championships[0]).isEqualTo(mockChampionship.toDto())
    }
}