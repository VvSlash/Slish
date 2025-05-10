package pl.edu.pw.slish.ast.expr;

import pl.edu.pw.slish.ast.NodeVisitor;

/**
 * Reprezentuje dostęp do elementu tablicy w AST
 */
public class ArrayAccess implements Expression {
    private final String arrayName;
    private final Expression index;
    
    public ArrayAccess(String arrayName, Expression index) {
        this.arrayName = arrayName;
        this.index = index;
    }
    
    public String getArrayName() {
        return arrayName;
    }
    
    public Expression getIndex() {
        return index;
    }
    
    @Override
    public String toString() {
        return arrayName + "[" + index + "]";
    }
    
    @Override
    public <T> T accept(NodeVisitor<T> visitor) {
        return visitor.visit(this);
    }
} 