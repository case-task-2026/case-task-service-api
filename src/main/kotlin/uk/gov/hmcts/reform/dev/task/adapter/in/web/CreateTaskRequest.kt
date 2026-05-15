package uk.gov.hmcts.reform.dev.task.adapter.`in`.web

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import java.time.OffsetDateTime
import uk.gov.hmcts.reform.dev.task.domain.TaskRules
import uk.gov.hmcts.reform.dev.task.domain.TaskStatus

@Schema(description = "Request body used to create a new task")
data class CreateTaskRequest(

    @field:Schema(
        description = "Short title describing the task",
        example = "Prepare case bundle",
        maxLength = TaskRules.MAX_TITLE_LENGTH,
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    @field:NotBlank(message = "Title must not be blank")
    @field:Size(
        max = TaskRules.MAX_TITLE_LENGTH,
        message = "Title must not exceed ${TaskRules.MAX_TITLE_LENGTH} characters"
    )
    val title: String?,

    @field:Schema(
        description = "Optional longer description of the task",
        example = "Collect and review required case documents",
        maxLength = TaskRules.MAX_DESCRIPTION_LENGTH,
        requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    @field:Size(
        max = TaskRules.MAX_DESCRIPTION_LENGTH,
        message = "Description must not exceed ${TaskRules.MAX_DESCRIPTION_LENGTH} characters"
    )
    val description: String?,

    @field:Schema(
        description = "Date and time when the task is due, represented as an ISO-8601 timestamp",
        example = "2026-06-12T16:30:00Z",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    @field:NotNull(message = "Due date/time must be provided")
    val dueDateTime: OffsetDateTime?
)

@Schema(description = "Request body used to update editable task details")
data class UpdateTaskDetailsRequest(

    @field:Schema(
        description = "Updated task title",
        example = "Updated case bundle",
        maxLength = TaskRules.MAX_TITLE_LENGTH,
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    @field:NotBlank(message = "Title must not be blank")
    @field:Size(
        max = TaskRules.MAX_TITLE_LENGTH,
        message = "Title must not exceed ${TaskRules.MAX_TITLE_LENGTH} characters"
    )
    val title: String?,

    @field:Schema(
        description = "Updated optional task description",
        example = "Updated description after reviewing the case notes",
        maxLength = TaskRules.MAX_DESCRIPTION_LENGTH,
        requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    @field:Size(
        max = TaskRules.MAX_DESCRIPTION_LENGTH,
        message = "Description must not exceed ${TaskRules.MAX_DESCRIPTION_LENGTH} characters"
    )
    val description: String?,

    @field:Schema(
        description = "Updated due date and time, represented as an ISO-8601 timestamp",
        example = "2026-07-01T10:00:00Z",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    @field:NotNull(message = "Due date/time must be provided")
    val dueDateTime: OffsetDateTime?
)

@Schema(description = "Request body used to update the status of a task")
data class UpdateTaskStatusRequest(

    @field:Schema(
        description = "Updated task status",
        example = "IN_PROGRESS",
        allowableValues = ["TODO", "IN_PROGRESS", "COMPLETED"],
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    @field:NotNull(message = "Status must be provided")
    val status: TaskStatus?
)