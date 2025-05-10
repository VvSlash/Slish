package pl.edu.pw.slish.instructions;

/**
 * Interfejs reprezentujący instrukcję w kodzie pośrednim (LLVM IR)
 */
public interface Instruction {
    /**
     * Zwraca reprezentację tekstową instrukcji w formacie LLVM IR
     * @return instrukcja w formacie LLVM IR
     */
    String toLLVMCode();
} 