package uk.gov.hmcts.reform.dev.task.adapter.`in`.web


import org.hamcrest.Matchers.hasSize
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
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.put
import uk.gov.hmcts.reform.dev.task.adapter.out.persistence.SpringDataTaskJpaRepository


@ActiveProfiles
@SpringBootTest
@AutoConfigureMockMvc
class TaskControllerTest(
    @Autowired private val mockMvc: MockMvc,
    @Autowired private val springDataTaskJpaRepository: SpringDataTaskJpaRepository
) {

    @BeforeEach
    fun clearDatabase() {
        springDataTaskJpaRepository.deleteAll()
    }

    @Test
    fun `create task`(){
        mockMvc.post("/tasks") {
            contentType = MediaType.APPLICATION_JSON
            content = """
                {
                "title": "Prepare case bundle",
                "description": "Collect required documents",
                "dueDateTime": "2026-06-12T16:30:00Z"
                }
            """.trimIndent()
        }
            .andExpect {
                status { isCreated() }
                header { exists("Location") }
                jsonPath("$.id") { exists()}
                jsonPath("$.title") { value("Prepare case bundle")}
                jsonPath("$.description") { value("Collect required documents")}
                jsonPath("$.status") { value("TODO")}
                jsonPath("$.dueDateTime") { value("2026-06-12T16:30:00Z")}
                jsonPath("$.createdAt") { exists()}
                jsonPath("$.updatedAt") { exists()}
            }
    }

    @Test
    fun `gets task by id`() {
        val createdTaskId = createTaskAndReturnId("Review evidence")

        mockMvc.get("/tasks/$createdTaskId")
            .andExpect {
                status { isOk() }
                jsonPath("$.id") { value(createdTaskId)}
                jsonPath("$.title") { value("Review evidence")}
                jsonPath("$.status") { value("TODO")}
            }
    }

    @Test
    fun `gets all tasks`() {
        createTaskAndReturnId("First task", "2026-06-12T09:00:00Z")
        createTaskAndReturnId("Second task", "2026-06-20T09:00:00Z")


        mockMvc.get("/tasks")
            .andExpect {
                status { isOk() }
                jsonPath("$", hasSize<Any>(2))
                jsonPath("$[0].title") { value("First task")}
                jsonPath("$[1].title") { value("Second task")}
            }
    }

    @Test
    fun `update task details`(){

        val createdTaskId = createTaskAndReturnId("Original title")

        mockMvc.put("/tasks/$createdTaskId") {
            contentType = MediaType.APPLICATION_JSON
            content = """
                {
                "title": "Updated title",
                "description": "Updated description",
                "dueDateTime": "2026-07-01T10:00:00Z"
                }
            """.trimIndent()
        }
            .andExpect {
                status { isOk() }
                jsonPath("$.id") { value(createdTaskId)}
                jsonPath("$.title") { value("Updated title")}
                jsonPath("$.description") { value("Updated description")}
                jsonPath("$.dueDateTime") { value("2026-07-01T10:00:00Z")}
                jsonPath("$.status") { value("TODO")}
            }
    }

    @Test
    fun `update task status`(){

        val createdTaskId = createTaskAndReturnId("Check status update")

        mockMvc.put("/tasks/$createdTaskId/status") {
            contentType = MediaType.APPLICATION_JSON
            content = """
                {
                "status": "IN_PROGRESS"
                }
            """.trimIndent()
        }
            .andExpect {
                status { isOk() }
                jsonPath("$.id") { value(createdTaskId)}
                jsonPath("$.title") { value("Check status update")}
                jsonPath("$.status") { value("IN_PROGRESS")}
            }
    }

    @Test
    fun `deletes task`() {
        val createdTaskId = createTaskAndReturnId("Task to delete")

        mockMvc.delete("/tasks/$createdTaskId")
            .andExpect {
                status { isNoContent() }
            }
    }

    @Test
    fun `returns bad request when create request is invalid`(){

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
            }
    }

    private fun createTaskAndReturnId(
        title: String,
        dueDateTime: String = "2026-06-12T16:30:00Z"
    ): String {
        val response = mockMvc.post("/tasks") {
            contentType = MediaType.APPLICATION_JSON
            content = """
                {
                "title": "$title",
                "description": null,
                "dueDateTime": "$dueDateTime"
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