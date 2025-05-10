package pl.edu.pw.slish.ast.expr;

import pl.edu.pw.slish.ast.NodeVisitor;

/**
 * Reprezentuje operację binarną w AST (np. dodawanie, mnożenie)
 */
public class BinaryOperation implements Expression {
    private final Expression left;
    private final Operator operator;
    private final Expression right;
    
    public enum Operator {
        ADD,
        SUBTRACT,
        MULTIPLY,
        DIVIDE,
        MODULO,
        AND,
        OR,
        XOR,
        GREATER_THAN,
        LESS_THAN,
        GREATER_EQUAL,
        LESS_EQUAL,
        EQUAL,
        NOT_EQUAL
    }
    
    public BinaryOperation(Expression left, Operator operator, Expression right) {
        this.left = left;
        this.operator = operator;
        this.right = right;
    }
    
    public Expression getLeft() {
        return left;
    }
    
    public Operator getOperator() {
        return operator;
    }
    
    public Expression getRight() {
        return right;
    }
    
    @Override
    public String toString() {
        String op;
        switch (operator) {
            case ADD: op = "+"; break;
            case SUBTRACT: op = "-"; break;
            case MULTIPLY: op = "*"; break;
            case DIVIDE: op = "//"; break;
            case MODULO: op = "%"; break;
            case AND: op = "and"; break;
            case OR: op = "or"; break;
            case XOR: op = "xor"; break;
            case GREATER_THAN: op = ">"; break;
            case LESS_THAN: op = "<"; break;
            case GREATER_EQUAL: op = ">="; break;
            case LESS_EQUAL: op = "<="; break;
            case EQUAL: op = "=="; break;
            case NOT_EQUAL: op = "!="; break;
            default: op = "?";
        }
        return "(" + left + " " + op + " " + right + ")";
    }
    
    @Override
    public <T> T accept(NodeVisitor<T> visitor) {
        return visitor.visit(this);
    }
} 