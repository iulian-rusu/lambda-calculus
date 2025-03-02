package lambda.common.algorithm

import lambda.compiler.ast.Abstraction
import lambda.compiler.ast.Application
import lambda.compiler.ast.Symbol
import lambda.compiler.ast.Term
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import kotlin.test.assertEquals

class SerializerTest {
    @Test
    fun `serializing a symbol returns expected results`() {
        assertEquals("x", Symbol("x").serialize())
        assertEquals("test", Symbol("test").serialize())
    }

    @Test
    fun `serializing an abstraction returns expected results`() {
        val term = Abstraction(Symbol("x"), Symbol("x"))
        assertEquals("λx.x", term.serialize())
    }

    @Test
    fun `serializing an application returns expected results`() {
        val term = Application(Symbol("x"), Symbol("x"))
        assertEquals("x x", term.serialize())
    }

    @ParameterizedTest
    @MethodSource("getTermsAndExpectedSerializations")
    fun `serializing a term returns expected results`(term: Term, expected: String) {
        assertEquals(expected, term.serialize())
    }

    companion object {
        @JvmStatic
        fun getTermsAndExpectedSerializations() = listOf(
            Arguments.of(
                Application(
                    Abstraction(Symbol("x"), Symbol("x")),
                    Symbol("y")
                ),
                "(λx.x) y"
            ),
            Arguments.of(
                Abstraction(
                    Symbol("x"),
                    Abstraction(
                        Symbol("y"),
                        Application(
                            Symbol("x"),
                            Application(
                                Symbol("x"),
                                Symbol("y")
                            )
                        )
                    ),
                ),
                "λx.λy.x (x y)"
            ),
            Arguments.of(
                Abstraction(
                    Symbol("x"),
                    Abstraction(
                        Symbol("y"),
                        Application(
                            Application(
                                Symbol("x"),
                                Symbol("x")
                            ),
                            Symbol("y")
                        )
                    ),
                ),
                "λx.λy.x x y"
            ),
            Arguments.of(
                Application(
                    Abstraction(
                        Symbol("x"),
                        Abstraction(
                            Symbol("y"),
                            Application(Symbol("x"), Symbol("y"))
                        )
                    ),
                    Symbol("z")
                ),
                "(λx.λy.x y) z"
            ),
            Arguments.of(
                Application(
                    Abstraction(
                        Symbol("x1"),
                        Abstraction(
                            Symbol("x2"),
                            Application(Symbol("x1"), Symbol("x2"))
                        )
                    ),
                    Abstraction(
                        Symbol("x3"),
                        Application(Symbol("x3"), Symbol("x3"))
                    )
                ),
                "(λx1.λx2.x1 x2) (λx3.x3 x3)"
            ),
            Arguments.of(
                Application(
                    Application(
                        Abstraction(
                            Symbol("x1"),
                            Abstraction(
                                Symbol("x2"),
                                Application(Symbol("x1"), Symbol("x2"))
                            )
                        ),
                        Abstraction(
                            Symbol("x3"),
                            Symbol("x3")
                        )
                    ),
                    Symbol("x4")
                ),
                "(λx1.λx2.x1 x2) (λx3.x3) x4"
            ),
            Arguments.of(
                Application(
                    Abstraction(
                        Symbol("x1"),
                        Abstraction(
                            Symbol("x2"),
                            Application(Symbol("x1"), Symbol("x2"))
                        )
                    ),
                    Application(
                        Abstraction(
                            Symbol("x3"),
                            Symbol("x3")
                        ),
                        Symbol("x4")
                    )
                ),
                "(λx1.λx2.x1 x2) ((λx3.x3) x4)"
            ),
            Arguments.of(
                Application(
                    Abstraction(
                        Symbol("x1"),
                        Symbol("x1")
                    ),
                    Application(
                        Application(
                            Abstraction(
                                Symbol("x2"),
                                Abstraction(
                                    Symbol("x3"),
                                    Abstraction(
                                        Symbol("x4"),
                                        Application(
                                            Symbol("x2"),
                                            Application(
                                                Symbol("x3"),
                                                Symbol("x4")
                                            )
                                        )
                                    )
                                )
                            ),
                            Abstraction(
                                Symbol("x5"),
                                Symbol("x5")
                            ),
                        ),
                        Abstraction(
                            Symbol("x6"),
                            Symbol("x6")
                        ),
                    )
                ),
                "(λx1.x1) ((λx2.λx3.λx4.x2 (x3 x4)) (λx5.x5) (λx6.x6))"
            )
        )
    }
}
