package com.zcorp.opensportmanagement.rest

import com.zcorp.opensportmanagement.repositories.UserRepository
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status


@RunWith(SpringRunner::class)
@WebMvcTest(UserController::class)
class UserControllerIT {

    @Autowired
    private lateinit var mvc: MockMvc

    @MockBean
    private lateinit var userRepository: UserRepository

    @Test
    @Throws(Exception::class)
    fun getAccount() {
        this.mvc.perform(get("/users/me")
                .accept(MediaType.parseMediaType("application/json;charset=UTF-8")))
                .andExpect(status().isUnauthorized)
    }
}