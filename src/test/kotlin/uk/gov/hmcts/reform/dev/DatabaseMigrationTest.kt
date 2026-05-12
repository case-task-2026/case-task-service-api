package uk.gov.hmcts.reform.dev

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.test.context.ActiveProfiles

@ActiveProfiles("test")
@SpringBootTest
class DatabaseMigrationTest(
    @Autowired private val jdbcTemplate: JdbcTemplate
) {

    @Test
    fun `flyway creates tasks table`() {
        val tableCount = jdbcTemplate.queryForObject(
            """
      SELECT COUNT(*)
      FROM information_schema.tables
      WHERE table_name = 'tasks'
      """.trimIndent(),
            Int::class.java
        )

        assertEquals(1, tableCount)
    }

    @Test
    fun `tasks table contains expected columns`() {
        val columnNames = jdbcTemplate.queryForList(
            """
      SELECT column_name
      FROM information_schema.columns
      WHERE table_name = 'tasks'
      ORDER BY ordinal_position
      """.trimIndent(),
            String::class.java
        )

        assertTrue(columnNames.contains("id"))
        assertTrue(columnNames.contains("title"))
        assertTrue(columnNames.contains("description"))
        assertTrue(columnNames.contains("status"))
        assertTrue(columnNames.contains("due_date_time"))
        assertTrue(columnNames.contains("created_at"))
        assertTrue(columnNames.contains("updated_at"))
    }
}