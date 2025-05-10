package pl.edu.pw.slish.ast.stmt;

import pl.edu.pw.slish.ast.NodeVisitor;
import pl.edu.pw.slish.ast.PipeElementNode;

/**
 * Reprezentuje instrukcję "return" w potoku (`... | /ret`).
 * Wartość do zwrócenia będzie pochodzić z wyniku poprzedniego elementu potoku.
 */
public class ReturnPipeStatement implements Statement, PipeElementNode {
    
    public ReturnPipeStatement() {
        // Konstruktor może być pusty
    }

    @Override
    public <T> T accept(NodeVisitor<T> visitor) {
        // Wymaga implementacji metody visit(ReturnPipeStatement) w NodeVisitor
        return visitor.visit(this);
    }

    @Override
    public String toString() {
        return "/ret";
    }
}
