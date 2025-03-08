package lambda.compiler.common

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals

class SpanTest {
    @Test
    fun `a non-empty span will have a positive length`() {
        val span = Span(1, 2)
        assertEquals(1, span.length)
    }

    @Test
    fun `an empty span will have a zero length`() {
        val span = Span(3, 3)
        assertEquals(0, span.length)
    }

    @Test
    fun `a single char span will have expected begin and end indices`() {
        val span = Span.ofSingleChar(5)
        assertEquals(Span(5, 6), span)
    }

    @Test
    fun `creating an invalid span throws`() {
        assertThrows<IllegalStateException> {
            Span(5, 4)
        }
    }

    @Test
    fun `displaying a span returns the expected string`() {
        assertEquals("8..32", Span(8, 32).display())
    }
}
