package pl.edu.pw.slish.ast.stmt;

import pl.edu.pw.slish.ast.Node;
import pl.edu.pw.slish.ast.NodeVisitor;
import pl.edu.pw.slish.ast.expr.Expression;

/**
 * Reprezentuje pętlę for w AST (standardową lub foreach)
 */
public class ForLoop implements Statement {
    private final Node initialization; // może być Declaration, Assignment lub null
    private final Expression condition;
    private final Expression iteration;
    private final Block body;
    private final Expression collection; // dla foreach
    
    // Standardowa pętla for
    public ForLoop(Node initialization, Expression condition, Expression iteration, Block body) {
        this.initialization = initialization;
        this.condition = condition;
        this.iteration = iteration;
        this.body = body;
        this.collection = null;
    }
    
    // Pętla foreach
    public ForLoop(Expression collection, Block body) {
        this.initialization = null;
        this.condition = null;
        this.iteration = null;
        this.body = body;
        this.collection = collection;
    }
    
    public Node getInitialization() {
        return initialization;
    }
    
    public Expression getCondition() {
        return condition;
    }
    
    public Expression getIteration() {
        return iteration;
    }
    
    public Block getBody() {
        return body;
    }
    
    public Expression getCollection() {
        return collection;
    }
    
    public boolean isForeach() {
        return collection != null;
    }
    
    /**
     * Sprawdza czy jest to pętla typu for-each (iteracja po kolekcji).
     * 
     * @return true jeśli pętla jest typu for-each, false w przeciwnym przypadku
     */
    public boolean isForEach() {
        return initialization == null && iteration == null && collection != null;
    }
    
    @Override
    public String toString() {
        if (isForeach()) {
            return collection + " | /for = " + body;
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append("/for(");
            sb.append(initialization != null ? initialization : "");
            sb.append("; ");
            sb.append(condition != null ? condition : "");
            sb.append("; ");
            sb.append(iteration != null ? iteration : "");
            sb.append(") ");
            sb.append(body);
            return sb.toString();
        }
    }
    
    @Override
    public <T> T accept(NodeVisitor<T> visitor) {
        return visitor.visit(this);
    }
} 