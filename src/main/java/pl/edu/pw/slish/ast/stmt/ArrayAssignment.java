package pl.edu.pw.slish.ast.stmt;

import pl.edu.pw.slish.ast.expr.Expression;
import pl.edu.pw.slish.ast.NodeVisitor;
import pl.edu.pw.slish.ast.expr.Variable;

public class ArrayAssignment implements Statement, Expression {
    private final String arrayName;
    private final Expression index;
    private final Expression value;

    public ArrayAssignment(Variable arrayName, Expression index, Expression value) {
        this.arrayName = arrayName.getName();
        this.index = index;
        this.value = value;
    }

    public String getArrayName() {
        return arrayName;
    }

    public Expression getIndex() {
        return index;
    }

    public Expression getValue() {
        return value;
    }

    @Override
    public <T> T accept(NodeVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toString() {
        return arrayName + "[" + index + "] = " + value;
    }
}
