package pl.edu.pw.slish.ast.stmt;

import pl.edu.pw.slish.ast.NodeVisitor;

import java.util.List;

/**
 * Reprezentuje blok kodu w AST
 */
public class Block implements Statement {
    private final List<Statement> statements;
    
    public Block(List<Statement> statements) {
        this.statements = statements;
    }
    
    public List<Statement> getStatements() {
        return statements;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{\n");
        for (Statement stmt : statements) {
            sb.append("  ").append(stmt).append("\n");
        }
        sb.append("}");
        return sb.toString();
    }
    
    @Override
    public <T> T accept(NodeVisitor<T> visitor) {
        return visitor.visit(this);
    }
} 