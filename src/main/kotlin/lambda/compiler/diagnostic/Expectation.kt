package lambda.compiler.diagnostic

import lambda.compiler.lexer.TokenKind
import java.util.EnumSet

sealed interface Expectation

data class TokenKindExpectation(val expectedKinds: EnumSet<TokenKind>) : Expectation, Set<TokenKind> by expectedKinds {
    companion object {
        fun any() = TokenKindExpectation(EnumSet.allOf(TokenKind::class.java))
        fun of(first: TokenKind, vararg rest: TokenKind) = TokenKindExpectation(EnumSet.of(first, *rest))
    }
}
