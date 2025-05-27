package pl.edu.pw.slish.codegen.instructions;

import pl.edu.pw.slish.codegen.Type;

/**
 * Warunkowy branch: br i1 %cond, label %thenLabel, label %elseLabel
 */
public class BranchInstruction implements Instruction {
    private final String condReg;
    private final String thenLabel;
    private final String elseLabel;

    public BranchInstruction(String condReg, String thenLabel, String elseLabel) {
        this.condReg   = condReg;
        this.thenLabel = thenLabel;
        this.elseLabel = elseLabel;
    }

    @Override
    public String generateCode() {
        return "br i1 " + (condReg.startsWith("%") ? condReg : "%" + condReg)
            + ", label %" + thenLabel
            + ", label %" + elseLabel;
    }
}
