package uk.gov.hmcts.reform.dev.task.application

import uk.gov.hmcts.reform.dev.task.domain.CreateTaskCommand
import uk.gov.hmcts.reform.dev.task.domain.Task
import uk.gov.hmcts.reform.dev.task.domain.TaskId

interface TaskFacade {
    fun createTask(command: CreateTaskCommand): Task
    fun getTask(taskId: TaskId): Task
    fun getAllTasks(): List<Task>
    fun updateTaskStatus(command: UpdateTaskStatusCommand): Task
    fun updateTaskDetails(command: UpdateTaskDetailsCommand): Task
    fun deleteTask(taskId: TaskId)
}