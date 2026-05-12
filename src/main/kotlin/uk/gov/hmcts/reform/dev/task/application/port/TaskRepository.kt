package uk.gov.hmcts.reform.dev.task.application.port

import uk.gov.hmcts.reform.dev.task.domain.Task
import uk.gov.hmcts.reform.dev.task.domain.TaskId

interface TaskRepository {
    fun save(task: Task): Task
    fun findById(id: TaskId): Task?
    fun findAll(): List<Task>
    fun existById(id: TaskId): Boolean
    fun deleteById(id: TaskId)
}