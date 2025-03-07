package lambda.common.algorithm

import lambda.compiler.ast.Abstraction
import lambda.compiler.ast.Variable
import lambda.compiler.ast.Term
import lambda.compiler.ast.TermTransformer
import lambda.compiler.ast.binds

/**
 * Replaces all non-shadowed occurences of a variable with a term.
 *
 * This takes into acount name shadowing and name collisions.
 * That is, if the target variable is shadowed by an abstraction parameter,
 * replacement will not happen in the body of the abstraction.
 *
 * @param target        The target symbol to be replaced
 * @param replacement   The replacement value
 */
data class Replacer(val target: Variable, val replacement: Term) : TermTransformer {
    override fun visitVariable(variable: Variable) = if (variable == target) replacement else variable

    override fun visitAbstraction(abs: Abstraction) = when {
        abs.binds(target) -> abs
        else -> Abstraction(
            param = abs.param,
            body = abs.body.accept(this)
        )
    }
}

fun Term.replace(target: Variable, replacement: Term) = accept(
    Replacer(
        target = target,
        replacement = replacement
    )
)
