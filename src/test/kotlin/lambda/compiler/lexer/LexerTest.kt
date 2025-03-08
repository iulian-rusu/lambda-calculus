package lambda.compiler.lexer

import lambda.TestUtils
import lambda.compiler.common.Location
import lambda.compiler.common.Span
import lambda.compiler.result.LexicalError
import lambda.compiler.result.Result
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class LexerTest {
    @Test
    fun `tokenizing an empty source returns an empty sequence`() {
        assertExpectedTokenResults("", emptyList())
    }

    @Test
    fun `tokenizing single character tokens returns expected results`() {
        val source = "\\ . ()"
        val expectedTokens = listOf(
            Token(
                kind = TokenKind.REVERSE_SOLIDUS,
                value = "\\",
                span = Span(0, 1),
                location = Location(1, 1)
            ),
            Token(
                kind = TokenKind.DOT,
                value = ".",
                span = Span(2, 3),
                location = Location(1, 3)
            ),
            Token(
                kind = TokenKind.LEFT_PAREN,
                value = "(",
                span = Span(4, 5),
                location = Location(1, 5)
            ),
            Token(
                kind = TokenKind.RIGHT_PAREN,
                value = ")",
                span = Span(5, 6),
                location = Location(1, 6)
            ),
        )
        assertExpectedTokens(source, expectedTokens)
    }

    @Test
    fun `tokenizing identifiers returns expected results`() {
        val source = "ident1 ident2\nident3\tident4\rident5"
        val expectedTokens = listOf(
            Token(
                kind = TokenKind.IDENT,
                value = "ident1",
                span = Span(0, 6),
                location = Location(1, 1)
            ),
            Token(
                kind = TokenKind.IDENT,
                value = "ident2",
                span = Span(7, 13),
                location = Location(1, 8)
            ),
            Token(
                kind = TokenKind.IDENT,
                value = "ident3",
                span = Span(14, 20),
                location = Location(2, 1)
            ),
            Token(
                kind = TokenKind.IDENT,
                value = "ident4",
                span = Span(21, 27),
                location = Location(2, 8)
            ),
            Token(
                kind = TokenKind.IDENT,
                value = "ident5",
                span = Span(28, 34),
                location = Location(2, 15)
            ),
        )
        assertExpectedTokens(source, expectedTokens)
    }

    @Test
    fun `tokenizing a program returns expected results`() {
        val source = TestUtils.readResourceFile("lexically-valid-program.txt")
        val expectedTokens = listOf(
            Token(
                kind = TokenKind.LEFT_PAREN,
                value = "(",
                span = Span(0, 1),
                location = Location(1, 1)
            ),
            Token(
                kind = TokenKind.REVERSE_SOLIDUS,
                value = "\\",
                span = Span(1, 2),
                location = Location(1, 2)
            ),
            Token(
                kind = TokenKind.IDENT,
                value = "x",
                span = Span(2, 3),
                location = Location(1, 3)
            ),
            Token(
                kind = TokenKind.DOT,
                value = ".",
                span = Span(3, 4),
                location = Location(1, 4)
            ),
            Token(
                kind = TokenKind.REVERSE_SOLIDUS,
                value = "\\",
                span = Span(4, 5),
                location = Location(1, 5)
            ),
            Token(
                kind = TokenKind.IDENT,
                value = "test",
                span = Span(5, 9),
                location = Location(1, 6)
            ),
            Token(
                kind = TokenKind.DOT,
                value = ".",
                span = Span(9, 10),
                location = Location(1, 10)
            ),
            Token(
                kind = TokenKind.RIGHT_PAREN,
                value = ")",
                span = Span(10, 11),
                location = Location(1, 11)
            ),
            Token(
                kind = TokenKind.LEFT_PAREN,
                value = "(",
                span = Span(11, 12),
                location = Location(1, 12)
            ),
            Token(
                kind = TokenKind.REVERSE_SOLIDUS,
                value = "\\",
                span = Span(18, 19),
                location = Location(2, 5)
            ),
            Token(
                kind = TokenKind.IDENT,
                value = "x1",
                span = Span(19, 21),
                location = Location(2, 6)
            ),
            Token(
                kind = TokenKind.REVERSE_SOLIDUS,
                value = "\\",
                span = Span(21, 22),
                location = Location(2, 8)
            ),
            Token(
                kind = TokenKind.IDENT,
                value = "y1",
                span = Span(22, 24),
                location = Location(2, 9)
            ),
            Token(
                kind = TokenKind.DOT,
                value = ".",
                span = Span(24, 25),
                location = Location(2, 11)
            ),
            Token(
                kind = TokenKind.DOT,
                value = ".",
                span = Span(25, 26),
                location = Location(2, 12)
            ),
            Token(
                kind = TokenKind.LEFT_PAREN,
                value = "(",
                span = Span(26, 27),
                location = Location(2, 13)
            ),
            Token(
                kind = TokenKind.RIGHT_PAREN,
                value = ")",
                span = Span(27, 28),
                location = Location(2, 14)
            ),
            Token(
                kind = TokenKind.LEFT_PAREN,
                value = "(",
                span = Span(28, 29),
                location = Location(2, 15)
            ),
            Token(
                kind = TokenKind.REVERSE_SOLIDUS,
                value = "\\",
                span = Span(39, 40),
                location = Location(3, 9)
            ),
            Token(
                kind = TokenKind.REVERSE_SOLIDUS,
                value = "\\",
                span = Span(40, 41),
                location = Location(3, 10)
            ),
            Token(
                kind = TokenKind.IDENT,
                value = "_test",
                span = Span(41, 46),
                location = Location(3, 11)
            ),
            Token(
                kind = TokenKind.LEFT_PAREN,
                value = "(",
                span = Span(46, 47),
                location = Location(3, 16)
            ),
            Token(
                kind = TokenKind.IDENT,
                value = "_1",
                span = Span(47, 49),
                location = Location(3, 17)
            ),
            Token(
                kind = TokenKind.RIGHT_PAREN,
                value = ")",
                span = Span(49, 50),
                location = Location(3, 19)
            ),
            Token(
                kind = TokenKind.LEFT_PAREN,
                value = "(",
                span = Span(60, 61),
                location = Location(4, 9)
            ),
            Token(
                kind = TokenKind.IDENT,
                value = "test_1",
                span = Span(61, 67),
                location = Location(4, 10)
            ),
            Token(
                kind = TokenKind.DOT,
                value = ".",
                span = Span(67, 68),
                location = Location(4, 16)
            ),
            Token(
                kind = TokenKind.IDENT,
                value = "test_123",
                span = Span(68, 76),
                location = Location(4, 17)
            ),
            Token(
                kind = TokenKind.RIGHT_PAREN,
                value = ")",
                span = Span(76, 77),
                location = Location(4, 25)
            ),
            Token(
                kind = TokenKind.RIGHT_PAREN,
                value = ")",
                span = Span(83, 84),
                location = Location(5, 5)
            ),
            Token(
                kind = TokenKind.RIGHT_PAREN,
                value = ")",
                span = Span(86, 87),
                location = Location(6, 1)
            ),
        )
        assertExpectedTokens(source, expectedTokens)
    }

    @Test
    fun `tokenizing an invalid source returns an error`() {
        val expectedTokenResults = listOf(
            Result.Error(
                LexicalError.InvalidChar(
                    char = '$',
                    location = Location(1, 1)
                )
            )
        )
        assertExpectedTokenResults("$", expectedTokenResults)
    }

    @Test
    fun `tokenizing a partially valid source returns some tokens and some errors`() {
        val source = "@\\.[name]"
        val expectedTokenResults = listOf(
            Result.Error(
                LexicalError.InvalidChar(
                    char = '@',
                    location = Location(1, 1)
                )
            ),
            Result.Ok(
                Token(
                    kind = TokenKind.REVERSE_SOLIDUS,
                    value = "\\",
                    span = Span(1, 2),
                    location = Location(1, 2)
                )
            ),
            Result.Ok(
                Token(
                    kind = TokenKind.DOT,
                    value = ".",
                    span = Span(2, 3),
                    location = Location(1, 3)
                )
            ),
            Result.Error(
                LexicalError.InvalidChar(
                    char = '[',
                    location = Location(1, 4)
                )
            ),
            Result.Ok(
                Token(
                    kind = TokenKind.IDENT,
                    value = "name",
                    span = Span(4, 8),
                    location = Location(1, 5)
                )
            ),
            Result.Error(
                LexicalError.InvalidChar(
                    char = ']',
                    location = Location(1, 9)
                )
            ),
        )
        assertExpectedTokenResults(source, expectedTokenResults)
    }

    companion object {
        private fun assertExpectedTokens(source: String, expectedTokens: List<Token>) {
            assertExpectedTokenResults(source, expectedTokens.map { Result.Ok(it) })
        }

        private fun assertExpectedTokenResults(source: String, expectedResults: List<TokenResult>) {
            val actualResults = Lexer.tokenize(source).toList()
            assertEquals(expectedResults.size, actualResults.size)
            expectedResults.zip(actualResults).forEach { (expected, actual) ->
                assertEquals(expected, actual)
            }
        }
    }
}