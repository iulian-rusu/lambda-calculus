package lambda.compiler.common

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class SourceCursorTest {
    @Test
    fun `creating a cursor correctly initializes all the properties`() {
        val source = "test"
        val cursor = SourceCursor(source)
        assertEquals(0, cursor.index)
        assertEquals(Location(1, 1), cursor.location)
        assertEquals(source[0], cursor.currentChar)
        assertEquals(Span(0, 1), cursor.currentCharSpan)
    }

    @Test
    fun `indexing a cursor with a span returns expected results`() {
        val source = "source"
        val cursor = SourceCursor(source)
        assertEquals("our", cursor[Span(1, 4)])
    }

    @Test
    fun `moving a cursor outside bounds returns expected results`() {
        val source = ""
        val cursor = SourceCursor(source)
        cursor.advance()
        assertEquals(0, cursor.index)
        assertEquals(Location(1, 1), cursor.location)
        assertNull(cursor.currentChar)
    }

    @Test
    fun `advancing a cursor over a line break increments the current line`() {
        val source = "first line\nsecond line"
        val cursor = cursorToEnd(source)
        assertNull(cursor.currentChar)
        assertEquals(source.length, cursor.index)
        assertEquals(Location(2, 12), cursor.location)
    }

    companion object {
        fun cursorToEnd(source: String): SourceCursor {
            val cursor = SourceCursor(source)
            repeat(source.length) {
                cursor.advance()
            }
            return cursor
        }
    }
}
