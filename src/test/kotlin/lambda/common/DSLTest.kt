package lambda.common

import lambda.compiler.ast.Abstraction
import lambda.compiler.ast.Application
import lambda.compiler.ast.Symbol
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import kotlin.test.Test
import kotlin.test.assertEquals

class DSLTest {
    @Test
    fun `creating a lambda binds terms correctly`() {
        val actual = lambda("x") { Symbol("x") }
        val expected = Abstraction(Symbol("x"), Symbol("x"))
        assertEquals(expected, actual)
    }

    @Test
    fun `currying lambdas binds terms correctly`() {
        val actual = lambda("x") { lambda("y") { lambda("z") { Symbol("z") } } }
        val expected = Abstraction(
            Symbol("x"),
            Abstraction(
                Symbol("y"),
                Abstraction(Symbol("z"), Symbol("z"))
            )
        )
        assertEquals(expected, actual)
    }

    @ParameterizedTest
    @MethodSource("getApplicationsAndExpectedResults")
    fun `applying terms associates calls correctly`(actual: Application, expected: Application) {
        assertEquals(expected, actual)
    }

    companion object {
        @JvmStatic
        fun getApplicationsAndExpectedResults(): List<Arguments> {
            val x = Symbol("x")
            return listOf(
                Arguments.of(x(x), Application(x, x)),
                Arguments.of((x)(x), Application(x, x)),
                Arguments.of(x(x)(x), Application(Application(x, x), x)),
                Arguments.of((x(x))(x), Application(Application(x, x), x)),
                Arguments.of(x(x(x)), Application(x, Application(x, x))),
                Arguments.of(
                    x(x(x)(x))(x(x))(x),
                    Application(
                        Application(
                            Application(
                                x,
                                Application(Application(x, x), x)
                            ),
                            Application(x, x)
                        ),
                        x
                    )
                ),
            )
        }
    }
}