package pl.edu.pw.slish.ast.expr;

import pl.edu.pw.slish.ast.NodeVisitor;
import pl.edu.pw.slish.ast.PipeElementNode;

/**
 * Reprezentuje wyrażenie (Expression) używane jako element w potoku.
 * Ta klasa opakowuje standardowe wyrażenie, aby mogło być traktowane jako PipeElementNode.
 */
public class ExpressionAsPipeElement implements PipeElementNode, Expression {
    private final Expression expression;

    public ExpressionAsPipeElement(Expression expression) {
        if (expression == null) {
            throw new IllegalArgumentException("Expression cannot be null for ExpressionAsPipeElement");
        }
        this.expression = expression;
    }

    public Expression getExpression() {
        return expression;
    }

    @Override
    public <T> T accept(NodeVisitor<T> visitor) {
        // Może wymagać specjalnej obsługi w visitorze lub delegować do opakowanego wyrażenia
        // Na przykład: return visitor.visit(this.expression);
        // Lub, jeśli NodeVisitor ma metodę visit(ExpressionAsPipeElement):
        return visitor.visit(this);
    }

    @Override
    public String toString() {
        return expression.toString();
    }
}
