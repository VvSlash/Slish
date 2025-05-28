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
        if (type.equals(Type.INTEGER)) return "i32";
        if (type.equals(Type.FLOAT)) return "double";
        if (type.equals(Type.BOOLEAN)) return "i1";
        if (type.equals(Type.STRING)) return "i8*";
        if (type.equals(Type.DYNAMIC)) return "i8*";
        if (type.isArray()) return mapToLLVMType(type.getElementType()) + "*";
        throw new IllegalArgumentException("Nieobsługiwany typ w StoreInstruction: " + type);
    }
}
