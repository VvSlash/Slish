package pl.edu.pw.slish.ast.expr;

import pl.edu.pw.slish.ast.NodeVisitor;
import pl.edu.pw.slish.ast.PipeElementNode;

/**
 * Reprezentuje operację rzutowania typu w potoku.
 * Przechowuje informację o typie docelowym.
 * Wartość do rzutowania będzie pochodzić z poprzedniego elementu potoku.
 */
public class TypeCastPipeExpression implements PipeElementNode, Expression {
    private final String targetType; // Nazwa typu docelowego, np. "float", "int"

    public TypeCastPipeExpression(String targetType) {
        if (targetType == null || targetType.trim().isEmpty()) {
            throw new IllegalArgumentException("Target type cannot be null or empty for TypeCastPipeExpression");
        }
        this.targetType = targetType;
    }

    public String getTargetType() {
        return targetType;
    }

    @Override
    public <T> T accept(NodeVisitor<T> visitor) {
        // Wymaga implementacji metody visit(TypeCastPipeExpression) w NodeVisitor
        return visitor.visit(this);
    }

    @Override
    public String toString() {
        return "/" + targetType;
    }
}
