package uk.gov.hmcts.reform.dev.task.adapter.`in`.web

import org.springframework.stereotype.Component
import uk.gov.hmcts.reform.dev.task.application.UpdateTaskDetailsCommand
import uk.gov.hmcts.reform.dev.task.application.UpdateTaskStatusCommand
import uk.gov.hmcts.reform.dev.task.domain.CreateTaskCommand
import uk.gov.hmcts.reform.dev.task.domain.Task
import uk.gov.hmcts.reform.dev.task.domain.TaskId

@Component
class TaskWebMapper {

    fun toTaskId(taskId: String): TaskId {
        return runCatching {
            TaskId.from(taskId)
        }.getOrElse {
            throw InvalidTaskIdException(taskId)
        }
    }

    fun toCreateCommand(request: CreateTaskRequest): CreateTaskCommand {
        return CreateTaskCommand(
            title = requireNotNull(request.title),
            description = request.description,
            dueDateTime = requireNotNull(request.dueDateTime)
        )
    }

    fun toUpdateDetailsCommand(
        taskId: TaskId,
        request: UpdateTaskDetailsRequest
    ): UpdateTaskDetailsCommand {
        return UpdateTaskDetailsCommand(
            taskId = taskId,
            title = requireNotNull(request.title),
            description = request.description,
            dueDateTime = requireNotNull(request.dueDateTime)
        )
    }

    fun toUpdateStatusCommand(
        taskId: TaskId,
        request: UpdateTaskStatusRequest
    ): UpdateTaskStatusCommand {
        return UpdateTaskStatusCommand(
            taskId = taskId,
            status = requireNotNull(request.status)
        )
    }

    fun toResponse(task: Task): TaskResponse {
        return TaskResponse(
            id = task.id.toString(),
            title = task.title,
            description = task.description,
            status = task.status,
            dueDateTime = task.dueDatetime,
            createdAt = task.createdAt,
            updatedAt = task.updatedAt
        )
    }
}