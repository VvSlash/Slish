package pl.edu.pw.slish.ast.expr;

import pl.edu.pw.slish.ast.NodeVisitor;

public class Literal implements Expression {
    private final Object value;
    private final Type type;

    public enum Type {
        INTEGER,
        FLOAT32, // NEW
        FLOAT64, // NEW
        STRING,
        BOOLEAN,
        NULL
    }

    public Literal(Object value, Type type) {
        this.value = value;
        this.type = type;
    }

    public Object getValue() {
        return value;
    }

    public Type getType() {
        return type;
    }

    @Override
    public String toString() {
        if (value == null) {
            return "null";
        }
        if (type == Type.STRING) {
            return "\"" + value + "\"";
        }
        return value.toString();
    }

    @Override
    public <T> T accept(NodeVisitor<T> visitor) {
        return visitor.visit(this);
    }
}