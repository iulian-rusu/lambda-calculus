package lambda.compiler.ast

/**
 * Visitor for processing a Lambda Calculus term.
 *
 * @param R The result of visiting
 */
interface TermVisitor<R> {
    fun visitSymbol(sym: Symbol): R
    fun visitAbstraction(abs: Abstraction): R
    fun visitApplication(app: Application): R
}

/**
 * TermVisitor that produces another Lambda Calculus term.
 */
interface TermTransformer : TermVisitor<Term> {
    override fun visitSymbol(sym: Symbol): Term = sym

    override fun visitAbstraction(abs: Abstraction): Term = Abstraction(
        param = abs.param,
        body = abs.body.accept(this)
    )

    override fun visitApplication(app: Application): Term = Application(
        target = app.target.accept(this),
        arg = app.arg.accept(this)
    )
}
