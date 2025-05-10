package pl.edu.pw.slish.codegen.instructions;

import pl.edu.pw.slish.codegen.Type;

/**
 * Instrukcja zapisania wartości do zmiennej.
 */
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
        return "store " + type.toString() + " %" + valueRegister + ", " + 
               type.toString() + "* %" + pointerRegister;
    }
} 