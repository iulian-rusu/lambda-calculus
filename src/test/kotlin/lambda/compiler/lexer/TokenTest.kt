package lambda.compiler.lexer

import lambda.compiler.common.Location
import lambda.compiler.common.Span
import kotlin.test.Test
import kotlin.test.assertEquals

class TokenTest {
    @Test
    fun `displaying a token returns the expected string`() {
        val token = Token(
            kind = TokenKind.IDENT,
            value = "some_identifier",
            span = Span(20, 35),
            location = Location(15, 23)
        )

        assertEquals("15:23 [20..35]=IDENT('some_identifier')", token.display())
    }
}
