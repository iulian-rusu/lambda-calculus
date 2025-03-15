package lambda.common.algorithm

import lambda.common.invoke
import lambda.common.lambda
import lambda.compiler.ast.Abstraction
import lambda.compiler.ast.Application
import lambda.compiler.ast.Term
import lambda.compiler.ast.Variable
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import kotlin.test.assertEquals

class BetaReducerTest {
    @Test
    fun `reducing a variable does nothing`() {
        assertEquals(Variable("x"), Variable("x").reduce())
    }

    @Test
    fun `reducing an irreducible abstraction does nothing`() {
        val program = Abstraction(Variable("x"), Variable("x"))
        assertEquals(program, program.reduce())
    }

    @Test
    fun `reducing an irreducible application does nothing`() {
        val program = Application(Variable("x"), Variable("y"))
        assertEquals(program, program.reduce())
    }

    @ParameterizedTest
    @MethodSource("getApplicationsAndExpectedReductions")
    fun `reducing an application returns the expected program`(initial: Term, expected: Term) {
        assertEquals(expected, initial.reduce())
    }

    companion object {
        @JvmStatic
        fun getApplicationsAndExpectedReductions(): List<Arguments> {
            val x = Variable("x")
            val y = Variable("y")
            val z = Variable("z")

            return listOf(
                Arguments.of(x(y), x(y)),
                Arguments.of(lambda(x) { x }(y), y),
                Arguments.of(lambda(x) { z }(y), z),
                Arguments.of(lambda(x) { x(y)(y) }(z), z(y)(y)),
                Arguments.of(lambda(x) { y(x)(y) }(z), y(z)(y)),
                Arguments.of(lambda(x) { y(x)(x) }(z), y(z)(z)),
                Arguments.of(lambda(x) { x(y) }(y), y(y)),
                Arguments.of(
                    lambda(x) { lambda(y) { x(y) } }(x(y)),
                    lambda(y) { x(y)(y) }
                ),
                Arguments.of(
                    lambda(x) { lambda(y) { y(y)(x) } }(y(z)),
                    lambda(y) { y(y)(y(z)) }
                ),
                Arguments.of(
                    lambda(x) { lambda(y) { x(y)(x) } }(z(x)),
                    lambda(y) { z(x)(y)(z(x)) }
                ),
                Arguments.of(
                    lambda(x) { lambda(y) { x(y) } }(lambda(x) { lambda(z) { x(y)(z) } }),
                    lambda(y) { (lambda(x) { lambda(z) { x(y)(z) } })(y) }
                ),
            )
        }
    }
}
