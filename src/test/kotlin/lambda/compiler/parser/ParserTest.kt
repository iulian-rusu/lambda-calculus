package lambda.compiler.parser

import lambda.compiler.ast.Abstraction
import lambda.compiler.ast.Application
import lambda.compiler.ast.Term
import lambda.compiler.ast.Variable
import lambda.compiler.common.Location
import lambda.compiler.common.Span
import lambda.compiler.diagnostic.LexicalError
import lambda.compiler.diagnostic.Result
import lambda.compiler.diagnostic.SourceAnalysisError
import lambda.compiler.diagnostic.SyntaxError
import lambda.compiler.diagnostic.TokenKindExpectation
import lambda.compiler.lexer.Token
import lambda.compiler.lexer.TokenKind
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import kotlin.test.assertEquals

class ParserTest {
    @Test
    fun `parsing an identifier returns a variable`() {
        val result = Parser("test").parse()
        val expected = Result.Ok(Variable("test"))
        assertEquals(expected, result)
    }

    @ParameterizedTest
    @MethodSource("getApplicationSourcesAndExpectedTerms")
    fun `parsing adjacent identifiers returns an application`(source: String, expected: Term) {
        val result = Parser(source).parse()
        assertEquals(Result.Ok(expected), result)
    }

    @ParameterizedTest
    @MethodSource("getAbstractionSourcesAndExpectedTerms")
    fun `parsing an abstraction returns the expected term`(source: String, expected: Term) {
        val result = Parser(source).parse()
        assertEquals(Result.Ok(expected), result)
    }

    @ParameterizedTest
    @MethodSource("getInvalidSourcesAndExpectedErrors")
    fun `parsing an invalid source returns an error`(source: String, error: SourceAnalysisError) {
        val result = Parser(source).parse()
        assertEquals(Result.Error(error), result)
    }

