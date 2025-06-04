package pl.edu.pw.slish.codegen.instructions;

import pl.edu.pw.slish.codegen.Type;

/**
 * Generates a proper printf call for printing values call i32 (i8*, ...) @printf(i8* <ptr>)
 */
public class PrintInstruction implements Instruction {

    private final String formatPtrReg;
    private final String valueReg;
    private final Type valueType;

    public PrintInstruction(String formatPtrReg, String valueReg, Type valueType) {
        // Ensure both registers start with '%'
        this.formatPtrReg = formatPtrReg.startsWith("%") ? formatPtrReg : "%" + formatPtrReg;
        this.valueReg = valueReg.startsWith("%") ? valueReg : "%" + valueReg;
        this.valueType = valueType;
    }

    @Override
    public String generateCode() {
        return String.format("call i32 (i8*, ...) @printf(i8* %s, %s %s)",
            formatPtrReg,
            getLLVMType(valueType),
            valueReg
        );
    }

    private String getLLVMType(Type type) {
        if (type == Type.INTEGER) {
            return "i32";
        }
        if (type == Type.FLOAT32) { // Handle FLOAT32
            return "float";
        }
        if (type == Type.FLOAT64) { // Handle FLOAT64
            return "double";
        }
        if (type == Type.STRING) {
            return "i8*";
        }
        if (type == Type.BOOLEAN) {
            return "i1";
        }
        return null;
    }
}