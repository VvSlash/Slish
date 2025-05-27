// Updated LoadConstantInstruction.java
package pl.edu.pw.slish.codegen.instructions;

import static pl.edu.pw.slish.codegen.Type.BOOLEAN;
import static pl.edu.pw.slish.codegen.Type.DYNAMIC;
import static pl.edu.pw.slish.codegen.Type.FLOAT;
import static pl.edu.pw.slish.codegen.Type.INTEGER;
import static pl.edu.pw.slish.codegen.Type.STRING;

import pl.edu.pw.slish.codegen.Type;

/**
 * Instrukcja wczytania wartości stałej numerycznej w kodzie LLVM IR.
 * (Stringy obsługuje StringConstantManager.)
 */
public class LoadConstantInstruction implements Instruction {
    private final String register;
    private final Object value;
    private final Type type;

    public LoadConstantInstruction(String register, Object value, Type type) {
        this.register = register;
        this.value = value;
        this.type = type;
    }

    @Override
    public String generateCode() {
        String llvmType = mapToLLVMType(type);

        if (type.equals(INTEGER)) {
            return "%" + register + " = add i32 0, " + value;
        } else if (type.equals(FLOAT)) {
            return "%" + register + " = fadd double 0.0, " + value;
        } else if (type.equals(BOOLEAN)) {
            int boolVal = Boolean.TRUE.equals(value) ? 1 : 0;
            return "%" + register + " = add i1 0, " + boolVal;
        }// Should never happen: strings are handled by StringConstantManager

        throw new IllegalArgumentException(
            "LoadConstantInstruction only supports numeric/boolean constants, got " + type);
    }

    private String mapToLLVMType(Type type) {
        if (type == INTEGER)   return "i32";
        if (type == FLOAT)     return "double";
        if (type == BOOLEAN)   return "i1";
        if (type == DYNAMIC)   return "i8*";
        if (type.isArray())         return mapToLLVMType(type.getElementType()) + "*";
        throw new IllegalArgumentException("Unsupported type in LoadConstantInstruction: " + type);
    }
}