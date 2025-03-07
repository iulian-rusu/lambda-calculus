package lambda.debrujin.ast

/**
 * Visitor for processing a de Brujin indexing.
 *
 * @param R The result of visiting
 */
interface DeBrujinVisitor<R> {
    fun visitIndex(index: DeBrujinIndex): R
    fun visitConstant(constant: DeBrujinConstant): R
    fun visitBinder(binder: DeBrujinBinder): R
    fun visitApplication(app: DeBrujinApplication): R
}
