package com.storage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Testcontainers
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class AuthControllerIntegrationTest {


    @Autowired
    private MockMvc mockMvc;

    @Container
    private static final PostgreSQLContainer<?> postgresContainer =
            new PostgreSQLContainer<>("postgres:15")
                    .withDatabaseName("CloudStorageTest")
                    .withUsername("postgres")
                    .withPassword("vlad");

    @BeforeEach
    void setUp() {
        System.setProperty("spring.datasource.url", postgresContainer.getJdbcUrl());
        System.setProperty("spring.datasource.username", postgresContainer.getUsername());
        System.setProperty("spring.datasource.password", postgresContainer.getPassword());
    }


    @Test
    void testRegistrationWithFormData() throws Exception {
        String uniqueEmail = "testuser" + System.currentTimeMillis() + "@example.com";
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("username", uniqueEmail) // Уникальный email
                        .param("password", "password123")) // Непустой пароль
                .andExpect(status().is3xxRedirection()) // Проверяем статус ред
                .andExpect(redirectedUrl("/hello"));     // Проверяем URL редиректа
    }

    @Test
    void testRegistrationWithInvalidEmail() throws Exception {
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("username", "testuser")
                        .param("password", "password123"))
                .andExpect(status().isOk())
                .andExpect(result -> result.getResponse().getContentAsString().contains("Invalid email format"));
    }

    @Test
    void testRegistrationWithEmptyEmail() throws Exception {
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("username", "")
                        .param("password", "password123"))
                .andExpect(status().isOk())
                .andExpect(result -> result.getResponse().getContentAsString().contains("Username cannot be empty"));
    }

    @Test
    void testRegistrationAlreadyRegisterAccount() throws Exception {
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("username", "testuser@example.com")
                        .param("password", "password123"))
                .andExpect(status().isOk())
                .andExpect(result -> result.getResponse().getContentAsString().contains("User already exists"));
    }


    @Test
    void testLoginNotExistAccount() throws Exception {
        mockMvc.perform(post("/process_login")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("username", "nonexistentuser@example.com")
                        .param("password", "password123"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/auth/login?error"));
    }

    @Test
    void testLoginExistAccout() throws Exception {
        mockMvc.perform(post("/process_login")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("username", "testuser2@example.com")
                        .param("password", "password123"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/hello"));
    }
}
