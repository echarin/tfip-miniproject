package ibf2022.tfipminiproject.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;

import ibf2022.tfipminiproject.dtos.BudgetDTO;
import ibf2022.tfipminiproject.entities.Budget;
import ibf2022.tfipminiproject.services.AuthenticationService;
import ibf2022.tfipminiproject.services.BudgetService;

@SpringBootTest
public class BudgetControllerTest {
    
    @Autowired
    private BudgetController budgetController;

    @MockBean
    private AuthenticationService authService;

    @MockBean
    private BudgetService budgetService;

    private MockMvc mockMvc;

    @BeforeEach
    public void setup(WebApplicationContext wac) {
        MockitoAnnotations.openMocks(this);
        this.mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
    }

    @Test
    @WithMockUser(username = "test@test.com", password = "password", authorities = { "ROLE_USER" })
    public void getBudgetSuccess() throws Exception {
        UUID userId = UUID.randomUUID();
        Budget budget = new Budget();
        
        when(authService.doesUserIdMatch(any(), any())).thenReturn(true);
        when(budgetService.findByUser(any())).thenReturn(budget);

        mockMvc.perform(get("/api/v1/" + userId + "/budget")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "test@test.com", password = "password", authorities = { "ROLE_USER" })
    public void getBudgetAccessDenied() throws Exception {
        UUID userId = UUID.randomUUID();

        when(authService.doesUserIdMatch(any(), any())).thenReturn(false);

        mockMvc.perform(get("/api/v1/" + userId + "/budget")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "test@test.com", password = "password", authorities = { "ROLE_USER" })
    public void createBudgetSuccess() throws Exception {
        UUID userId = UUID.randomUUID();
        BudgetDTO budgetDTO = new BudgetDTO();

        when(authService.doesUserIdMatch(any(), any())).thenReturn(true);
        when(budgetService.save(any(), any())).thenReturn(new Budget());

        mockMvc.perform(post("/api/v1/" + userId + "/budget")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(budgetDTO)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "test@test.com", password = "password", authorities = { "ROLE_USER" })
    public void deleteBudgetSuccess() throws Exception {
        UUID userId = UUID.randomUUID();
        UUID budgetId = UUID.randomUUID();

        when(authService.doesUserIdMatch(any(), any())).thenReturn(true);

        mockMvc.perform(delete("/api/v1/" + userId + "/budget/" + budgetId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    private String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
