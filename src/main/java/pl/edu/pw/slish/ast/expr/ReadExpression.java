package pl.edu.pw.slish.ast.expr;

import pl.edu.pw.slish.ast.NodeVisitor;
import pl.edu.pw.slish.codegen.Type;

/**
 * Reprezentuje wyrażenie wejścia w AST (np. /read).
 */
public class ReadExpression implements Expression {
    private Type expectedType;

    public ReadExpression() {
        // Domyślny typ to dynamiczny
        this.expectedType = null;
    }
    
    /**
     * Ustawia oczekiwany typ dla wartości wczytanej przez read.
     * 
     * @param expectedType Oczekiwany typ wczytywanej wartości
     */
    public void setExpectedType(Type expectedType) {
        this.expectedType = expectedType;
    }
    
    /**
     * Zwraca oczekiwany typ dla wartości wczytanej przez read.
     * 
     * @return Oczekiwany typ lub null jeśli nie został określony
     */
    public Type getExpectedType() {
        return expectedType;
    }

    @Override
    public <T> T accept(NodeVisitor<T> visitor) {
        return visitor.visit(this);
    }
    
    @Override
    public String toString() {
        return "/read";
    }
} 