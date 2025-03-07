package lambda.debrujin.algorithm

import lambda.debrujin.ast.DeBrujinApplication
import lambda.debrujin.ast.DeBrujinBinder
import lambda.debrujin.ast.DeBrujinConstant
import lambda.debrujin.ast.DeBrujinIndex
import lambda.debrujin.ast.DeBrujinTerm
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import kotlin.test.assertEquals

class DeBrujinSerializerTest {
    @Test
    fun `serializing an index returns expected results`() {
        assertEquals("1", DeBrujinIndex(1).serialize())
        assertEquals("12", DeBrujinIndex(12).serialize())
    }

    @Test
    fun `serializing a constant returns expected results`() {
        assertEquals("x", DeBrujinConstant("x").serialize())
        assertEquals("test", DeBrujinConstant("test").serialize())
        assertEquals("'12'", DeBrujinConstant("12").serialize())
    }

    @Test
    fun `serializing an application returns expected results`() {
        val term = DeBrujinApplication(DeBrujinIndex(1), DeBrujinConstant("x"))
        assertEquals("1 x", term.serialize())
    }

    @ParameterizedTest
    @MethodSource("getTermsAndExpectedSerializations")
    fun `serializing a term returns expected results`(term: DeBrujinTerm, expected: String) {
        assertEquals(expected, term.serialize())
    }

    companion object {
        @JvmStatic
        fun getTermsAndExpectedSerializations() = listOf(
            Arguments.of(
                DeBrujinApplication(
                    DeBrujinBinder(DeBrujinIndex(1)),
                    DeBrujinConstant("y")
                ),
                "(λ 1) y"
            ),
            Arguments.of(
                DeBrujinBinder(
                    DeBrujinBinder(
                        DeBrujinApplication(
                            DeBrujinIndex(2),
                            DeBrujinApplication(
                                DeBrujinIndex(2),
                                DeBrujinIndex(1)
                            )
                        )
                    ),
                ),
                "λ λ 2 (2 1)"
            ),
            Arguments.of(
                DeBrujinApplication(
                    DeBrujinBinder(
                        DeBrujinBinder(
                            DeBrujinApplication(DeBrujinIndex(2), DeBrujinIndex(1))
                        )
                    ),
                    DeBrujinBinder(
                        DeBrujinApplication(DeBrujinIndex(1), DeBrujinIndex(1))
                    )
                ),
                "(λ λ 2 1) (λ 1 1)"
            ),
            Arguments.of(
                DeBrujinApplication(
                    DeBrujinBinder(DeBrujinIndex(1)),
                    DeBrujinApplication(
                        DeBrujinApplication(
                            DeBrujinBinder(
                                DeBrujinBinder(
                                    DeBrujinBinder(
                                        DeBrujinApplication(
                                            DeBrujinIndex(3),
                                            DeBrujinApplication(
                                                DeBrujinIndex(2),
                                                DeBrujinIndex(1),
                                            )
                                        )
                                    )
                                )
                            ),
                            DeBrujinBinder(DeBrujinIndex(1),),
                        ),
                        DeBrujinBinder(DeBrujinIndex(1),),
                    )
                ),
                "(λ 1) ((λ λ λ 3 (2 1)) (λ 1) (λ 1))"
            )
        )
    }
}