package uk.gov.hmcts.reform.dev.task.application


import uk.gov.hmcts.reform.dev.task.application.port.TaskRepository
import uk.gov.hmcts.reform.dev.task.domain.CreateTaskCommand
import uk.gov.hmcts.reform.dev.task.domain.DefaultTaskFactory
import uk.gov.hmcts.reform.dev.task.domain.Task
import uk.gov.hmcts.reform.dev.task.domain.TaskFactory
import uk.gov.hmcts.reform.dev.task.domain.TaskId
import uk.gov.hmcts.reform.dev.task.domain.TaskIdGenerator
import uk.gov.hmcts.reform.dev.task.domain.TaskStatus
import java.time.Clock
import java.time.Instant
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.util.UUID
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class DefaultTaskFacadeTest {

    private val fixedClock = Clock.fixed(
        Instant.parse("2026-05-11T10:00:00Z"),
        ZoneOffset.UTC
    )

    private val fixedTaskId = TaskId.from(UUID.fromString("11111111-1111-1111-1111-111111111111"))
    private val taskRepository = InMemoryTaskRepository()
    private val taskFactory: TaskFactory = DefaultTaskFactory(
        taskIdGenerator = FixedTaskIdGenerator(fixedTaskId),
        clock = fixedClock
    )

    private val taskFacade = DefaultTaskFacade(
        taskRepository = taskRepository,
        taskFactory = taskFactory,
        clock = fixedClock
    )

    @Test
    fun `creates and saves task`(){
        val createdTask = taskFacade.createTask(
            CreateTaskCommand(
                title = "Prepare case bundle",
                description = "Collect required documents",
                dueDateTime = OffsetDateTime.parse("2026-06-12T16:30:00Z")
            )
        )

        assertEquals(fixedTaskId, createdTask.id)
        assertEquals("Prepare case bundle", createdTask.title)
        assertEquals("Collect required documents", createdTask.description)
        assertEquals(TaskStatus.TODO, createdTask.status)
        assertTrue(taskRepository.existById(fixedTaskId))
    }

    @Test
    fun `gets task by id`(){
        val task = buildTask(
            id = fixedTaskId,
            title = "Review evidence"
        )
        taskRepository.save(task)

        val foundTask = taskFacade.getTask(fixedTaskId)
        assertEquals(task.id, foundTask.id)
        assertEquals("Review evidence", foundTask.title)
    }

    @Test
    fun `throws not found when getting missing task`(){
        val missingTaskId = TaskId.from("99999999-9999-9999-9999-999999999999")

        val exception = assertFailsWith<TaskNotFoundException> {
            taskFacade.getTask(missingTaskId)
        }

        assertEquals("Task with id '$missingTaskId' was not found", exception.message)
    }

    @Test
    fun `get all tasks`(){
        val firstTask = buildTask(
            id = TaskId.from("22222222-2222-2222-2222-222222222222"),
            title = "First task",
            dueDateTime = OffsetDateTime.parse("2026-06-12T09:00:00Z")
        )
        val secondTask = buildTask(
            id = TaskId.from("33333333-3333-3333-3333-333333333333"),
            title = "Second task",
            dueDateTime = OffsetDateTime.parse("2026-06-13T09:00:00Z")
        )

        taskRepository.save(secondTask)
        taskRepository.save(firstTask)

        val tasks = taskFacade.getAllTasks()

        assertEquals(2, tasks.size)
        assertEquals("First task", tasks[0].title)
        assertEquals("Second task", tasks[1].title)
    }

    @Test
    fun `updates task status`() {
        val task = buildTask(
            id = fixedTaskId,
            title = "Review listing details",
            status = TaskStatus.TODO,
            createdAt = OffsetDateTime.parse("2026-05-11T09:00:00Z"),
            updatedAt = OffsetDateTime.parse("2026-05-11T09:00:00Z")
        )
        taskRepository.save(task)

        val updatedTask = taskFacade.updateTaskStatus(
            UpdateTaskStatusCommand(
                taskId = fixedTaskId,
                status = TaskStatus.IN_PROGRESS
            )
        )

        assertEquals(TaskStatus.IN_PROGRESS, updatedTask.status)
        assertEquals(OffsetDateTime.parse("2026-05-11T10:00:00Z"), updatedTask.updatedAt)
    }

    @Test
    fun `does not save task when status has not changed`() {
        val task = buildTask(
            id = fixedTaskId,
            title = "Check case reference",
            status = TaskStatus.TODO,
            createdAt = OffsetDateTime.parse("2026-05-11T09:00:00Z"),
            updatedAt = OffsetDateTime.parse("2026-05-11T09:00:00Z")
        )
        taskRepository.save(task)
        taskRepository.saveCallCount = 0

        val unchangedTask = taskFacade.updateTaskStatus(
            UpdateTaskStatusCommand(
                taskId = fixedTaskId,
                status = TaskStatus.TODO
            )
        )

        assertEquals(TaskStatus.TODO, unchangedTask.status)
        assertEquals(0, taskRepository.saveCallCount)
    }

    @Test
    fun `throws not found when updating missing task status`() {
        val missingTaskId = TaskId.from("99999999-9999-9999-9999-999999999999")

        val exception = assertFailsWith<TaskNotFoundException> {
            taskFacade.updateTaskStatus(
                UpdateTaskStatusCommand(
                    taskId = missingTaskId,
                    status = TaskStatus.COMPLETED
                )
            )
        }

        assertEquals("Task with id '$missingTaskId' was not found", exception.message)
    }

    @Test
    fun `updates task details`() {
        val task = buildTask(
            id = fixedTaskId,
            title = "Old title",
            description = "Old description",
            createdAt = OffsetDateTime.parse("2026-05-11T09:00:00Z"),
            updatedAt = OffsetDateTime.parse("2026-05-11T09:00:00Z")
        )
        taskRepository.save(task)

        val updatedTask = taskFacade.updateTaskDetails(
            UpdateTaskDetailsCommand(
                taskId = fixedTaskId,
                title = "Updated title",
                description = "Updated description",
                dueDateTime = OffsetDateTime.parse("2026-06-20T12:00:00Z")
            )
        )

        assertEquals("Updated title", updatedTask.title)
        assertEquals("Updated description", updatedTask.description)
        assertEquals(OffsetDateTime.parse("2026-06-20T12:00:00Z"), updatedTask.dueDatetime)
        assertEquals(OffsetDateTime.parse("2026-05-11T10:00:00Z"), updatedTask.updatedAt)
    }

    @Test
    fun `deletes task`() {
        val task = buildTask(
            id = fixedTaskId,
            title = "Task to delete"
        )
        taskRepository.save(task)

        taskFacade.deleteTask(fixedTaskId)

        assertFalse(taskRepository.existById(fixedTaskId))
    }

    @Test
    fun `throws not found when deleting missing task`() {
        val missingTaskId = TaskId.from("99999999-9999-9999-9999-999999999999")

        val exception = assertFailsWith<TaskNotFoundException> {
            taskFacade.deleteTask(missingTaskId)
        }

        assertEquals("Task with id '$missingTaskId' was not found", exception.message)
    }

    private fun buildTask(
        id: TaskId,
        title: String,
        description: String? = null,
        status: TaskStatus = TaskStatus.TODO,
        dueDateTime: OffsetDateTime = OffsetDateTime.parse("2026-06-12T16:30:00Z"),
        createdAt: OffsetDateTime = OffsetDateTime.parse("2026-05-11T09:00:00Z"),
        updatedAt: OffsetDateTime = OffsetDateTime.parse("2026-05-11T09:00:00Z"),
    ): Task {
        return Task.restore(
            id = id,
            title = title,
            description = description,
            status = status,
            dueDateTime = dueDateTime,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }

    private class FixedTaskIdGenerator(
        private val taskId: TaskId
    ): TaskIdGenerator {
        override fun generate(): TaskId {
            return taskId
        }
    }

    private class InMemoryTaskRepository : TaskRepository {

        private val tasks = linkedMapOf<TaskId, Task>()

        var saveCallCount = 0

        override fun save(task: Task): Task {
            saveCallCount += 1
            tasks[task.id] = task
            return task
        }

        override fun findById(id: TaskId): Task? {
            return tasks[id]
        }

        override fun findAll(): List<Task> {
            return tasks.values
                .sortedWith(compareBy<Task> { it.dueDatetime }.thenBy { it.createdAt })
        }

        override fun existById(id: TaskId): Boolean {
            return tasks.containsKey(id)
        }

        override fun deleteById(id: TaskId) {
            tasks.remove(id)
        }
    }
}


