package pl.edu.pw.slish.ast;

/**
 * Bazowy interfejs dla wszystkich węzłów w drzewie AST
 */
public interface Node {
    /**
     * Implementacja wzorca Visitor
     * @param visitor wizytator odwiedzający węzeł
     * @param <T> typ zwracany przez wizytatora
     * @return wynik odwiedzenia węzła
     */
    <T> T accept(NodeVisitor<T> visitor);
} 