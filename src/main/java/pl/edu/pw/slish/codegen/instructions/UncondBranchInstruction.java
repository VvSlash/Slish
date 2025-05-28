package pl.edu.pw.slish.codegen.instructions;

/**
 * Bezwarunkowy branch: br label %label
 */
public class UncondBranchInstruction implements Instruction {
    private final String label;

    public UncondBranchInstruction(String label) {
        this.label = label;
    }

    @Override
    public String generateCode() {
        return "br label %" + label;
    }
}
