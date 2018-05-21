package com.zcorp.opensportmanagement.rest

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.doThrow
import com.nhaarman.mockito_kotlin.whenever
import com.zcorp.opensportmanagement.dto.UserDto
import com.zcorp.opensportmanagement.security.AccessController
import com.zcorp.opensportmanagement.service.ChampionshipService
import com.zcorp.opensportmanagement.service.NotFoundException
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status


@RunWith(SpringRunner::class)
@WebMvcTest(ChampionshipController::class)
@ContextConfiguration
class ChampionshipControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockBean
    private lateinit var championshipServiceMock: ChampionshipService

    @MockBean
    private lateinit var accessControllerMock: AccessController

    private val championshipId = 1
    private val username = "toto"

    @Test
    fun `GET on 'championshipId' when unauthenticated should return response code 'UNAUTHORIZED'`() {
        this.mockMvc.perform(get("/$championshipId")).andExpect(status().isUnauthorized)
    }

    @Test
    fun `POST on 'championshipId-matches' when unauthenticated should return response code 'FORBIDDEN'`() {
        this.mockMvc.perform(post("/$championshipId/matches")).andExpect(status().isForbidden)
    }

    @Test
    @WithMockUser("toto")
    fun `POST on 'championshipId-matches' when championship does not exist should return response code 'FORBIDDEN'`() {
        whenever(championshipServiceMock.getChampionship(any())).thenThrow(NotFoundException(""))
        this.mockMvc.perform(post("/$championshipId/matches")).andExpect(status().isForbidden)
    }
}