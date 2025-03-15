package lambda.compiler.diagnostic

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

    @Test
    fun `chaining any operation after an error returns the inital error`() {
        val firstResult: Result<Unit> = TEST_ERROR.asResult()
        val secondResult = firstResult.andThen { Result.Ok(it) }
        assertEquals(TEST_ERROR, secondResult.errorOrNull)
    }

    @Test
    fun `chaining two successful operations returns a success`() {
        val firstResult = Result.Ok(2)
        val secondResult = firstResult.andThen { Result.Ok(it * it) }
        assertEquals(4, secondResult.valueOrNull)
    }

    @Test
    fun `chaining a error after a success returns the error`() {
        val firstResult = Result.Ok(2)
        val secondResult = firstResult.andThen { fail(it) }
        assertEquals(TEST_ERROR, secondResult.errorOrNull)
    }

    @Test
    fun `calling valueOrElse on an Ok result returns the value`() {
        val result = Result.Ok(1)
        assertEquals(1, result.valueOrElse { 2 })
    }

    @Test
    fun `calling valueOrElse on an Error result returns the alternative`() {
        val result = TEST_ERROR.asResult()
        assertEquals(2, result.valueOrElse { 2 })
    }

    companion object {
        private val TEST_ERROR = LexicalError.InvalidChar('_', Location(1, 1))
        private fun <T> fail(x: T): Result<T> = TEST_ERROR.asResult()
    }
}
