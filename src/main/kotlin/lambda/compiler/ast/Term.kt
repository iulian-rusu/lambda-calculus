package lambda.compiler.ast

/**
 * Generic term of a Lambda Calculus program.
 */
sealed interface Term {
    fun <T> accept(visitor: TermVisitor<T>): T
}

/**
 * A symbol is a named variable.
 *
 * @param name  The name of the symbol
 */
data class Symbol(val name: String) : Term {
    override fun <T> accept(visitor: TermVisitor<T>) = visitor.visitSymbol(this)
}

/**
 *  An abstraction is a function definition, taking some bound symbol as input and returning the function body.
 *
 *  @param param    The bound symbol
 *  @param body     The returned body
 */
data class Abstraction(val param: Symbol, val body: Term) : Term {
    override fun <T> accept(visitor: TermVisitor<T>) = visitor.visitAbstraction(this)
}

/**
 * Returns true if the abstraction binds the symbol.
 * A symbol is bound by an abstraction if it has the same name as its parameter.
 */
fun Abstraction.binds(sym: Symbol) = param == sym

/**
 * An application is a function call.
 *
 * @param target    The target of the call (calee)
 * @param arg       The argument of the call
 */
data class Application(val target: Term, val arg: Term) : Term {
    override fun <T> accept(visitor: TermVisitor<T>) = visitor.visitApplication(this)
}
