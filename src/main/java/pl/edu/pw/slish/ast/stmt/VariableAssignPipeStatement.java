package pl.edu.pw.slish.ast.stmt;

import pl.edu.pw.slish.ast.NodeVisitor;
import pl.edu.pw.slish.ast.PipeElementNode;

/**
 * Reprezentuje instrukcję przypisania wartości z potoku do zmiennej (`type? varName = _`).
 * Wartość do przypisania będzie pochodzić z poprzedniego elementu potoku.
 */
public class VariableAssignPipeStatement implements Statement, PipeElementNode {
    private final String variableName;
    private final String declaredType; // Może być null, jeśli typ jest dynamiczny ('_')

    public VariableAssignPipeStatement(String declaredType, String variableName) {
        if (variableName == null || variableName.trim().isEmpty()) {
            throw new IllegalArgumentException("Variable name cannot be null or empty");
        }
        this.variableName = variableName;
        this.declaredType = declaredType; // np. "int", "float", lub null dla '_'
    }

    public String getVariableName() {
        return variableName;
    }

    public String getDeclaredType() {
        return declaredType;
    }

    @Override
    public <T> T accept(NodeVisitor<T> visitor) {
        // Wymaga implementacji metody visit(VariableAssignPipeStatement) w NodeVisitor
        return visitor.visit(this);
    }

    @Override
    public String toString() {
        return (declaredType != null ? declaredType + " " : "") + variableName + " = _";
    }
}
