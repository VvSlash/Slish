package pl.edu.pw.slish.ast.expr;

import pl.edu.pw.slish.ast.NodeVisitor;

import java.util.List;

/**
 * Reprezentuje wywołanie funkcji w AST
 */
public class FunctionCall implements Expression {
    private final String name;
    private final List<Expression> arguments;
    
    public FunctionCall(String name, List<Expression> arguments) {
        this.name = name;
        this.arguments = arguments;
    }
    
    public String getName() {
        return name;
    }
    
    /**
     * Zwraca nazwę wywoływanej funkcji.
     * 
     * @return Nazwa funkcji
     */
    public String getFunctionName() {
        return name;
    }
    
    public List<Expression> getArguments() {
        return arguments;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("/").append(name).append("(");
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