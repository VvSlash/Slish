package pl.edu.pw.slish.ast.stmt;

import pl.edu.pw.slish.ast.Node;
import pl.edu.pw.slish.ast.NodeVisitor;

import java.util.List;

/**
 * Reprezentuje deklarację funkcji w AST.
 */
public class FunctionDeclaration implements Statement {
    private final String name;
    private final String returnType;
    private final List<Declaration> parameters;
    private final Block body;

    /**
     * Tworzy nową deklarację funkcji.
     * 
     * @param name Nazwa funkcji
     * @param returnType Typ zwracany przez funkcję
     * @param parameters Lista parametrów funkcji
     * @param body Ciało funkcji (blok kodu)
     */
    public FunctionDeclaration(String name, String returnType, List<Declaration> parameters, Block body) {
        this.name = name;
        this.returnType = returnType;
        this.parameters = parameters;
        this.body = body;
    }

    /**
     * Zwraca nazwę funkcji.
     */
    public String getName() {
        return name;
    }

    /**
     * Zwraca typ zwracany przez funkcję.
     */
    public String getReturnType() {
        return returnType;
    }

    /**
     * Zwraca listę parametrów funkcji.
     */
    public List<Declaration> getParameters() {
        return parameters;
    }

    /**
     * Zwraca ciało funkcji.
     */
    public Block getBody() {
        return body;
    }

    @Override
    public <T> T accept(NodeVisitor<T> visitor) {
        return visitor.visit(this);
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("/fun ").append(name).append("(");
        for (int i = 0; i < parameters.size(); i++) {
            if (i > 0) sb.append(", ");
            sb.append("/").append(parameters.get(i).getType()).append(" ").append(parameters.get(i).getName());
        }
        sb.append(") = ");
        sb.append(body);
        return sb.toString();
    }
} 