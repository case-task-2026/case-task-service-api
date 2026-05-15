package uk.gov.hmcts.reform.dev.task.adapter.`in`.web

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import kotlin.test.assertTrue

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
class OpenApiDocumentationTest(
    @Autowired private val mockMvc: MockMvc
) {

    @Test
    fun `exposes OpenAPI documentation`(){
        val response = mockMvc.get("/v3/api-docs")
            .andExpect {
                status { isOk() }
            }
            .andReturn()
            .response
            .contentAsString

        assertTrue(response.contains("\"title\":\"Case Task Service API\""))
        assertTrue(response.contains("\"/tasks\""))
        assertTrue(response.contains("\"/tasks/{taskId}\""))
        assertTrue(response.contains("\"/tasks/{taskId}/status\""))
        assertTrue(response.contains("\"summary\":\"Create a task\""))
        assertTrue(response.contains("\"summary\":\"Update task status\""))
    }

    @Test
    fun `exposes Swagger UI`() {
        mockMvc.get("/swagger-ui/index.html")
            .andExpect {
                status { isOk() }
            }
    }
}