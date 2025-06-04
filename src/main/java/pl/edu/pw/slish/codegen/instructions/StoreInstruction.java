package pl.edu.pw.slish.codegen.instructions;

import pl.edu.pw.slish.codegen.Type;

public class StoreInstruction implements Instruction {
    private final String valueRegister;
    private final String pointerRegister;
    private final Type type;

    public StoreInstruction(String valueRegister, String pointerRegister, Type type) {
        this.valueRegister = valueRegister;
        this.pointerRegister = pointerRegister;
        this.type = type;
    }

    @Override
    public String generateCode() {
        String llvmType = mapToLLVMType(type);
        return "store " + llvmType + " %" + valueRegister + ", " + llvmType + "* %" + pointerRegister;
    }

    private String mapToLLVMType(Type type) {
        if (type == null) return "i32"; // Fallback, should not happen
        if (type == Type.INTEGER) return "i32";
        if (type == Type.FLOAT32) return "float"; // LLVM 'float' is 32-bit
        if (type == Type.FLOAT64) return "double"; // LLVM 'double' is 64-bit
        if (type == Type.BOOLEAN) return "i1";
        if (type == Type.STRING) return "i8*"; // Pointer to char
        if (type == Type.VOID) return "void";
        if (type.isArray()) return mapToLLVMType(type.getElementType()) + "*";
        if (type == Type.DYNAMIC) return "i8*"; // Or handle appropriately
        throw new IllegalArgumentException("Unsupported type for LLVM mapping: " + type.getTypeName());
    }
}
