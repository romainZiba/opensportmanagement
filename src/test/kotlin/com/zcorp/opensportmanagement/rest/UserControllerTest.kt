package com.zcorp.opensportmanagement.rest

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.nhaarman.mockito_kotlin.whenever
import com.zcorp.opensportmanagement.dto.UserDto
import com.zcorp.opensportmanagement.service.UserService
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
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@RunWith(SpringRunner::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
class UserControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockBean
    private lateinit var userService: UserService

    private val username = "toto"
    private val userDto = UserDto("toto", "sacred", username, "", "")

    @Test
    fun `GET on 'users-me' when unauthenticated should return a response with status 'FORBIDDEN'`() {
        this.mockMvc.perform(get("/users/me")).andExpect(status().isForbidden)
    }

    @Test
    @WithMockUser("toto")
    fun `GET on 'users-me' when authenticated should return a response with status 'OK'`() {
        whenever(userService.findByUsername(username)).thenReturn(userDto)
        this.mockMvc.perform(
                get("/users/me"))
                .andExpect(status().isOk)
                .andExpect(MockMvcResultMatchers.content().json(jacksonObjectMapper().writeValueAsString(userDto)))
    }

    @Test
    @WithMockUser("toto")
    fun `GET on 'users-me' when an user does not exist should return a response with status 'FORBIDDEN'`() {
        whenever(userService.findByUsername(username)).thenReturn(null)
        this.mockMvc.perform(
                get("/users/me"))
                .andExpect(status().isForbidden)
    }
}