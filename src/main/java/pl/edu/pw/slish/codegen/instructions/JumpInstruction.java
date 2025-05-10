package pl.edu.pw.slish.codegen.instructions;

/**
 * Instrukcja bezwarunkowego skoku w kodzie LLVM IR.
 */
public class JumpInstruction implements Instruction {
    private final String targetLabel;
    
    public JumpInstruction(String targetLabel) {
        this.targetLabel = targetLabel;
    }
    
    @Override
    public String generateCode() {
        return "br label %" + targetLabel;
    }
} 