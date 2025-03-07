package lambda.common.algorithm

import lambda.compiler.ast.Abstraction
import lambda.compiler.ast.Application
import lambda.compiler.ast.Term
import lambda.compiler.ast.Variable
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import kotlin.test.assertEquals

class SerializerTest {
    @Test
    fun `serializing a variable returns expected results`() {
        assertEquals("x", Variable("x").serialize())
        assertEquals("test", Variable("test").serialize())
    }

    @Test
    fun `serializing an abstraction returns expected results`() {
        val term = Abstraction(Variable("x"), Variable("x"))
        assertEquals("λx.x", term.serialize())
    }

    @Test
    fun `serializing an application returns expected results`() {
        val term = Application(Variable("x"), Variable("x"))
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
                    Abstraction(Variable("x"), Variable("x")),
                    Variable("y")
                ),
                "(λx.x) y"
            ),
            Arguments.of(
                Abstraction(
                    Variable("x"),
                    Abstraction(
                        Variable("y"),
                        Application(
                            Variable("x"),
                            Application(
                                Variable("x"),
                                Variable("y")
                            )
                        )
                    ),
                ),
                "λx.λy.x (x y)"
            ),
            Arguments.of(
                Abstraction(
                    Variable("x"),
                    Abstraction(
                        Variable("y"),
                        Application(
                            Application(
                                Variable("x"),
                                Variable("x")
                            ),
                            Variable("y")
                        )
                    ),
                ),
                "λx.λy.x x y"
            ),
            Arguments.of(
                Application(
                    Abstraction(
                        Variable("x"),
                        Abstraction(
                            Variable("y"),
                            Application(Variable("x"), Variable("y"))
                        )
                    ),
                    Variable("z")
                ),
                "(λx.λy.x y) z"
            ),
            Arguments.of(
                Application(
                    Abstraction(
                        Variable("x1"),
                        Abstraction(
                            Variable("x2"),
                            Application(Variable("x1"), Variable("x2"))
                        )
                    ),
                    Abstraction(
                        Variable("x3"),
                        Application(Variable("x3"), Variable("x3"))
                    )
                ),
                "(λx1.λx2.x1 x2) (λx3.x3 x3)"
            ),
            Arguments.of(
                Application(
                    Application(
                        Abstraction(
                            Variable("x1"),
                            Abstraction(
                                Variable("x2"),
                                Application(Variable("x1"), Variable("x2"))
                            )
                        ),
                        Abstraction(
                            Variable("x3"),
                            Variable("x3")
                        )
                    ),
                    Variable("x4")
                ),
                "(λx1.λx2.x1 x2) (λx3.x3) x4"
            ),
            Arguments.of(
                Application(
                    Abstraction(
                        Variable("x1"),
                        Abstraction(
                            Variable("x2"),
                            Application(Variable("x1"), Variable("x2"))
                        )
                    ),
                    Application(
                        Abstraction(
                            Variable("x3"),
                            Variable("x3")
                        ),
                        Variable("x4")
                    )
                ),
                "(λx1.λx2.x1 x2) ((λx3.x3) x4)"
            ),
            Arguments.of(
                Application(
                    Abstraction(
                        Variable("x1"),
                        Variable("x1")
                    ),
                    Application(
                        Application(
                            Abstraction(
                                Variable("x2"),
                                Abstraction(
                                    Variable("x3"),
                                    Abstraction(
                                        Variable("x4"),
                                        Application(
                                            Variable("x2"),
                                            Application(
                                                Variable("x3"),
                                                Variable("x4")
                                            )
                                        )
                                    )
                                )
                            ),
                            Abstraction(
                                Variable("x5"),
                                Variable("x5")
                            ),
                        ),
                        Abstraction(
                            Variable("x6"),
                            Variable("x6")
                        ),
                    )
                ),
                "(λx1.x1) ((λx2.λx3.λx4.x2 (x3 x4)) (λx5.x5) (λx6.x6))"
            )
        )
    }
}
