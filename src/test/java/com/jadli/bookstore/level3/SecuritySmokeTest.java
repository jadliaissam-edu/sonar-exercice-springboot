package com.jadli.bookstore.level3;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class SecuritySmokeTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void adminAreaNeedsCredentials() throws Exception {
        mockMvc.perform(get("/admin/tools/status")).andExpect(status().isUnauthorized());
    }

    @Test
    void adminCanOpenAdminArea() throws Exception {
        mockMvc.perform(get("/admin/tools/status").with(httpBasic("admin", "admin123")))
                .andExpect(status().isOk());
    }

}
