package lambda.debrujin

import lambda.compiler.ast.Application
import lambda.compiler.ast.Abstraction
import lambda.compiler.ast.Variable
import lambda.compiler.ast.Term
import lambda.compiler.ast.TermVisitor
import lambda.debrujin.ast.DeBrujinApplication
import lambda.debrujin.ast.DeBrujinBinder
import lambda.debrujin.ast.DeBrujinConstant
import lambda.debrujin.ast.DeBrujinIndex
import lambda.debrujin.ast.DeBrujinTerm

/**
 * Generates the de Brujin indexing of a lambda term.
 *
 * @see Term
 * @see DeBrujinTerm
 * @see <a href="https://en.wikipedia.org/wiki/De_Bruijn_index">de Bruijn index (Wikipedia)</a>
 */
class DeBrujinIndexer(private val indices: Map<Variable, Int>) : TermVisitor<DeBrujinTerm> {
    override fun visitVariable(variable: Variable) =
        indices[variable]?.let { DeBrujinIndex(it) } ?: DeBrujinConstant(variable.name)

    override fun visitAbstraction(abs: Abstraction) = DeBrujinBinder(
        abs.body.accept(innerScopeIndexer(abs))
    )

    override fun visitApplication(app: Application) = DeBrujinApplication(
        target = app.target.accept(this),
        arg = app.arg.accept(this)
    )

    private fun innerScopeIndexer(abs: Abstraction): DeBrujinIndexer {
        val innerScopeIndices = indices.mapValuesTo(mutableMapOf()) { it.value + 1 }
        innerScopeIndices[abs.param] = 1
        return DeBrujinIndexer(innerScopeIndices)
    }
}

fun Term.deBrujin() = accept(DeBrujinIndexer(mapOf()))
