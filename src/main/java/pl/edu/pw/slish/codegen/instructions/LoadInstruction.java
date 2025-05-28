package pl.edu.pw.slish.codegen.instructions;

import pl.edu.pw.slish.codegen.Type;

public class LoadInstruction implements Instruction {
    private final String resultRegister;
    private final String pointerRegister;
    private final Type type;

    public LoadInstruction(String resultRegister, String pointerRegister, Type type) {
        this.resultRegister = resultRegister;
        this.pointerRegister = pointerRegister;
        this.type = type;
    }

    @Override
    public String generateCode() {
        String llvmType = mapToLLVMType(type);
        return "%" + resultRegister + " = load " + llvmType + ", " + llvmType + "* %" + pointerRegister;
    }

    private String mapToLLVMType(Type type) {
        if (type.equals(Type.INTEGER)) return "i32";
        if (type.equals(Type.FLOAT)) return "double";
        if (type.equals(Type.BOOLEAN)) return "i1";
        if (type.equals(Type.STRING)) return "i8*";
        if (type.equals(Type.DYNAMIC)) return "i8*";
        if (type.isArray()) return mapToLLVMType(type.getElementType()) + "*";
        throw new IllegalArgumentException("Unsupported type in LoadInstruction: " + type);
    }
}
