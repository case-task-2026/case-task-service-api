package uk.gov.hmcts.reform.dev.task.adapter.`in`.web

import io.swagger.v3.oas.annotations.media.Schema
import java.time.OffsetDateTime
import uk.gov.hmcts.reform.dev.task.domain.TaskStatus

@Schema(description = "Task response returned by the API")
data class TaskResponse(

    @field:Schema(
        description = "Unique task identifier",
        example = "3e0cc629-cc55-406b-ada6-f33bfbdf92b0"
    )
    val id: String,

    @field:Schema(
        description = "Task title",
        example = "Prepare case bundle"
    )
    val title: String,

    @field:Schema(
        description = "Optional task description",
        example = "Collect and review required case documents"
    )
    val description: String?,

    @field:Schema(
        description = "Current task status",
        example = "TODO",
        allowableValues = ["TODO", "IN_PROGRESS", "COMPLETED"]
    )
    val status: TaskStatus,

    @field:Schema(
        description = "Task due date and time",
        example = "2026-06-12T16:30:00Z"
    )
    val dueDateTime: OffsetDateTime,

    @field:Schema(
        description = "Timestamp when the task was created",
        example = "2026-05-14T09:30:00Z"
    )
    val createdAt: OffsetDateTime,

    @field:Schema(
        description = "Timestamp when the task was last updated",
        example = "2026-05-14T10:15:00Z"
    )
    val updatedAt: OffsetDateTime
)