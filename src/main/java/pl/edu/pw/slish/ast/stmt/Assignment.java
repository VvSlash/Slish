package pl.edu.pw.slish.ast.stmt;

import pl.edu.pw.slish.ast.NodeVisitor;
import pl.edu.pw.slish.ast.expr.Expression;
import pl.edu.pw.slish.ast.expr.Variable;

/**
 * Reprezentuje przypisanie wartości do zmiennej w AST
 */
public class Assignment implements Statement {
    private final Variable target;
    private final Expression value;
    
    public Assignment(Variable target, Expression value) {
        this.target = target;
        this.value = value;
    }
    
    public Variable getTarget() {
        return target;
    }
    
    public Expression getValue() {
        return value;
    }
    
    @Override
    public String toString() {
        return target + " = " + value;
    }
    
    @Override
    public <T> T accept(NodeVisitor<T> visitor) {
        return visitor.visit(this);
    }
} 