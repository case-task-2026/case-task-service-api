package uk.gov.hmcts.reform.dev.task.domain

import uk.gov.hmcts.reform.dev.task.domain.*
import java.time.Clock
import java.time.Instant
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertSame

class DefaultTaskFactoryTest {
    private val fixedTaskId = TaskId.from(UUID.fromString("11111111-1111-1111-1111-111111111111"))


    private val fixedClock = Clock.fixed(
        Instant.parse("2026-05-11T10:00:00Z"),
        ZoneOffset.UTC
    )

    private val taskFactory = DefaultTaskFactory(
        taskIdGenerator = FixedTaskIdGenerator(fixedTaskId),
        clock = fixedClock
    )

    @Test
    fun `creates task with generated id default status and timestamp`() {
        val dueDateTime = OffsetDateTime.parse("2026-06-12T16:30:00Z")

        val task = taskFactory.create(
            CreateTaskCommand(
                title = "Prepare case bundle",
                description = "Collect and review required case documents",
                dueDateTime = dueDateTime
            )
        )

        assertEquals(fixedTaskId, task.id)
        assertEquals("Prepare case bundle", task.title)
        assertEquals("Collect and review required case documents", task.description)
        assertEquals(TaskStatus.TODO, task.status)
        assertEquals(dueDateTime, task.dueDatetime)
        assertEquals(OffsetDateTime.parse("2026-05-11T10:00:00Z"), task.createdAt)
        assertEquals(OffsetDateTime.parse("2026-05-11T10:00:00Z"), task.updatedAt)
    }

    @Test
    fun `trims title and converts blank description to null`(){
        val task = taskFactory.create(
            CreateTaskCommand(
                title = "   Review evidence   ",
                description = "    ",
                dueDateTime = OffsetDateTime.parse("2026-06-12T16:30:00Z")
            )
        )
    }

    @Test
    fun `rejects blank title`(){
        val exception = assertFailsWith<TaskValidationException> {
            taskFactory.create(
                CreateTaskCommand(
                    title = "    ",
                    description = null,
                    dueDateTime = OffsetDateTime.parse("2026-06-12T16:30:00Z")
                )
            )
        }
        assertEquals("Task title must not be blank", exception.message)
    }

    @Test
    fun `rejects title that exceeds maximum length`(){
        val exception = assertFailsWith<TaskValidationException> {
            taskFactory.create(
                CreateTaskCommand(
                    title = "A".repeat(TaskRules.MAX_TITLE_LENGTH + 1),
                    description = null,
                    dueDateTime = OffsetDateTime.parse("2026-06-12T16:30:00Z")
                )
            )
        }
        assertEquals("Task title must not exceed ${TaskRules.MAX_TITLE_LENGTH} characters", exception.message)
    }

    @Test
    fun `updates task status without mutating original task`(){
        val task = taskFactory.create(
            CreateTaskCommand(
                title = "Review hearing notes",
                description = null,
                dueDateTime = OffsetDateTime.parse("2026-06-12T16:30:00Z")
            )
        )

        val updatedTask = task.updateStatus(
            newStatus = TaskStatus.IN_PROGRESS,
            updatedAt = OffsetDateTime.parse("2026-05-11T11:00:00Z")
        )

        assertEquals(TaskStatus.TODO, task.status)
        assertEquals(TaskStatus.IN_PROGRESS, updatedTask.status)
        assertEquals(OffsetDateTime.parse("2026-05-11T11:00:00Z"), updatedTask.updatedAt)
    }

    @Test
    fun `returns same task  when status has not changed`(){
        val task = taskFactory.create(
            CreateTaskCommand(
                title = "Check listing details",
                description = null,
                dueDateTime = OffsetDateTime.parse("2026-06-12T16:30:00Z")
            )
        )

        val unchangedTask = task.updateStatus(
            newStatus = TaskStatus.TODO,
            updatedAt = OffsetDateTime.parse("2026-05-11T11:00:00Z")
        )

        assertSame( task, unchangedTask)
    }

    @Test
    fun `updates task details without changing task status`(){
        val task = taskFactory.create(
            CreateTaskCommand(
                title = "Initial title",
                description = "Initial description",
                dueDateTime = OffsetDateTime.parse("2026-06-12T16:30:00Z")
            )
        )

        val updatedTask = task.updateDetails(
            title = "  Updated title  ",
            description = "  Updated description  ",
            dueDatetime = OffsetDateTime.parse("2026-06-15T09:00:00Z"),
            updatedAt = OffsetDateTime.parse("2026-05-11T12:00:00Z")
        )

        assertEquals("Updated title", updatedTask.title)
        assertEquals("Updated description", updatedTask.description)
        assertEquals(TaskStatus.TODO, updatedTask.status)
        assertEquals(OffsetDateTime.parse("2026-06-15T09:00:00Z"), updatedTask.dueDatetime)
        assertEquals(OffsetDateTime.parse("2026-05-11T12:00:00Z"), updatedTask.updatedAt)
    }


    private class FixedTaskIdGenerator(
        private val taskId: TaskId
    ): TaskIdGenerator {
        override fun generate(): TaskId {
            return taskId
        }
    }
}


