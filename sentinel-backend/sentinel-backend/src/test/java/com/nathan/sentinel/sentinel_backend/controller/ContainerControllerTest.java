package com.nathan.sentinel.sentinel_backend.controller;

import com.nathan.sentinel.config.JwtAuthenticationFilter;
import com.nathan.sentinel.config.SecurityConfig;
import com.nathan.sentinel.controller.ContainerController;
import com.nathan.sentinel.repository.UserRepository;
import com.nathan.sentinel.service.DockerStatsService;
import com.nathan.sentinel.service.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ContainerController.class)
// We import the real SecurityConfig and the real JwtAuthenticationFilter.
@Import({SecurityConfig.class, JwtAuthenticationFilter.class})
class ContainerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    // We only mock the services that our security layer depends on.
    @MockBean
    private DockerStatsService dockerStatsService; // Needed by the controller
    @MockBean
    private JwtService jwtService; // Needed by the REAL JwtAuthenticationFilter
    @MockBean
    private UserRepository userRepository; // Needed by the REAL SecurityConfig to create the UserDetailsService

    @Test
    void whenUnauthenticated_thenContainersEndpointReturnsForbidden() throws Exception {
        mockMvc.perform(get("/api/v1/containers"))
                // Spring Security's default for unauthenticated access is 403 Forbidden.
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser
    void whenAuthenticated_thenContainersEndpointReturnsOk() throws Exception {
        mockMvc.perform(get("/api/v1/containers"))
                .andExpect(status().isOk());
    }
}