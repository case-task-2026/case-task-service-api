package uk.gov.hmcts.reform.dev.task.adapter.`in`.web

import org.hamcrest.Matchers.containsString
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.delete
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.patch
import org.springframework.test.web.servlet.post
import uk.gov.hmcts.reform.dev.task.adapter.out.persistence.SpringDataTaskJpaRepository

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
class TaskControllerErrorHandlingTest(
    @Autowired private val mockMvc: MockMvc,
    @Autowired private val springDataTaskJpaRepository: SpringDataTaskJpaRepository
) {

    @BeforeEach
    fun clearDatabase() {
        springDataTaskJpaRepository.deleteAll()
    }

    @Test
    fun `returns bad request response for invalid create request`() {
        mockMvc.post("/tasks") {
            contentType = MediaType.APPLICATION_JSON
            content = """
        {
          "title": "",
          "description": "Missing valid title",
          "dueDateTime": "2026-06-12T16:30:00Z"
        }
      """.trimIndent()
        }
            .andExpect {
                status { isBadRequest() }
                jsonPath("$.status") { value(400) }
                jsonPath("$.error") { value("Bad Request") }
                jsonPath("$.message") { value("Request validation failed") }
                jsonPath("$.path") { value("/tasks") }
                jsonPath("$.fieldErrors[0].field") { value("title") }
                jsonPath("$.fieldErrors[0].message") { value("Title must not be blank") }
            }
    }

    @Test
    fun `returns bad request response for invalid task id`() {
        mockMvc.get("/tasks/not-a-valid-uuid")
            .andExpect {
                status { isBadRequest() }
                jsonPath("$.status") { value(400) }
                jsonPath("$.error") { value("Bad Request") }
                jsonPath("$.message") { value("Task id must be a valid UUID: 'not-a-valid-uuid'") }
                jsonPath("$.path") { value("/tasks/not-a-valid-uuid") }
            }
    }

    @Test
    fun `returns not found response for missing task`() {
        val missingTaskId = "99999999-9999-9999-9999-999999999999"

        mockMvc.get("/tasks/$missingTaskId")
            .andExpect {
                status { isNotFound() }
                jsonPath("$.status") { value(404) }
                jsonPath("$.error") { value("Not Found") }
                jsonPath("$.message") { value("Task with id '$missingTaskId' was not found") }
                jsonPath("$.path") { value("/tasks/$missingTaskId") }
            }
    }

    @Test
    fun `returns bad request response for invalid status value`() {
        val createdTaskId = createTaskAndReturnId("Check invalid status")

        mockMvc.patch("/tasks/$createdTaskId/status") {
            contentType = MediaType.APPLICATION_JSON
            content = """
        {
          "status": "STARTED"
        }
      """.trimIndent()
        }
            .andExpect {
                status { isBadRequest() }
                jsonPath("$.status") { value(400) }
                jsonPath("$.error") { value("Bad Request") }
                jsonPath("$.message") { value("Request body is malformed or contains invalid values") }
                jsonPath("$.path") { value("/tasks/$createdTaskId/status") }
            }
    }

    @Test
    fun `returns bad request response for malformed json`() {
        mockMvc.post("/tasks") {
            contentType = MediaType.APPLICATION_JSON
            content = """
        {
          "title": "Broken JSON",
          "dueDateTime": "2026-06-12T16:30:00Z"
      """.trimIndent()
        }
            .andExpect {
                status { isBadRequest() }
                jsonPath("$.status") { value(400) }
                jsonPath("$.error") { value("Bad Request") }
                jsonPath("$.message") { value("Request body is malformed or contains invalid values") }
                jsonPath("$.path") { value("/tasks") }
            }
    }

    @Test
    fun `returns not found response when deleting missing task`() {
        val missingTaskId = "88888888-8888-8888-8888-888888888888"

        mockMvc.delete("/tasks/$missingTaskId")
            .andExpect {
                status { isNotFound() }
                jsonPath("$.status") { value(404) }
                jsonPath("$.message") { value("Task with id '$missingTaskId' was not found") }
            }
    }

    @Test
    fun `returns method not allowed response for unsupported method`() {
        val createdTaskId = createTaskAndReturnId("Check method not allowed")

        mockMvc.post("/tasks/$createdTaskId") {
            contentType = MediaType.APPLICATION_JSON
            content = "{}"
        }
            .andExpect {
                status { isMethodNotAllowed() }
                jsonPath("$.status") { value(405) }
                jsonPath("$.error") { value("Method Not Allowed") }
                jsonPath("$.message") { value(containsString("POST")) }
            }
    }

    private fun createTaskAndReturnId(title: String): String {
        val response = mockMvc.post("/tasks") {
            contentType = MediaType.APPLICATION_JSON
            content = """
        {
          "title": "$title",
          "description": null,
          "dueDateTime": "2026-06-12T16:30:00Z"
        }
      """.trimIndent()
        }
            .andExpect {
                status { isCreated() }
            }
            .andReturn()
            .response
            .contentAsString

        return Regex(""""id":"([^"]+)"""")
            .find(response)
            ?.groupValues
            ?.get(1)
            ?: error("Created task response did not contain an id: $response")
    }
}