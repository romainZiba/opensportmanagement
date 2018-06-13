package com.zcorp.opensportmanagement.rest

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.whenever
import com.zcorp.opensportmanagement.dto.ChampionshipMatchCreationDto
import com.zcorp.opensportmanagement.model.Championship
import com.zcorp.opensportmanagement.model.Match
import com.zcorp.opensportmanagement.model.Season
import com.zcorp.opensportmanagement.model.Team
import com.zcorp.opensportmanagement.security.AccessController
import com.zcorp.opensportmanagement.service.ChampionshipService
import com.zcorp.opensportmanagement.service.NotFoundException
import com.zcorp.opensportmanagement.service.SeasonService
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

@RunWith(SpringRunner::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@AutoConfigureRestDocs
@ActiveProfiles("test")
class SeasonControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockBean
    private lateinit var championshipServiceMock: ChampionshipService
    @MockBean
    private lateinit var seasonServiceMock: SeasonService

    @MockBean
    private lateinit var accessControllerMock: AccessController

    private val championshipId = 6
    private val teamId = 5
    private val seasonId = 58
    private val mockTeam = Team("Team", Team.Sport.BASKETBALL, Team.Gender.BOTH, Team.AgeGroup.ADULTS,
            "", teamId)
    private val mockSeason = Season("Season", LocalDate.of(2018, 1, 1), LocalDate.of(2018, 1, 9), mockTeam, seasonId)
    private val mockChampionship = Championship("Championship", mockSeason, championshipId)

    @Test
    fun `Get championships when unauthenticated should return response code 'FORBIDDEN'`() {
        this.mockMvc.perform(get("/seasons/$seasonId/championships")).andExpect(status().isForbidden)
    }

    @Test
    @WithMockUser("foo")
    fun `Get championship when authenticated should return response code 'OK'`() {
        whenever(seasonServiceMock.getSeason(seasonId)).thenReturn(mockSeason.toDto())
        whenever(championshipServiceMock.getChampionships(seasonId)).thenReturn(listOf(mockChampionship.toDto()))
        whenever(accessControllerMock.isAccountAllowedToAccessTeam(any(), any())).thenReturn(true)
        this.mockMvc.perform(get("/seasons/$seasonId/championships"))
                .andExpect(status().isOk)
                .andExpect(MockMvcResultMatchers.content()
                        .json(jacksonObjectMapper().writeValueAsString(listOf(mockChampionship.toDto()))))
    }
}