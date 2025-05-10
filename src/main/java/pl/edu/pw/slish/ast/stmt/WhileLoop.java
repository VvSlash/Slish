package pl.edu.pw.slish.ast.stmt;

import pl.edu.pw.slish.ast.NodeVisitor;
import pl.edu.pw.slish.ast.expr.Expression;

/**
 * Reprezentuje pętlę while w AST
 */
public class WhileLoop implements Statement {
    private final Expression condition;
    private final Block body;
    
    public WhileLoop(Expression condition, Block body) {
        this.condition = condition;
        this.body = body;
    }
    
    public Expression getCondition() {
        return condition;
    }
    
    public Block getBody() {
        return body;
    }
    
    @Override
    public String toString() {
        return "/while(" + condition + ") " + body;
    }
    
    @Override
    public <T> T accept(NodeVisitor<T> visitor) {
        return visitor.visit(this);
    }
} 