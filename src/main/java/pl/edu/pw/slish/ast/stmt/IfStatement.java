package pl.edu.pw.slish.ast.stmt;

import pl.edu.pw.slish.ast.Node;
import pl.edu.pw.slish.ast.NodeVisitor;
import pl.edu.pw.slish.ast.expr.Expression;

/**
 * Reprezentuje instrukcję warunkową if-else w AST
 */
public class IfStatement implements Statement {
    private final Expression condition;
    private final Block thenBlock;
    private final Block elseBlock;
    
    /**
     * Tworzy nową instrukcję if-else.
     * 
     * @param condition Warunek
     * @param thenBlock Blok kodu wykonywany, gdy warunek jest prawdziwy
     * @param elseBlock Blok kodu wykonywany, gdy warunek jest fałszywy (może być null)
     */
    public IfStatement(Expression condition, Block thenBlock, Block elseBlock) {
        this.condition = condition;
        this.thenBlock = thenBlock;
        this.elseBlock = elseBlock;
    }
    
    /**
     * Zwraca warunek instrukcji if.
     */
    public Expression getCondition() {
        return condition;
    }
    
    /**
     * Zwraca blok kodu wykonywany, gdy warunek jest prawdziwy.
     */
    public Block getThenBlock() {
        return thenBlock;
    }
    
    /**
     * Zwraca blok kodu wykonywany, gdy warunek jest fałszywy.
     */
    public Block getElseBlock() {
        return elseBlock;
    }
    
    /**
     * Sprawdza czy instrukcja if posiada blok else.
     * 
     * @return true jeśli instrukcja posiada blok else, false w przeciwnym przypadku
     */
    public boolean hasElse() {
        return elseBlock != null;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("/if(").append(condition).append(") ").append(thenBlock);
        if (elseBlock != null) {
            sb.append(" /else ");
            sb.append(elseBlock);
        }
        return sb.toString();
    }
    
    @Override
    public <T> T accept(NodeVisitor<T> visitor) {
        return visitor.visit(this);
    }
} 