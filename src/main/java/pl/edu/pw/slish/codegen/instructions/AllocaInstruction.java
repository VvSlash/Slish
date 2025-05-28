package pl.edu.pw.slish.codegen.instructions;

import pl.edu.pw.slish.codegen.Type;

/**
 * Instrukcja alokacji zmiennej na stosie.
 */
public class AllocaInstruction implements Instruction {

    private final String register;
    private final Type type;

    public AllocaInstruction(String register, Type type) {
        this.register = register;
        this.type = type;
    }

    @Override
    public String generateCode() {
        // mapToLLVMType should be the same helper you use elsewhere
        /* default */
        return "%" + register +
            " = alloca " + mapToLLVMType(type) +
            ", align " + (type == Type.BOOLEAN ? 1 :
            type == Type.INTEGER ? 4 :
                8);
    }

    private String mapToLLVMType(Type type) {
        if (type == Type.INTEGER) {
            return "i32";
        }
        if (type == Type.FLOAT) {
            return "double";
        }
        if (type == Type.BOOLEAN) {
            return "i1";
        }
        if (type == Type.STRING) {
            return "i8*";
        }
        if (type == Type.DYNAMIC) {
            return "i8*";
        }
        if (type.isArray()) {
            return mapToLLVMType(type.getElementType()) + "*";
        }
        throw new IllegalArgumentException("Unsupported type: " + type);
    }
} 