package lambda.debrujin.ast

/**
 * Generic term of a de Brujin indexing.
 */
sealed interface DeBrujinTerm {
    fun <R> accept(visitor: DeBrujinVisitor<R>): R
}

/**
 * Equivalent of a bound variable in the de Brujin form.
 */
data class DeBrujinIndex(val value: Int) : DeBrujinTerm {
    override fun <R> accept(visitor: DeBrujinVisitor<R>) = visitor.visitIndex(this)
}

/**
 * Equivalent of a free variable in the de Brujin form.
 */
data class DeBrujinConstant(val value: String) : DeBrujinTerm {
    override fun <R> accept(visitor: DeBrujinVisitor<R>) = visitor.visitConstant(this)
}

/**
 * Equivalent of an abstraction in the de Brujin form.
 * Unlike abstractions, de Brujin binders do not require specifying a parameter.
 */
data class DeBrujinBinder(val expr: DeBrujinTerm) : DeBrujinTerm {
    override fun <R> accept(visitor: DeBrujinVisitor<R>) = visitor.visitBinder(this)
}

/**
 * Equivalent of an application in the de Brujin form.
 */
data class DeBrujinApplication(val target: DeBrujinTerm, val arg: DeBrujinTerm) : DeBrujinTerm {
    override fun <R> accept(visitor: DeBrujinVisitor<R>) = visitor.visitApplication(this)
}
