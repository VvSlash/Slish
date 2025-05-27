package pl.edu.pw.slish.ast.stmt;

import java.util.List;
import pl.edu.pw.slish.ast.NodeVisitor;
import pl.edu.pw.slish.ast.expr.Expression;

/**
 * Reprezentuje instrukcję print w AST
 */
public class PrintStatement implements Statement {

    private final List<Expression> arguments;

    public PrintStatement(List<Expression> arguments) {
        this.arguments = arguments;
    }

    public List<Expression> getArguments() {
        return arguments;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("/print(");
        for (int i = 0; i < arguments.size(); i++) {
            sb.append(arguments.get(i));
            if (i < arguments.size() - 1) {
                sb.append(", ");
            }
        }
        sb.append(")");
        return sb.toString();
    }

    @Override
    public <T> T accept(NodeVisitor<T> visitor) {
        return visitor.visit(this);
    }
}