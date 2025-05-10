package pl.edu.pw.slish.ast.stmt;

import pl.edu.pw.slish.ast.stmt.Block;
import pl.edu.pw.slish.ast.NodeVisitor;
import pl.edu.pw.slish.ast.PipeElementNode;

/**
 * Reprezentuje instrukcję warunkową "if" w potoku (`... | /if = { ... }`).
 * Warunek dla tej instrukcji będzie pochodził z wyniku poprzedniego elementu potoku.
 * Nie ma tutaj bloku "else".
 */
public class IfPipeStatement implements Statement, PipeElementNode {
    private final Block thenBlock;

    public IfPipeStatement(Block thenBlock) {
        if (thenBlock == null) {
            throw new IllegalArgumentException("Then block cannot be null for IfPipeStatement");
        }
        this.thenBlock = thenBlock;
    }

    public Block getThenBlock() {
        return thenBlock;
    }

    @Override
    public <T> T accept(NodeVisitor<T> visitor) {
        // Wymaga implementacji metody visit(IfPipeStatement) w NodeVisitor
        return visitor.visit(this);
    }

    @Override
    public String toString() {
        return "/if = " + thenBlock.toString();
    }
}
