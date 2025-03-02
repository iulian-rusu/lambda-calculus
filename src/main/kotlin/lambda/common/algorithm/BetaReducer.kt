package lambda.common.algorithm

import lambda.compiler.ast.Application
import lambda.compiler.ast.Abstraction
import lambda.compiler.ast.Term
import lambda.compiler.ast.TermTransformer

/**
 * Applies a single step of β-reduction on a term.
 * Beta reduction is equivalent to the concept of function application.
 */
data object BetaReducer : TermTransformer {
    override fun visitApplication(app: Application) = when (app.target) {
        is Abstraction -> substitute(app.target, app.arg)
        else -> app
    }

    private fun substitute(abs: Abstraction, arg: Term) = abs.body.substitute(abs.param, arg)
}

fun Term.reduce() = accept(BetaReducer)
