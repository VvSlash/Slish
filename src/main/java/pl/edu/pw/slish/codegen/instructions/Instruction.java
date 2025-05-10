package pl.edu.pw.slish.codegen.instructions;

/**
 * Interfejs reprezentujący instrukcję kodu pośredniego (LLVM IR).
 */
public interface Instruction {
    /**
     * Generuje kod LLVM IR dla tej instrukcji.
     * @return Wygenerowany kod instrukcji.
     */
    String generateCode();
} 