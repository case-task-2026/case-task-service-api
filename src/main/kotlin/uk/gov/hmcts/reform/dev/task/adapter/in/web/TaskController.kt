package uk.gov.hmcts.reform.dev.task.adapter.`in`.web

import jakarta.validation.Valid
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
import uk.gov.hmcts.reform.dev.task.domain.TaskId
import java.net.URI

@RestController
@RequestMapping("/tasks")
class TaskController(
    private val taskFacade: TaskFacade,
    private val taskWebMapper: TaskWebMapper
) {

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

    @GetMapping("/{taskId}")
    fun getTask(
        @PathVariable taskId: String
    ): ResponseEntity<TaskResponse> {
        val task = taskFacade.getTask(TaskId.from(taskId))

        return ResponseEntity.ok(taskWebMapper.toResponse(task))
    }

    @GetMapping
    fun getAllTasks(): ResponseEntity<List<TaskResponse>> {
        val tasks = taskFacade.getAllTasks()
            .map(taskWebMapper::toResponse)

        return ResponseEntity.ok(tasks)
    }

    @PutMapping("/{taskId}")
    fun updateTaskDetails(
        @PathVariable taskId: String,
        @Valid @RequestBody request: UpdateTaskDetailsRequest
    ): ResponseEntity<TaskResponse> {
        val updatedTask = taskFacade.updateTaskDetails(
            taskWebMapper.toUpdateDetailsCommand(
                taskId = TaskId.from(taskId),
                request = request
            )
        )

        return ResponseEntity.ok(taskWebMapper.toResponse(updatedTask))
    }

    @PatchMapping("/{taskId}/status")
    fun updateTaskStatus(
        @PathVariable taskId: String,
        @Valid @RequestBody request: UpdateTaskStatusRequest
    ): ResponseEntity<TaskResponse> {
        val updatedTask = taskFacade.updateTaskStatus(
            taskWebMapper.toUpdateStatusCommand(
                taskId = TaskId.from(taskId),
                request = request
            )
        )

        return ResponseEntity.ok(taskWebMapper.toResponse(updatedTask))
    }

    @DeleteMapping("/{taskId}")
    fun deleteTask(
        @PathVariable taskId: String
    ): ResponseEntity<Void> {
        taskFacade.deleteTask(TaskId.from(taskId))

        return ResponseEntity.noContent().build()

    }

}