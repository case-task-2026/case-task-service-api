package uk.gov.hmcts.reform.dev

import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import kotlin.test.Test

@ActiveProfiles("test")
@SpringBootTest
class ApplicationTest {

    @Test
    fun `context loads`(){
        // This test passes when Spring can start the application context successfully.
    }
}