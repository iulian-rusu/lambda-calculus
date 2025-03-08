package lambda.compiler.common

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class LocationTest {
    @Test
    fun `displaying a location returns the expected string`() {
        assertEquals("128:15", Location(128, 15).display())
    }
}
