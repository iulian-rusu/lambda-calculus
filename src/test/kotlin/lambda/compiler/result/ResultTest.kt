package lambda.compiler.result

import lambda.compiler.common.Location
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import kotlin.test.assertEquals

class ResultTest {
    @Test
    fun `mapping a value applies the function to the value`() {
        val result = Result.Ok(1)

        assertEquals(1, result.valueOrNull)
        assertEquals(Result.Ok(2), result.map { it * 2 })
    }

    @Test
    fun `mapping an error does not call the function`() {
        val error = LexicalError.InvalidChar('_', Location(1, 1))
        val result = Result.Error(error)

        assertEquals(error, result.errorOrNull)
        assertDoesNotThrow {
            assertEquals(result, result.map { error("This should not be called") })
        }
    }
}
