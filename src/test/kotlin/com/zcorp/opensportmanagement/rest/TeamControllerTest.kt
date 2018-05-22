package com.zcorp.opensportmanagement.rest

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.whenever
import com.zcorp.opensportmanagement.dto.EventCreationDto
import com.zcorp.opensportmanagement.dto.PlaceDto
import com.zcorp.opensportmanagement.security.AccessController
import com.zcorp.opensportmanagement.service.BadParameterException
import com.zcorp.opensportmanagement.service.EventService
import com.zcorp.opensportmanagement.service.TeamService
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.http.MediaType.APPLICATION_JSON
import java.time.LocalDate
import java.time.LocalTime

@RunWith(SpringRunner::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
class TeamControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockBean
    private lateinit var teamService: TeamService
    @MockBean
    private lateinit var eventService: EventService
    @MockBean
    private lateinit var accessController: AccessController

    private val mockPlacesDto = listOf(PlaceDto("", "", "", 1, 1))

    @Test
    fun `GET places when unauthenticated should return a response with status 'FORBIDDEN'`() {
        this.mockMvc.perform(get("/teams/1/places")).andExpect(status().isForbidden)
    }

    @Test
    @WithMockUser("toto")
    fun `GET places when authenticated should return a response with status 'OK'`() {
        whenever(accessController.isUserAllowedToAccessTeam(any(), any())).thenReturn(true)
        whenever(teamService.getPlaces(any())).thenReturn(mockPlacesDto)
        this.mockMvc.perform(
                get("/teams/1/places"))
                .andExpect(status().isOk)
                .andExpect(MockMvcResultMatchers.content().json(jacksonObjectMapper().writeValueAsString(mockPlacesDto)))
    }

    @Test
    @WithMockUser("toto")
    fun `Get opponents when authenticated should return a response with status 'OK'`() {
        whenever(accessController.isUserAllowedToAccessTeam(any(), any())).thenReturn(true)
        this.mockMvc.perform(get("/teams/1/opponents"))
                .andExpect(status().isOk)
    }

    @Test
    @WithMockUser("toto")
    fun `Create event with an empty name should return a response with status 'BAD REQUEST'`() {
        whenever(accessController.isUserAllowedToAccessTeam(any(), any())).thenReturn(true)
        whenever(eventService.createEvent(any(), any())).thenThrow(BadParameterException(""))
        val eventCreationDto = EventCreationDto("", LocalDate.of(2050, 1, 1), LocalDate.of(2050, 2, 1),
                LocalTime.of(16, 0), LocalTime.of(18, 0), 1)
        this.mockMvc.perform(
                post("/teams/1/events")
                        .contentType(APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content(jacksonObjectMapper().findAndRegisterModules().writeValueAsString(eventCreationDto)))
                .andExpect(status().isBadRequest)
    }
}