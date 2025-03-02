package lambda.common.algorithm

import lambda.compiler.ast.Abstraction
import lambda.compiler.ast.Symbol
import lambda.compiler.ast.Term
import lambda.compiler.ast.TermTransformer
import lambda.compiler.ast.binds

/**
 * Substitutes a symbol with a term.
 *
 * This takes into acount name shadowing and name collisions.
 * That is, if the target symbol is shadowed by an abstraction parameter,
 * substitution will not happen in the body of the abstraction.
 *
 * @param target        The target symbol to be substituted
 * @param replacement   The replacement value
 */
data class Substitutor(val target: Symbol, val replacement: Term) : TermTransformer {
    override fun visitSymbol(sym: Symbol) = if (sym == target) replacement else sym

    override fun visitAbstraction(abs: Abstraction) = when {
        // Target is shadowed by the abstraction's argument
        abs.binds(target) -> abs
        // TODO: handle name collisions -> transform term to canonical form
        else -> Abstraction(
            param = abs.param,
            body = abs.body.accept(this)
        )
    }
}

fun Term.substitute(target: Symbol, replacement: Term) = accept(
    Substitutor(
        target = target,
        replacement = replacement
    )
)
