package pl.edu.pw.slish.ast.stmt;

import pl.edu.pw.slish.ast.NodeVisitor;
import pl.edu.pw.slish.ast.expr.Expression;

/**
 * Reprezentuje deklarację zmiennej w AST
 */
public class Declaration implements Statement {
    private final String type;
    private final String name;
    private final Expression initializer;
    
    public Declaration(String type, String name, Expression initializer) {
        this.type = type;
        this.name = name;
        this.initializer = initializer;
    }
    
    public String getType() {
        return type;
    }
    
    public String getName() {
        return name;
    }
    
    public Expression getInitializer() {
        return initializer;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("/").append(type).append(" ").append(name);
        if (initializer != null) {
            sb.append(" = ").append(initializer);
        }
        return sb.toString();
    }
    
    @Override
    public <T> T accept(NodeVisitor<T> visitor) {
        return visitor.visit(this);
    }
} 