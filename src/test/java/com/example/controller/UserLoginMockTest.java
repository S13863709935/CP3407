package com.example.controller;

import com.example.entity.User;
import com.example.service.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
public class UserLoginMockTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private org.springframework.web.socket.server.standard.ServerEndpointExporter serverEndpointExporter;

    @Test
    public void testUserLoginSuccessWithMock() throws Exception {

        // 1. Create a mock user object
        User mockUser = new User();
        mockUser.setUsername("admin");
        mockUser.setPassword("admin123");

        // 2. Mock the behavior of UserService
        Mockito.when(userService.login(Mockito.any())).thenReturn(mockUser);

        // 3. Prepare JSON request body
        String loginJson = "{\"username\":\"admin\", \"password\":\"admin123\"}";

        // 4. Perform POST request and verify expectations
        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(4001));
    }
}