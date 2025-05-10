package pl.edu.pw.slish.instructions;

import pl.edu.pw.slish.codegen.Type;

/**
 * Instrukcja alokacji zmiennej lokalnej (alloca) w LLVM IR
 */
public class AllocaInstruction implements Instruction {
    private final String resultName;
    private final Type type;
    
    public AllocaInstruction(String resultName, Type type) {
        this.resultName = resultName;
        this.type = type;
    }
    
    public String getResultName() {
        return resultName;
    }
    
    public Type getType() {
        return type;
    }
    
    @Override
    public String toLLVMCode() {
        return "%" + resultName + " = alloca " + type.toString();
    }
} 