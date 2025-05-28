package pl.edu.pw.slish.ast.expr;

import pl.edu.pw.slish.ast.NodeVisitor;

public class UnaryOperation implements Expression {
    public enum Operator { NEG };

    private final Operator operator;
    private final Expression operand;

    public UnaryOperation(Operator operator, Expression operand) {
        this.operator = operator;
        this.operand = operand;
    }

    public Operator getOperator() {
        return operator;
    }

    public Expression getOperand() {
        return operand;
    }

    @Override
    public <T> T accept(NodeVisitor<T> visitor) {
        return visitor.visit(this);
    }
}