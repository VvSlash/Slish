package pl.edu.pw.slish.ast.stmt;

import pl.edu.pw.slish.ast.NodeVisitor;
import pl.edu.pw.slish.ast.expr.Expression;

/**
 * Reprezentuje instrukcję return w AST
 */
public class ReturnStatement implements Statement {
    private final Expression value;
    
    public ReturnStatement(Expression value) {
        this.value = value;
    }
    
    public Expression getValue() {
        return value;
    }
    
    /**
     * Sprawdza czy instrukcja return zwraca wartość.
     * 
     * @return true jeśli instrukcja zwraca wartość, false dla pustego return
     */
    public boolean hasValue() {
        return value != null;
    }
    
    @Override
    public String toString() {
        if (value != null) {
            return "/ret " + value;
        } else {
            return "/ret";
        }
    }
    
    @Override
    public <T> T accept(NodeVisitor<T> visitor) {
        return visitor.visit(this);
    }
} 