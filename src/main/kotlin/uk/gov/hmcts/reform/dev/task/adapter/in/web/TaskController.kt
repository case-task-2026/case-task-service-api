package uk.gov.hmcts.reform.dev.task.adapter.`in`.web

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import java.net.URI
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import uk.gov.hmcts.reform.dev.task.application.TaskFacade

@Tag(
    name = "Tasks",
    description = "Operations for creating, viewing, updating, and deleting caseworker tasks"
)
@RestController
@RequestMapping("/tasks")
class TaskController(
    private val taskFacade: TaskFacade,
    private val taskWebMapper: TaskWebMapper
) {

    @Operation(
        summary = "Create a task",
        description = "Creates a new task with a title, optional description, due date/time, and default TODO status.",
        responses = [
            ApiResponse(
                responseCode = "201",
                description = "Task created successfully",
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(implementation = TaskResponse::class)
                    )
                ]
            ),
            ApiResponse(
                responseCode = "400",
                description = "Invalid create task request",
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(implementation = ApiErrorResponse::class)
                    )
                ]
            )
        ]
    )
    @PostMapping
    fun createTask(
        @Valid @RequestBody request: CreateTaskRequest
    ): ResponseEntity<TaskResponse> {
        val createdTask = taskFacade.createTask(
            taskWebMapper.toCreateCommand(request)
        )

        return ResponseEntity
            .created(URI.create("/tasks/${createdTask.id}"))
            .body(taskWebMapper.toResponse(createdTask))
    }

    @Operation(
        summary = "Retrieve a task by ID",
        description = "Returns a single task by its unique identifier.",
        responses = [
            ApiResponse(
                responseCode = "200",
                description = "Task found",
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(implementation = TaskResponse::class)
                    )
                ]
            ),
            ApiResponse(
                responseCode = "400",
                description = "Invalid task ID format",
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(implementation = ApiErrorResponse::class)
                    )
                ]
            ),
            ApiResponse(
                responseCode = "404",
                description = "Task not found",
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(implementation = ApiErrorResponse::class)
                    )
                ]
            )
        ]
    )
    @GetMapping("/{taskId}")
    fun getTask(
        @Parameter(
            description = "Task UUID",
            example = "3e0cc629-cc55-406b-ada6-f33bfbdf92b0"
        )
        @PathVariable taskId: String
    ): ResponseEntity<TaskResponse> {
        val task = taskFacade.getTask(
            taskWebMapper.toTaskId(taskId)
        )

        return ResponseEntity.ok(taskWebMapper.toResponse(task))
    }

    @Operation(
        summary = "Retrieve all tasks",
        description = "Returns all tasks ordered by due date/time and creation time.",
        responses = [
            ApiResponse(
                responseCode = "200",
                description = "Tasks retrieved successfully"
            )
        ]
    )
    @GetMapping
    fun getAllTasks(): ResponseEntity<List<TaskResponse>> {
        val tasks = taskFacade.getAllTasks()
            .map(taskWebMapper::toResponse)

        return ResponseEntity.ok(tasks)
    }

    @Operation(
        summary = "Update task details",
        description = "Updates the editable task details: title, description, and due date/time.",
        responses = [
            ApiResponse(
                responseCode = "200",
                description = "Task details updated successfully",
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(implementation = TaskResponse::class)
                    )
                ]
            ),
            ApiResponse(
                responseCode = "400",
                description = "Invalid update request or task ID format",
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(implementation = ApiErrorResponse::class)
                    )
                ]
            ),
            ApiResponse(
                responseCode = "404",
                description = "Task not found",
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(implementation = ApiErrorResponse::class)
                    )
                ]
            )
        ]
    )
    @PutMapping("/{taskId}")
    fun updateTaskDetails(
        @Parameter(
            description = "Task UUID",
            example = "3e0cc629-cc55-406b-ada6-f33bfbdf92b0"
        )
        @PathVariable taskId: String,
        @Valid @RequestBody request: UpdateTaskDetailsRequest
    ): ResponseEntity<TaskResponse> {
        val updatedTask = taskFacade.updateTaskDetails(
            taskWebMapper.toUpdateDetailsCommand(
                taskId = taskWebMapper.toTaskId(taskId),
                request = request
            )
        )

        return ResponseEntity.ok(taskWebMapper.toResponse(updatedTask))
    }

    @Operation(
        summary = "Update task status",
        description = "Updates only the status of an existing task.",
        responses = [
            ApiResponse(
                responseCode = "200",
                description = "Task status updated successfully",
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(implementation = TaskResponse::class)
                    )
                ]
            ),
            ApiResponse(
                responseCode = "400",
                description = "Invalid status value or task ID format",
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(implementation = ApiErrorResponse::class)
                    )
                ]
            ),
            ApiResponse(
                responseCode = "404",
                description = "Task not found",
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(implementation = ApiErrorResponse::class)
                    )
                ]
            )
        ]
    )
    @PatchMapping("/{taskId}/status")
    fun updateTaskStatus(
        @Parameter(
            description = "Task UUID",
            example = "3e0cc629-cc55-406b-ada6-f33bfbdf92b0"
        )
        @PathVariable taskId: String,
        @Valid @RequestBody request: UpdateTaskStatusRequest
    ): ResponseEntity<TaskResponse> {
        val updatedTask = taskFacade.updateTaskStatus(
            taskWebMapper.toUpdateStatusCommand(
                taskId = taskWebMapper.toTaskId(taskId),
                request = request
            )
        )

        return ResponseEntity.ok(taskWebMapper.toResponse(updatedTask))
    }

    @Operation(
        summary = "Delete a task",
        description = "Deletes an existing task by its unique identifier.",
        responses = [
            ApiResponse(
                responseCode = "204",
                description = "Task deleted successfully"
            ),
            ApiResponse(
                responseCode = "400",
                description = "Invalid task ID format",
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(implementation = ApiErrorResponse::class)
                    )
                ]
            ),
            ApiResponse(
                responseCode = "404",
                description = "Task not found",
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(implementation = ApiErrorResponse::class)
                    )
                ]
            )
        ]
    )
    @DeleteMapping("/{taskId}")
    fun deleteTask(
        @Parameter(
            description = "Task UUID",
            example = "3e0cc629-cc55-406b-ada6-f33bfbdf92b0"
        )
        @PathVariable taskId: String
    ): ResponseEntity<Void> {
        taskFacade.deleteTask(
            taskWebMapper.toTaskId(taskId)
        )

        return ResponseEntity.noContent().build()
    }
}