    companion object {
        @JvmStatic
        fun getApplicationSourcesAndExpectedTerms() = listOf(
            Arguments.of(
                "x y",
                Application(Variable("x"), Variable("y"))
            ),
            Arguments.of(
                "(x y)",
                Application(Variable("x"), Variable("y"))
            ),
            Arguments.of(
                "x (y)",
                Application(Variable("x"), Variable("y"))
            ),
            Arguments.of(
                "(x) y",
                Application(Variable("x"), Variable("y"))
            ),
            Arguments.of(
                "((x) (y))",
                Application(Variable("x"), Variable("y"))
            ),
            Arguments.of(
                "x y z",
                Application(
                    Application(Variable("x"), Variable("y")),
                    Variable("z")
                )
            ),
            Arguments.of(
                "x (y z)",
                Application(
                    Variable("x"),
                    Application(Variable("y"), Variable("z")),
                )
            ),
            Arguments.of(
                "x1 x2 x3 x4",
                Application(
                    Application(
                        Application(Variable("x1"), Variable("x2")),
                        Variable("x3")
                    ),
                    Variable("x4")
                )
            ),
            Arguments.of(
                "x1 (x2 x3) x4",
                Application(
                    Application(
                        Variable("x1"),
                        Application(Variable("x2"), Variable("x3")),
                    ),
                    Variable("x4")
                )
            ),
            Arguments.of(
                "x1 (x2 (x3 x4))",
                Application(
                    Variable("x1"),
                    Application(
                        Variable("x2"),
                        Application(Variable("x3"), Variable("x4")),
                    ),
                )
            ),
            Arguments.of(
                "(x1 x2) (x3 x4)",
                Application(
                    Application(Variable("x1"), Variable("x2")),
                    Application(Variable("x3"), Variable("x4")),
                )
            ),
        )

        @JvmStatic
        fun getAbstractionSourcesAndExpectedTerms() = listOf(
            Arguments.of(
                "\\x.x",
                Abstraction(Variable("x"), Variable("x"))
            ),
            Arguments.of(
                "(\\x.x)",
                Abstraction(Variable("x"), Variable("x"))
            ),
            Arguments.of(
                "(\\x.x)y",
                Application(
                    Abstraction(
                        Variable("x"),
                        Variable("x")
                    ),
                    Variable("y")
                )
            ),
            Arguments.of(
                "\\x.(x)",
                Abstraction(Variable("x"), Variable("x"))
            ),
            Arguments.of(
                "\\x.((x))",
                Abstraction(Variable("x"), Variable("x"))
            ),
            Arguments.of(
                "\\x.(((x)))",
                Abstraction(Variable("x"), Variable("x"))
            ),
            Arguments.of(
                "\\x.(((x)))y",
                Abstraction(
                    Variable("x"),
                    Application(Variable("x"), Variable("y")),
                )
            ),
            Arguments.of(
                "\\x1.x1 x2",
                Abstraction(
                    Variable("x1"),
                    Application(Variable("x1"), Variable("x2")),
                )
            ),
            Arguments.of(
                "\\x1.\\x2.x1 x2 \\x3.x3",
                Abstraction(
                    Variable("x1"),
                    Abstraction(
                        Variable("x2"),
                        Application(
                            Application(
                                Variable("x1"),
                                Variable("x2")
                            ),
                            Abstraction(
                                Variable("x3"),
                                Variable("x3")
                            )
                        ),
                    )
                )
            ),
            Arguments.of(
                "\\x1. (\\x2.x1 x2) \\x3.x3",
                Abstraction(
                    Variable("x1"),
                    Application(
                        Abstraction(
                            Variable("x2"),
                            Application(
                                Variable("x1"),
                                Variable("x2")
                            ),
                        ),
                        Abstraction(
                            Variable("x3"),
                            Variable("x3")
                        )
                    )
                )
            ),
            Arguments.of(
                "\\x1.\\x2.x1 (x2 \\x3.x3)",
                Abstraction(
                    Variable("x1"),
                    Abstraction(
                        Variable("x2"),
                        Application(
                            Variable("x1"),
                            Application(
                                Variable("x2"),
                                Abstraction(
                                    Variable("x3"),
                                    Variable("x3")
                                )
                            )
                        ),
                    )
                )
            ),
            Arguments.of(
                "(\\x1.(\\x2.x1 (x2 \\x3.(x3)))x1)y",
                Application(
                    Abstraction(
                        Variable("x1"),
                        Application(
                            Abstraction(
                                Variable("x2"),
                                Application(
                                    Variable("x1"),
                                    Application(
                                        Variable("x2"),
                                        Abstraction(
                                            Variable("x3"),
                                            Variable("x3")
                                        )
                                    )
                                ),
                            ),
                            Variable("x1")
                        )
                    ),
                    Variable("y")
                ),
            )
        )

        @JvmStatic
        fun getInvalidSourcesAndExpectedErrors() = listOf<Arguments>(
            Arguments.of(
                "",
                SyntaxError.UnexpectedEndOfTerm(
                    expectation = Parser.START_OF_TERM_EXPECTATION,
                    location = Location(1, 1)
                )
            ),
            Arguments.of(
                "(",
                SyntaxError.UnexpectedEndOfTerm(
                    expectation = Parser.START_OF_TERM_EXPECTATION,
                    location = Location(1, 2)
                )
            ),
            Arguments.of(
                ")",
                SyntaxError.UnexpectedToken(
                    expectation = Parser.START_OF_TERM_EXPECTATION,
                    actualToken = Token(
                        kind = TokenKind.RIGHT_PAREN,
                        value = ")",
                        span = Span(0, 1),
                        location = Location(1, 1)
                    )
                )
            ),
            Arguments.of(
                "()",
                SyntaxError.UnexpectedEndOfTerm(
                    expectation = Parser.START_OF_TERM_EXPECTATION,
                    location = Location(1, 2)
                )
            ),
            Arguments.of(
                "\\",
                SyntaxError.UnexpectedEndOfTerm(
                    expectation = TokenKindExpectation.of(TokenKind.IDENT),
                    location = Location(1, 2)
                )
            ),
            Arguments.of(
                "\\x",
                SyntaxError.UnexpectedEndOfTerm(
                    expectation = TokenKindExpectation.of(TokenKind.DOT),
                    location = Location(1, 3)
                )
            ),
            Arguments.of(
                "\\x.",
                SyntaxError.UnexpectedEndOfTerm(
                    expectation = Parser.START_OF_TERM_EXPECTATION,
                    location = Location(1, 4)
                )
            ),
            Arguments.of(
                ".",
                SyntaxError.UnexpectedToken(
                    expectation = Parser.START_OF_TERM_EXPECTATION,
                    actualToken = Token(
                        kind = TokenKind.DOT,
                        value = ".",
                        span = Span(0, 1),
                        location = Location(1, 1)
                    )
                )
            ),
            Arguments.of(
                "x.",
                SyntaxError.UnexpectedToken(
                    expectation = Parser.START_OF_TERM_EXPECTATION,
                    actualToken = Token(
                        kind = TokenKind.DOT,
                        value = ".",
                        span = Span(1, 2),
                        location = Location(1, 2)
                    )
                )
            ),
            Arguments.of(
                "@",
                LexicalError.InvalidChar(
                    '@',
                    Location(1, 1)
                )
            ),
            Arguments.of(
                "(x",
                SyntaxError.UnexpectedEndOfTerm(
                    expectation = TokenKindExpectation.of(TokenKind.RIGHT_PAREN),
                    location = Location(1, 3)
                )
            ),
            Arguments.of(
                "x)",
                SyntaxError.UnexpectedToken(
                    expectation = Parser.START_OF_TERM_EXPECTATION,
                    actualToken = Token(
                        kind = TokenKind.RIGHT_PAREN,
                        value = ")",
                        span = Span(1, 2),
                        location = Location(1, 2)
                    )
                )
            ),
            Arguments.of(
                "(x(y)((z)((x(y)))",
                SyntaxError.UnexpectedEndOfTerm(
                    expectation = TokenKindExpectation.of(TokenKind.RIGHT_PAREN),
                    location = Location(1, 18)
                )
            ),
            Arguments.of(
                "\\)x.x",
                SyntaxError.UnexpectedToken(
                    expectation = TokenKindExpectation.of(TokenKind.IDENT),
                    actualToken = Token(
                        kind = TokenKind.RIGHT_PAREN,
                        value = ")",
                        span = Span(1, 2),
                        location = Location(1, 2)
                    )
                )
            ),
            Arguments.of(
                "(\\x y",
                SyntaxError.UnexpectedToken(
                    expectation = TokenKindExpectation.of(TokenKind.DOT),
                    actualToken = Token(
                        kind = TokenKind.IDENT,
                        value = "y",
                        span = Span(4, 5),
                        location = Location(1, 5)
                    )
                )
            ),
            Arguments.of(
                "(\\x y)",
                SyntaxError.UnexpectedToken(
                    expectation = TokenKindExpectation.of(TokenKind.DOT),
                    actualToken = Token(
                        kind = TokenKind.IDENT,
                        value = "y",
                        span = Span(4, 5),
                        location = Location(1, 5)
                    )
                )
            ),
            Arguments.of(
                "(\\x.)",
                SyntaxError.UnexpectedEndOfTerm(
                    expectation = Parser.START_OF_TERM_EXPECTATION,
                    location = Location(1, 5)
                )
            ),
            Arguments.of(
                "\\x.)",
                SyntaxError.UnexpectedToken(
                    expectation = Parser.START_OF_TERM_EXPECTATION,
                    actualToken = Token(
                        kind = TokenKind.RIGHT_PAREN,
                        value = ")",
                        span = Span(3, 4),
                        location = Location(1, 4)
                    )
                )
            ),
            Arguments.of(
                "(\\x )",
                SyntaxError.UnexpectedToken(
                    expectation = TokenKindExpectation.of(TokenKind.DOT),
                    actualToken = Token(
                        kind = TokenKind.RIGHT_PAREN,
                        value = ")",
                        span = Span(4, 5),
                        location = Location(1, 5)
                    )
                )
            ),
            Arguments.of(
                "(\\ )",
                SyntaxError.UnexpectedToken(
                    expectation = TokenKindExpectation.of(TokenKind.IDENT),
                    actualToken = Token(
                        kind = TokenKind.RIGHT_PAREN,
                        value = ")",
                        span = Span(3, 4),
                        location = Location(1, 4)
                    )
                )
            ),
            Arguments.of(
                "(. )",
                SyntaxError.UnexpectedToken(
                    expectation = Parser.START_OF_TERM_EXPECTATION,
                    actualToken = Token(
                        kind = TokenKind.DOT,
                        value = ".",
                        span = Span(1, 2),
                        location = Location(1, 2)
                    )
                )
            ),
        )
    }
}
