package com.zcorp.opensportmanagement.rest

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.whenever
import com.zcorp.opensportmanagement.model.Place
import com.zcorp.opensportmanagement.model.Team
import com.zcorp.opensportmanagement.security.AccessController
import com.zcorp.opensportmanagement.service.PlaceService
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation
import org.springframework.restdocs.payload.PayloadDocumentation
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@RunWith(SpringRunner::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@AutoConfigureRestDocs
@ActiveProfiles("test")
class PlaceControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockBean
    private lateinit var placeServiceMock: PlaceService

    @MockBean
    private lateinit var accessControllerMock: AccessController

    private val teamId = 5
    private val placeId = 63
    private val mockTeam = Team("Team", Team.Sport.BASKETBALL, Team.Gender.BOTH, Team.AgeGroup.ADULTS,
            "", teamId)
    private val placeMock = Place("Place", "address", "City", Place.PlaceType.STADIUM, mockTeam,
            placeId)

    @Test
    fun `Get place when unauthenticated should return response code 'FORBIDDEN'`() {
        this.mockMvc.perform(get("/places/$placeId")).andExpect(status().isForbidden)
    }

    @Test
    @WithMockUser("foo")
    fun `Get place when authenticated should return response code 'OK'`() {
        whenever(placeServiceMock.getPlace(placeId)).thenReturn(placeMock.toDto())
        whenever(accessControllerMock.isAccountAllowedToAccessTeam(any(), any())).thenReturn(true)
        this.mockMvc.perform(get("/places/$placeId"))
                .andExpect(status().isOk)
                .andExpect(MockMvcResultMatchers.content()
                        .json(jacksonObjectMapper().writeValueAsString(placeMock.toDto())))
                .andDo(MockMvcRestDocumentation.document("get_place", PayloadDocumentation.responseFields(
                        PayloadDocumentation.fieldWithPath("name").description("The name of the place"),
                        PayloadDocumentation.fieldWithPath("address").description("The address of the place"),
                        PayloadDocumentation.fieldWithPath("city").description("The city of the place"),
                        PayloadDocumentation.fieldWithPath("type").description("The type of the place"),
                        PayloadDocumentation.fieldWithPath("_id").description("The identifier of the place")
                )))
    }

    @Test
    @WithMockUser("foo")
    fun `Delete place when not admin should return response code 'FORBIDDEN'`() {
        whenever(placeServiceMock.getPlace(placeId)).thenReturn(placeMock.toDto())
        whenever(accessControllerMock.isTeamAdmin(any(), any())).thenReturn(false)
        this.mockMvc.perform(delete("/places/$placeId"))
                .andExpect(status().isForbidden)
    }

    @Test
    @WithMockUser("foo")
    fun `Delete place when admin should return response code 'OK'`() {
        whenever(placeServiceMock.getPlace(placeId)).thenReturn(placeMock.toDto())
        whenever(accessControllerMock.isTeamAdmin(any(), any())).thenReturn(true)
        this.mockMvc.perform(delete("/places/$placeId"))
                .andExpect(status().isOk)
    }
}