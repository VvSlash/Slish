package pl.edu.pw.slish.ast.expr;

import pl.edu.pw.slish.ast.NodeVisitor;
import pl.edu.pw.slish.ast.PipeElementNode;

import java.util.List;

/**
 * Reprezentuje wyrażenie z potokami (pipe) w AST
 */
public class PipeExpression implements Expression {
    private final List<PipeElementNode> elements;
    
    public PipeExpression(List<PipeElementNode> elements) {
        this.elements = elements;
    }
    
    public List<PipeElementNode> getElements() {
        return elements;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < elements.size(); i++) {
            sb.append(elements.get(i).toString());
            if (i < elements.size() - 1) {
                sb.append(" | ");
            }
        }
        return sb.toString();
    }
    
    @Override
    public <T> T accept(NodeVisitor<T> visitor) {
        return visitor.visit(this);
    }
} 