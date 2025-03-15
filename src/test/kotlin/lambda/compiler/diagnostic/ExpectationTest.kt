package lambda.compiler.diagnostic

import lambda.compiler.lexer.TokenKind
import org.junit.jupiter.api.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ExpectationTest {
    @Test
    fun `expectations for any token kind include all token kinds`() {
        val expectation = TokenKindExpectation.any()
        TokenKind.entries.forEach {
            assertTrue(it in expectation)
        }
    }

    @Test
    fun `expectations for some token kinds include only those token kinds`() {
        val expectedKinds = arrayOf(TokenKind.DOT, TokenKind.IDENT)
        val expectation = TokenKindExpectation.of(expectedKinds[0], *expectedKinds)

        expectedKinds.forEach {
            assertTrue(it in expectation)
        }

        TokenKind.entries
            .filter { it !in expectedKinds }
            .forEach {
                assertFalse(it in expectation)
            }
    }
}