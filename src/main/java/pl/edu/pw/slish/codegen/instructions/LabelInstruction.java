package pl.edu.pw.slish.codegen.instructions;

/**
 * Instrukcja reprezentująca etykietę w kodzie LLVM IR.
 */
public class LabelInstruction implements Instruction {
    private final String label;
    
    public LabelInstruction(String label) {
        this.label = label;
    }
    
    @Override
    public String generateCode() {
        return label + ":";
    }
} 