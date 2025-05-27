package pl.edu.pw.slish.codegen.instructions;

public class RawInstruction implements Instruction {
    private final String code;

    public RawInstruction(String code) {
        this.code = code;
    }

    @Override
    public String generateCode() {
        return code;
    }
}