package pl.edu.pw.slish.codegen.instructions;

/**
 * Instrukcja warunkowego skoku w kodzie LLVM IR.
 */
public class ConditionalJumpInstruction implements Instruction {
    private final String conditionRegister;
    private final String trueLabel;
    private final String falseLabel;
    
    public ConditionalJumpInstruction(String conditionRegister, String trueLabel, String falseLabel) {
        this.conditionRegister = conditionRegister;
        this.trueLabel = trueLabel;
        this.falseLabel = falseLabel;
    }
    
    @Override
    public String generateCode() {
        return "br i1 %" + conditionRegister + ", label %" + trueLabel + ", label %" + falseLabel;
    }
} 