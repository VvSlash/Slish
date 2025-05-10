package pl.edu.pw.slish.instructions;

import pl.edu.pw.slish.codegen.Type;

/**
 * Instrukcja przypisania wartości do zmiennej (store) w LLVM IR
 */
public class StoreInstruction implements Instruction {
    private final String valueName;
    private final String pointerName;
    private final Type type;
    private final boolean isGlobal;
    
    public StoreInstruction(String valueName, String pointerName, Type type, boolean isGlobal) {
        this.valueName = valueName;
        this.pointerName = pointerName;
        this.type = type;
        this.isGlobal = isGlobal;
    }
    
    public String getValueName() {
        return valueName;
    }
    
    public String getPointerName() {
        return pointerName;
    }
    
    public Type getType() {
        return type;
    }
    
    public boolean isGlobal() {
        return isGlobal;
    }
    
    @Override
    public String toLLVMCode() {
        String valueRef = valueName.startsWith("%") || valueName.startsWith("@") ? 
                         valueName : "%" + valueName;
        String pointerRef = isGlobal ? "@" + pointerName : "%" + pointerName;
        
        return "store " + type.toString() + " " + valueRef + ", " + 
               type.toString() + "* " + pointerRef;
    }
} 