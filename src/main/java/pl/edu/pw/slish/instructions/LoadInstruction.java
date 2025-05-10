package pl.edu.pw.slish.instructions;

import pl.edu.pw.slish.codegen.Type;

/**
 * Instrukcja wczytania wartości ze zmiennej (load) w LLVM IR
 */
public class LoadInstruction implements Instruction {
    private final String resultName;
    private final Type type;
    private final String pointerName;
    
    public LoadInstruction(String resultName, Type type, String pointerName) {
        this.resultName = resultName;
        this.type = type;
        this.pointerName = pointerName;
    }
    
    public String getResultName() {
        return resultName;
    }
    
    public Type getType() {
        return type;
    }
    
    public String getPointerName() {
        return pointerName;
    }
    
    @Override
    public String toLLVMCode() {
        return "%" + resultName + " = load " + type.toString() + ", " + 
               type.toString() + "* %" + pointerName;
    }
} 