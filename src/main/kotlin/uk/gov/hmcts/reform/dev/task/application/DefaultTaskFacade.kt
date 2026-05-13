package uk.gov.hmcts.reform.dev.task.application

import uk.gov.hmcts.reform.dev.task.application.port.TaskRepository
import uk.gov.hmcts.reform.dev.task.domain.CreateTaskCommand
import uk.gov.hmcts.reform.dev.task.domain.Task
import uk.gov.hmcts.reform.dev.task.domain.TaskFactory
import uk.gov.hmcts.reform.dev.task.domain.TaskId
import java.time.Clock
import java.time.OffsetDateTime

class DefaultTaskFacade(
    private val taskRepository: TaskRepository,
    private val taskFactory: TaskFactory,
    private val clock: Clock
): TaskFacade {
    override fun createTask(command: CreateTaskCommand): Task {
        val task = taskFactory.create(command)
        return taskRepository.save(task)
    }

    override fun getTask(taskId: TaskId): Task {
        return findTaskOrThrow(taskId)
    }

    override fun getAllTasks(): List<Task> {
        return taskRepository.findAll()
    }

    override fun updateTaskStatus(command: UpdateTaskStatusCommand): Task {
        val existingTask = findTaskOrThrow(command.taskId)
        val updatedTask = existingTask.updateStatus(
            newStatus = command.status,
            updatedAt = OffsetDateTime.now(clock)
        )
        if (updatedTask === existingTask){
            return existingTask
        }
        return taskRepository.save(updatedTask)
    }


    override fun updateTaskDetails(command: UpdateTaskDetailsCommand): Task {
        val existingTask = findTaskOrThrow(command.taskId)
        val updatedTask = existingTask.updateDetails(
            title = command.title,
            description = command.description,
            dueDatetime = command.dueDateTime,
            updatedAt = OffsetDateTime.now(clock)
        )

        return taskRepository.save(updatedTask)
    }

    override fun deleteTask(taskId: TaskId) {
        if (!taskRepository.existById(taskId)){
            throw TaskNotFoundException(taskId)
        }
        taskRepository.deleteById(taskId)
    }

    private fun findTaskOrThrow(taskId: TaskId): Task {
        return taskRepository.findById(taskId)
            ?: throw TaskNotFoundException(taskId)
    }
}