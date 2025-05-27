package pl.edu.pw.slish.codegen.instructions;

/**
 * Generates a proper printf call for printing values call i32 (i8*, ...) @printf(i8* <ptr>)
 */
public class PrintInstruction implements Instruction {

    private final String ptrReg;

    public PrintInstruction(String ptrReg) {
        // Ensure register has % prefix for LLVM IR
        this.ptrReg = ptrReg.startsWith("%") ? ptrReg : "%" + ptrReg;
    }

    @Override
    public String generateCode() {
        // Generate the actual printf call
        return "call i32 (i8*, ...) @printf(i8* " + ptrReg + ")";
    }
}