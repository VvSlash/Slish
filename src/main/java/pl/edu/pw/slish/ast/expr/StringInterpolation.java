package pl.edu.pw.slish.ast.expr;

import pl.edu.pw.slish.ast.NodeVisitor;

import java.util.Map;

/**
 * Reprezentuje interpolację stringów w AST
 */
public class StringInterpolation implements Expression {
    private final String template;
    private final Map<String, Expression> variables;
    
    public StringInterpolation(String template, Map<String, Expression> variables) {
        this.template = template;
        this.variables = variables;
    }
    
    public String getTemplate() {
        return template;
    }
    
    public Map<String, Expression> getVariables() {
        return variables;
    }
    
    @Override
    public String toString() {
        return template;
    }
    
    @Override
    public <T> T accept(NodeVisitor<T> visitor) {
        return visitor.visit(this);
    }
} 