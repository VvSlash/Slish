package pl.edu.pw.slish.ast;

/**
 * Klasa Program w pakiecie ast.
 * Jest to alias do klasy Program z pakietu stmt.
 */
public class Program extends pl.edu.pw.slish.ast.stmt.Program {
    /**
     * Konstruktor delegujący do klasy Program z pakietu stmt
     * @param statements lista instrukcji w programie
     */
    public Program(java.util.List<Node> statements) {
        super(statements);
    }
} 