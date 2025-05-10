package pl.edu.pw.slish.ast.expr;

import pl.edu.pw.slish.ast.NodeVisitor;

import java.util.List;

/**
 * Reprezentuje literał tablicowy w AST
 */
public class ArrayLiteral implements Expression {
    private final List<Expression> elements;
    
    public ArrayLiteral(List<Expression> elements) {
        this.elements = elements;
    }
    
    public List<Expression> getElements() {
        return elements;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (int i = 0; i < elements.size(); i++) {
            sb.append(elements.get(i));
            if (i < elements.size() - 1) {
                sb.append(", ");
            }
        }
        sb.append("]");
        return sb.toString();
    }
    
    @Override
    public <T> T accept(NodeVisitor<T> visitor) {
        return visitor.visit(this);
    }
} 