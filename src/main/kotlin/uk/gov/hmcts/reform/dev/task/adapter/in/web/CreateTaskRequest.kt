package uk.gov.hmcts.reform.dev.task.adapter.`in`.web

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import uk.gov.hmcts.reform.dev.task.domain.TaskRules
import uk.gov.hmcts.reform.dev.task.domain.TaskStatus
import java.time.OffsetDateTime

data class CreateTaskRequest(

    @field:NotBlank(message = "Title must not be blank")
    @field:Size(
        max = TaskRules.MAX_TITLE_LENGTH,
        message = "Title must not exceed ${TaskRules.MAX_TITLE_LENGTH} characters"
    )
    val title: String?,

    @field:Size(
        max = TaskRules.MAX_DESCRIPTION_LENGTH,
        message = "Description must not exceed ${TaskRules.MAX_DESCRIPTION_LENGTH} characters"
    )
    val description: String?,

    @field:NotNull(message = "Due date/time must be provided")
    val dueDateTime: OffsetDateTime?
)


data class UpdateTaskDetailsRequest(
    @field:NotBlank(message = " Title must not be blank")
    @field:Size(
        max = TaskRules.MAX_TITLE_LENGTH,
        message = "Title must not exceed ${TaskRules.MAX_TITLE_LENGTH} characters"
    )
    val title: String?,

    @field:Size(
        max = TaskRules.MAX_DESCRIPTION_LENGTH,
        message = "Description must not exceed ${TaskRules.MAX_DESCRIPTION_LENGTH} characters"
    )
    val description: String?,

    @field:NotNull(message = "Due date/time must be provided")
    val dueDateTime: OffsetDateTime?
)

data class UpdateTaskStatusRequest(
    @field:NotNull(message = "Status must be provided")
    val status: TaskStatus?
)