package pl.edu.pw.slish.ast.stmt;

import pl.edu.pw.slish.ast.Node;
import pl.edu.pw.slish.ast.NodeVisitor;

import java.util.List;

/**
 * Reprezentuje cały program Slish w AST
 */
public class Program implements Node {
    private final List<Node> statements;
    
    public Program(List<Node> statements) {
        this.statements = statements;
    }
    
    public List<Node> getStatements() {
        return statements;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Node stmt : statements) {
            sb.append(stmt).append("\n");
        }
        return sb.toString();
    }
    
    @Override
    public <T> T accept(NodeVisitor<T> visitor) {
        return visitor.visit(this);
    }
} 